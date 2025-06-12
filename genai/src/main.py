# main.py - FastAPI app entry point for SkillForge GenAI Service

from fastapi import FastAPI, HTTPException
import uvicorn
from dotenv import load_dotenv
import os

import httpx
from src.services.crawler.schemas import CrawlRequest, CrawlResponse
from src.services.crawler import crawler_service

# Load environment variables
load_dotenv()
PORT = int(os.getenv("GENAI_PORT", "8082"))

# Get API title from environment variables
API_TITLE = os.getenv("API_TITLE", "SkillForge GenAI Service")

# Initialize FastAPI with configuration from environment
app = FastAPI(title=API_TITLE)

# Health check endpoint
@app.get("/health")
def health():
    return {"status": "ok"}

# Crawl a URL and return its content (cached or fresh)
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
    except httpx.HTTPStatusError as e:
        # Return the actual HTTP status code from the external service
        status_code = e.response.status_code
        raise HTTPException(
            status_code=status_code,
            detail=f"An unexpected error occurred: {str(e)}"
        )
    except httpx.RequestError as e:
        raise HTTPException(status_code=400, detail=f"Failed to fetch URL: {e}")
    except Exception as e:
        raise HTTPException(status_code=500, detail=f"An unexpected error occurred: {e}")


# Run the app if executed directly
if __name__ == "__main__":
    print("🔌 Testing Weaviate connection on startup...")
    test_weaviate_connection()
    print("✅ Weaviate connection test complete.")
    uvicorn.run(app, host="0.0.0.0", port=PORT)