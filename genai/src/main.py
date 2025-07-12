# Setup logging - will be used by all modules
import logging
logging.basicConfig(level=logging.INFO, format='%(asctime)s %(levelname)s %(name)s %(message)s')

import os
from dotenv import load_dotenv
from fastapi import FastAPI, HTTPException, Request
from fastapi.responses import JSONResponse
import httpx
from contextlib import asynccontextmanager
from langchain_openai import OpenAIEmbeddings
from fastapi.middleware.cors import CORSMiddleware


from .utils.vector_db_test import test_weaviate_connection
from .services.crawler import crawler_service
from .services.crawler.schemas import CrawlRequest, CrawlResponse
from .services.embedding import embedder_service
from .services.embedding.schemas import EmbedRequest, EmbedResponse, QueryRequest, QueryResponse, DocumentResult
from .services.embedding.weaviate_service import get_weaviate_client, ensure_schema_exists, DOCUMENT_CLASS_NAME
from .services.llm import llm_service
from .services.llm.schemas import GenerateRequest, GenerateResponse
from .services.rag.schemas import CourseGenerationRequest, Course
from .services.rag import course_generator
from .utils.error_schema import ErrorResponse
from .utils.handle_httpx_exception import handle_httpx_exception

from .services.scheduler.schemas import SchedulerStatus, SchedulerControl
from .services.scheduler import start_scheduler, stop_scheduler, get_scheduler_status



# --- Configuration ---
load_dotenv()
logger = logging.getLogger("skillforge.genai")

APP_PORT = int(os.getenv("GENAI_PORT", "8082"))
APP_TITLE = os.getenv("GENAI_APP_NAME", "SkillForge GenAI Service")
APP_VERSION = os.getenv("GENAI_APP_VERSION", "0.0.1")
APP_DESCRIPTION = (
    "SkillForge GenAI Service provides endpoints for web crawling, "
    "chunking, embedding, semantic querying, and text generation using LLMs. "
    "Ideal for integrating vector search and AI-driven workflows."
)
API_PREFIX = "/api/v1"
TAGS_METADATA = [
    {"name": "System", "description": "Health checks and system status."},
    {"name": "Crawler", "description": "Crawl and clean website content."},
    {"name": "Embedder", "description": "Chunk and embed text to vector DB."},
    {"name": "LLM", "description": "Language Model completions and chat."},
]

# -------------------------------
# --- Lifespan Manager (for startup) ---
# -------------------------------
@asynccontextmanager
async def lifespan(app: FastAPI):
   logger.info("Service starting up...")
   try:
       # Example: Create client connections here
       client = get_weaviate_client()
       logger.info("Testing Weaviate connection...")
       test_weaviate_connection()
       logger.info("Ensuring Weaviate schema exists...")
       ensure_schema_exists(client)
       logger.info("Everything is set up successfully and ready to go!")
      
       # Start the blog embedder scheduler
       logger.info("Starting blog embedder scheduler...")
       start_scheduler()
      
   except Exception as e:
       logger.error(f"Error during startup: {e}", exc_info=True)
       raise RuntimeError("Failed during application startup.") from e
   yield
   logger.info("Service shutting down...")
  
   # Stop the scheduler on shutdown
   logger.info("Stopping blog embedder scheduler...")
   stop_scheduler()

# --- App Initialization ---
app = FastAPI(
    title=APP_TITLE,
    version=APP_VERSION,
    description=APP_DESCRIPTION,
    lifespan=lifespan,
    docs_url="/docs",
    openapi_url="/openapi.json",
    contact={
        "name": "SkillForge AI Team",
        "url": "https://github.com/AET-DevOps25/team-git-it-together",
    },
    license_info={
        "name": "MIT",
        "url": "https://opensource.org/licenses/MIT",
    },
    openapi_tags=TAGS_METADATA,
    root_path=os.getenv("API_ROOT_PATH", ""),
)

app.add_middleware(
    CORSMiddleware,
    allow_origins=os.getenv("CORS_ALLOW_ORIGINS", "*").split(","),
    allow_credentials=True,
    allow_methods=["*"],
    allow_headers=["*"],
)

# --- Custom Exception Handler for 422 & 500 errors ---
@app.exception_handler(HTTPException)
async def http_exception_handler(request: Request, exc: HTTPException):
    logger.error(f"HTTPException: {exc.detail}")
    return JSONResponse(
        status_code=exc.status_code,
        content={"error": "HTTPException", "detail": exc.detail}
    )

