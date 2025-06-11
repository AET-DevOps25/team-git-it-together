# genai/src/services/crawler/crawler_service.py
import httpx
from bs4 import BeautifulSoup
import hashlib
import os
import json

# Define a cache directory to store results
CACHE_DIR = "tmp/crawled_pages"
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
        response.raise_for_status() # Raise exception for 4xx/5xx errors

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
        print(f"Error fetching URL {url}: {e}")
        raise  # Re-raise the exception to be handled by FastAPI