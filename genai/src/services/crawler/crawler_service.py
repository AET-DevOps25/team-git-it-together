import httpx
from bs4 import BeautifulSoup
import hashlib
import os
import json
import logging

# Configure logging
logging.basicConfig(level=logging.INFO, format='%(asctime)s - %(name)s - %(levelname)s - %(message)s')

# Define a cache directory to store results
CACHE_DIR = os.path.abspath(os.path.join(os.path.dirname(__file__), "tmp/crawled_pages"))
os.makedirs(CACHE_DIR, exist_ok=True)

def get_crawled_page(url: str) -> dict | None:
    """Checks the cache for a previously crawled page."""
    file_id = hashlib.md5(url.encode()).hexdigest()
    path = os.path.join(CACHE_DIR, f"{file_id}.json")
    if os.path.exists(path):
        with open(path, "r", encoding="utf-8") as f:
            return json.load(f)
    return None

def fetch_and_clean_page(url: str) -> dict:
    """Fetches a URL, cleans its content, and saves it to the cache."""
    try:
        headers = {'User-Agent': 'SkillForgeBot/1.0'}
        response = httpx.get(url, timeout=10.0, follow_redirects=True, headers=headers)
        try:
            response.raise_for_status() # Raise exception for 4xx/5xx errors
        except httpx.HTTPStatusError as e:
            logging.error(f"HTTP error while fetching URL {url}: {e.response.status_code} - {e.response.text}")
            # Re-raise with the specific status code to be handled by FastAPI
            raise httpx.HTTPStatusError(
                f"Client error '{e.response.status_code} {e.response.reason_phrase}' for url '{url}'\nFor more information check: https://developer.mozilla.org/en-US/docs/Web/HTTP/Status/{e.response.status_code}",
                request=e.request,
                response=e.response
            )

        soup = BeautifulSoup(response.text, 'html.parser')

        # Remove irrelevant tags
        for element in soup(["script", "style", "noscript", "nav", "footer", "header"]):
            element.extract()

        text = soup.get_text(separator="\n", strip=True)

        # Create result object
        result = {"url": url, "text": text}

        # Save the fresh result to the cache
        file_id = hashlib.md5(url.encode()).hexdigest()
        path = os.path.join(CACHE_DIR, f"{file_id}.json")
        with open(path, "w", encoding="utf-8") as f:
            json.dump(result, f, ensure_ascii=False, indent=4)

        return result

    except httpx.RequestError as e:
        logging.error(f"Error fetching URL {url}: {e}")
        raise  # Re-raise the exception to be handled by FastAPI