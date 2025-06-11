# genai/src/main.py

import os
import logging
from dotenv import load_dotenv
from fastapi import FastAPI, HTTPException
import httpx
from contextlib import asynccontextmanager

# --- All Combined Imports ---
from .services.crawler import crawler_service
from .services.crawler.schemas import CrawlRequest, CrawlResponse
from .services.embedding import embedder_service
from .services.embedding.schemas import EmbedRequest, EmbedResponse, QueryRequest, QueryResponse, DocumentResult
from .services.embedding.weaviate_service import get_weaviate_client, ensure_schema_exists, DOCUMENT_CLASS_NAME
from .services.llm import llm_service
from .services.llm.schemas import GenerateRequest, GenerateResponse
from langchain_openai import OpenAIEmbeddings


# --- Configuration ---
logging.basicConfig(level=logging.INFO)
load_dotenv()
APP_TITLE = os.getenv("GENAI_APP_NAME", "SkillForge GenAI Service")
PORT = int(os.getenv("GENAI_PORT", "8082"))

# --- Lifespan Manager (for startup) ---
@asynccontextmanager
async def lifespan(app: FastAPI):
    logging.info("Running startup tasks...")
    client = get_weaviate_client()
    try:
        ensure_schema_exists(client)
        logging.info("Startup tasks complete. Weaviate schema is ready.")
    finally:
        pass # v3 client does not need explicit close here
    yield
    logging.info("Application shutting down.")

# --- App Initialization ---
app = FastAPI(title=APP_TITLE, lifespan=lifespan)

# --- API Endpoints ---
@app.get("/health")
def health():
    return {"status": "ok"}

@app.post("/crawl", response_model=CrawlResponse)
def crawl(request: CrawlRequest):
    """Crawls a URL, cleans the content, and utilizes a cache."""
    try:
        page = crawler_service.fetch_and_clean_page(str(request.url))
        if page:
            return page
    except httpx.HTTPStatusError as e:
        # This improved error handling is from your 'crawler' branch
        logging.error(f"HTTP error for {request.url}: {e}")
        raise HTTPException(
            status_code=e.response.status_code,
            detail=f"Could not fetch URL. Server responded with {e.response.status_code}"
        )
    except Exception as e:
        logging.error(f"Unexpected error crawling {request.url}: {e}")
        raise HTTPException(status_code=500, detail=f"An unexpected error occurred during crawling: {str(e)}")
    
    raise HTTPException(status_code=400, detail="Could not find any content at the URL to crawl.")

@app.post("/embed", response_model=EmbedResponse)
def embed_url(request: EmbedRequest):
    """Orchestrates the full workflow: Crawl -> Chunk -> Embed -> Store."""
    url_str = str(request.url)
    try:
        crawled_data = crawler_service.fetch_and_clean_page(url_str)
        if not crawled_data or not crawled_data.get("text"):
            raise HTTPException(status_code=404, detail="Could not find any text content to embed at the URL.")
    except httpx.HTTPStatusError as e:
        raise HTTPException(status_code=e.response.status_code, detail=f"Crawling failed: The target URL responded with {e.response.status_code}")

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

@app.post("/query", response_model=QueryResponse)
def query_vector_db(request: QueryRequest):
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

@app.post("/generate", response_model=GenerateResponse)
def generate_completion(request: GenerateRequest):
    """Generates a text completion using the configured LLM abstraction layer."""
    try:
        generated_text = llm_service.generate_text(request.prompt)
        return GenerateResponse(
            prompt=request.prompt,
            generated_text=generated_text,
            provider=os.getenv("LLM_PROVIDER", "dummy")
        )
    except Exception as e:
        logging.error(f"ERROR during text generation: {e}")
        raise HTTPException(status_code=500, detail=f"Failed to generate text: {str(e)}")