@app.exception_handler(Exception)
async def unhandled_exception_handler(request: Request, exc: Exception):
    logger.exception("Unhandled exception occurred")
    return JSONResponse(
        status_code=500,
        content={"error": "InternalServerError", "detail": str(exc)}
    )

# ---- System Endpoints --------
# -------------------------------
@app.get(f"{API_PREFIX}/health", tags=["System"])
async def health():
    """
    Deep health check. Verifies the application and its core dependencies (e.g., DB, vector store).
    Use this for readiness/liveness probes in Kubernetes.
    """
    try:
        test_weaviate_connection()
        return {"status": "ok", "message": "Service and DB are healthy."}
    except Exception as e:
        logger.error(f"Health check failed: {e}", exc_info=True)
        return JSONResponse(
            status_code=500,
            content={"status": "error", "message": "Dependency failure. See logs for details."}
        )

@app.get(f"{API_PREFIX}/ping", tags=["System"])
async def ping():
    """
    Lightweight liveness check. Confirms the API process is running, but does not check dependencies.
    """
    logger.info("Ping received.")
    return {"message": "pong", "status": "ok"}

from fastapi.responses import JSONResponse

# -------------------------------
# ----- Crawler endpoints -----
# -------------------------------
@app.post(f"{API_PREFIX}/crawl", response_model=CrawlResponse, responses={400: {"model": ErrorResponse}, 500: {"model": ErrorResponse}}, tags=["Crawler"])
async def crawl(request: CrawlRequest):
    url = str(request.url)
    try:
        page = await crawler_service.fetch_and_clean_page(url)
        if page:
            page.setdefault("message", "Page crawled successfully.")
            return page
        
    except (httpx.HTTPStatusError, httpx.RequestError, httpx.TimeoutException) as e:
        logger.error(f"HTTP error while crawling {url}: {e}")
        raise handle_httpx_exception(url, e)
    except ValueError as e:
        # Empty content or invalid URL, often a 400 or 404
        logger.warning(f"Value error crawling {url}: {e}")
        raise HTTPException(status_code=404, detail=str(e))
    except Exception as e:
        logger.error(f"Unexpected error crawling {url}: {e}", exc_info=True)
        raise HTTPException(
            status_code=500,
            detail=f"An unexpected error occurred while crawling '{url}': {str(e)}"
        )
    # Defensive fallback (should never hit unless page is None)
    logger.warning(f"No content found at URL {url}")
    return JSONResponse(
        status_code=400,
        content={
            "error": "No content found.",
            "detail": f"Could not find any content at the URL '{url}' to crawl."
        }
    )

# -------------------------------
# ----- Vector DB endpoints -----
# -------------------------------
@app.post(f"{API_PREFIX}/embed", response_model=EmbedResponse, tags=["Embedder"])
async def embed_url(request: EmbedRequest):
    """Orchestrates the full workflow: Crawl -> Chunk -> Embed -> Store."""
    url_str = str(request.url)
    try:
        crawled_data = await crawler_service.fetch_and_clean_page(url_str)
        if not crawled_data or not crawled_data.get("text"):
            raise HTTPException(status_code=404, detail="Could not find any text content to embed at the URL.")
    except (httpx.HTTPStatusError, httpx.RequestError, httpx.TimeoutException) as e:
        logger.error(f"HTTP error while crawling {url_str}: {e}")
        raise handle_httpx_exception(url_str, e)
    except ValueError as e:
        # Empty content or invalid URL, often a 400 or 404
        logger.warning(f"Value error crawling {url_str}: {e}")
        raise HTTPException(status_code=404, detail=str(e))
    except Exception as e:
        logger.error(f"Unexpected error crawling {url_str}: {e}", exc_info=True)
        raise HTTPException(
            status_code=500,
            detail=f"An unexpected error occurred while crawling '{url_str}': {str(e)}"
        )

    try:
        num_chunks = embedder_service.embed_and_store_text(
            text=crawled_data["text"], source_url=url_str
        )
        return EmbedResponse(
            url=url_str, chunks_embedded=num_chunks, message="Content successfully crawled and embedded."
        )
    except Exception as e:
        logging.error(f"ERROR during embedding: {e}")
        raise HTTPException(status_code=500, detail=f"Failed to embed content: {str(e)}")



