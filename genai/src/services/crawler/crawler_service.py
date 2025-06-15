from fastapi import HTTPException
import httpx
from bs4 import BeautifulSoup
import hashlib
import os
import json
import logging

# Configure logging
logger = logging.getLogger("skillforge.genai.crawler")
# Define a cache directory to store results
CACHE_DIR = os.path.abspath(os.path.join(os.path.dirname(__file__), "tmp/crawled_pages"))
os.makedirs(CACHE_DIR, exist_ok=True)

def get_crawled_page(url: str) -> dict | None:
    file_id = hashlib.md5(url.encode()).hexdigest()
    path = os.path.join(CACHE_DIR, f"{file_id}.json")
    if os.path.exists(path):
        logger.info(f"Cache hit for '{url}'")
        with open(path, "r", encoding="utf-8") as f:
            return json.load(f)
    logger.info(f"Cache miss for '{url}'")
    return None

async def fetch_and_clean_page(url: str) -> dict:
    """
    Fetches a web page, cleans it by removing non-text elements, and caches the result.
    If the page has been crawled before, it retrieves the cached version.
    Args:
        url (str): The URL of the page to crawl.
    Returns:
        dict: A dictionary containing the URL and cleaned text content.
    Raises:
        httpx.HTTPStatusError: If the HTTP request fails with a non-2xx status code.
        httpx.RequestError: If there is a network-related error.
        ValueError: If no usable text content is found on the page.
        RuntimeError: For unexpected errors during processing.
    """
    if not url.startswith("http"):
        # raise an error with 404 not found
        raise HTTPException(status_code=404, detail=f"Invalid URL format: {url}. URL must start with 'http' or 'https'.")
    # Check if the page is already cached
    cached_page = get_crawled_page(url)
    if cached_page:
        return cached_page
    
    logger.info(f"Fetching page: {url}")
    # Set a user-agent to avoid being blocked by some websites
    headers = {'User-Agent': 'SkillForgeBot/1.0'}
    try:
        async with httpx.AsyncClient() as client:
            try:
                response = await client.get(
                    url, timeout=10.0, follow_redirects=True, headers=headers
                )
                response.raise_for_status()
            except httpx.HTTPStatusError as e:
                status_code = e.response.status_code
                logger.error(
                    f"HTTP error {status_code} while fetching '{url}': {e.response.text[:250]}"
                )
                raise
            except httpx.TimeoutException:
                logger.error(f"Timeout occurred while fetching '{url}'.")
                raise httpx.RequestError("Timeout occurred.", request=None)

        soup = BeautifulSoup(response.text, 'html.parser')
        for element in soup(["script", "style", "noscript", "nav", "footer", "header"]):
            element.extract()

        text = soup.get_text(separator="\n", strip=True)
        if not text.strip():
            logger.warning(f"Page at '{url}' returned no usable text content.")
            raise ValueError(f"No usable text found at {url}")

        result = {"url": url, "text": text}

        file_id = hashlib.md5(url.encode()).hexdigest()
        path = os.path.join(CACHE_DIR, f"{file_id}.json")
        with open(path, "w", encoding="utf-8") as f:
            json.dump(result, f, ensure_ascii=False, indent=4)

        logger.info(f"Crawled and cached: {url}")
        return result

    except httpx.HTTPStatusError as e:
        raise
    except httpx.RequestError as e:
        logger.error(f"Request error while fetching '{url}': {e}")
        raise
    except ValueError as e:
        logger.warning(str(e))
        raise
    except Exception as e:
        logger.exception(f"Unexpected error while fetching '{url}': {e}")
        raise RuntimeError(f"Unexpected error while fetching '{url}': {str(e)}")