from fastapi import FastAPI, HTTPException
import uvicorn
from dotenv import load_dotenv
import os

# --- New Imports for Crawler Service ---
import httpx
from src.services.crawler.schemas import CrawlRequest, CrawlResponse
from src.services.crawler import crawler_service
# --- End of New Imports ---

# Your existing imports
from src.utils.vector_db_test import test_weaviate_connection

# Load environment variables
load_dotenv()
PORT = int(os.getenv("PORT", "8082"))

# It's good practice to add a title for your API documentation
app = FastAPI(title="SkillForge GenAI Service")


# --- Your Existing Endpoints (Untouched) ---
@app.get("/health")
def health():
    return {"status": "ok"}

# Optional: test connection via HTTP route
@app.get("/test-weaviate")
def test_connection():
    test_weaviate_connection()
    return {"status": "tested"}


# --- New Crawler Endpoint ---
@app.post("/crawl", response_model=CrawlResponse)
def crawl(request: CrawlRequest):
    """
    Crawls a URL. Returns a cached result if available, otherwise fetches,
    cleans, caches, and returns the new content.
    """
    url_str = str(request.url)
    
    # 1. Check cache first
    cached_page = crawler_service.get_crawled_page(url_str)
    if cached_page:
        return CrawlResponse(
            url=cached_page["url"],
            text=cached_page["text"],
            message="Content retrieved from cache."
        )

    # 2. If not in cache, fetch and clean
    try:
        new_page = crawler_service.fetch_and_clean_page(url_str)
        return CrawlResponse(
            url=new_page["url"],
            text=new_page["text"],
            message="Content fetched, cleaned, and cached successfully."
        )
    except httpx.RequestError as e:
        raise HTTPException(status_code=400, detail=f"Failed to fetch URL: {e}")
    except Exception as e:
        raise HTTPException(status_code=500, detail=f"An unexpected error occurred: {e}")
# --- End of New Crawler Endpoint ---


# Your existing startup logic (Untouched)
if __name__ == "__main__":
    print("🔌 Testing Weaviate connection on startup...")
    test_weaviate_connection()
    print("✅ Weaviate connection test complete.")
    uvicorn.run(app, host="0.0.0.0", port=PORT)