@app.post(f"{API_PREFIX}/query", response_model=QueryResponse)
async def query_vector_db(request: QueryRequest):
    """Queries the vector database for text chunks semantically similar to the query."""
    client = get_weaviate_client()
    embeddings_model = OpenAIEmbeddings(model="text-embedding-3-small")
    query_vector = embeddings_model.embed_query(request.query_text)

    result = (
         client.query
         .get(DOCUMENT_CLASS_NAME, ["content", "source_url"])
         .with_near_vector({"vector": query_vector})
         .with_limit(request.limit)
         .do()
    )
    
    docs_data = result["data"]["Get"][DOCUMENT_CLASS_NAME]
    docs = [DocumentResult(**doc) for doc in docs_data]
    return QueryResponse(query=request.query_text, results=docs)

# -------------------------------
# --- LLM Endpoints -------------
# -------------------------------
@app.post(f"{API_PREFIX}/generate", response_model=GenerateResponse, tags=["LLM"])
async def generate_completion(request: GenerateRequest):
    """Generates a text completion using the configured LLM abstraction layer."""
    try:
        generated_text = llm_service.generate_text(request.prompt)
        return GenerateResponse(
            prompt=request.prompt,
            generated_text=generated_text,
            provider=os.getenv("LLM_PROVIDER", "local")
        )
    except Exception as e:
        logging.error(f"ERROR during text generation: {e}")
        raise HTTPException(status_code=500, detail=f"Failed to generate text: {str(e)}")
  

# ──────────────────────────────────────────────────────────────────────────
# RAG endpoint
# ──────────────────────────────────────────────────────────────────────────
@app.post("/api/v1/rag/generate-course", response_model=Course, tags=["rag"])
async def generate_course(req: CourseGenerationRequest):
    """
    • POST because generation is a side-effectful operation (non-idempotent).
    • Returns a fully-validated Course JSON ready for the course-service.
    """
    try:
        return course_generator.generate_course(req)
    except Exception as e:
        raise HTTPException(500, str(e)) from e

# -------------------------------
# --- Scheduler Endpoints -------
# -------------------------------
@app.get(f"{API_PREFIX}/scheduler/status", response_model=SchedulerStatus, tags=["Scheduler"])
async def get_scheduler_status_endpoint():
   """Get the current status of the blog embedder scheduler"""
   return SchedulerStatus(**get_scheduler_status())


@app.post(f"{API_PREFIX}/scheduler/control", tags=["Scheduler"])
async def control_scheduler(request: SchedulerControl):
   """Control the blog embedder scheduler (start/stop)"""
   if request.action == "start":
       start_scheduler()
       return {"message": "Scheduler started successfully"}
   elif request.action == "stop":
       stop_scheduler()
       return {"message": "Scheduler stopped successfully"}
   else:
       raise HTTPException(status_code=400, detail="Invalid action. Use 'start' or 'stop'")


@app.post(f"{API_PREFIX}/scheduler/run-now", tags=["Scheduler"])
async def run_scheduler_now():
   """Manually trigger the blog embedder job immediately"""
   try:
       # Import here to avoid circular imports
       from .services.scheduler.scheduler_service import _scheduler
      
       # Run the job in a separate thread to avoid blocking
       import threading
       def run_job():
           import asyncio
           async def async_job():
               urls = await _scheduler.fetch_freecodecamp_articles()
               await _scheduler.embed_articles(urls)
           asyncio.run(async_job())
      
       thread = threading.Thread(target=run_job, daemon=True)
       thread.start()
      
       return {"message": "Blog embedder job triggered successfully"}
   except Exception as e:
       logger.error(f"Error triggering scheduler job: {e}")
       raise HTTPException(status_code=500, detail=f"Failed to trigger job: {str(e)}")



# -------------------------------
# --------- MAIN ----------------
# -------------------------------
if __name__ == "__main__":
    import sys
    import uvicorn

    logger.info(f"Starting {APP_TITLE} v{APP_VERSION} on port {APP_PORT}...")

    try:
        uvicorn.run(
            "src.main:app",
            host="0.0.0.0",
            port=APP_PORT,
            log_level="info",
            reload=os.getenv("IS_DEV_MODE", "0") == "1",
            workers=int(os.getenv("UVICORN_WORKERS", "1")),
        )
    except Exception as exc:
        logger.exception(f"Failed to launch {APP_TITLE}: {exc}")
        sys.exit(1)
