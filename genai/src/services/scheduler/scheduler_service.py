import asyncio
import logging
import threading
import time
import json
import os
from datetime import datetime
from typing import Optional
import httpx
from bs4 import BeautifulSoup
import re


from ..crawler import crawler_service
from ..embedding import embedder_service


logger = logging.getLogger(__name__)


class BlogEmbedderScheduler:
   def __init__(self):
       self.running = False
       self.thread: Optional[threading.Thread] = None
       self.last_run: Optional[datetime] = None
       self.cache_file = "scheduler_cache.json"
       self.embedded_urls = self._load_cache()  # Load from persistent cache
      
   def _load_cache(self) -> set:
       """Load embedded URLs from persistent cache file"""
       try:
           if os.path.exists(self.cache_file):
               with open(self.cache_file, 'r') as f:
                   data = json.load(f)
                   urls = set(data.get('embedded_urls', []))
                   last_run_str = data.get('last_run')
                   if last_run_str:
                       self.last_run = datetime.fromisoformat(last_run_str)
                   logger.info(f"Loaded {len(urls)} embedded URLs from cache")
                   return urls
           else:
               logger.info("No cache file found, starting with empty cache")
               return set()
       except Exception as e:
           logger.error(f"Error loading cache: {e}")
           return set()
  
   def _save_cache(self):
       """Save embedded URLs to persistent cache file"""
       try:
           data = {
               'embedded_urls': list(self.embedded_urls),
               'last_run': self.last_run.isoformat() if self.last_run else None,
               'last_updated': datetime.now().isoformat()
           }
           with open(self.cache_file, 'w') as f:
               json.dump(data, f, indent=2)
           logger.debug(f"Saved {len(self.embedded_urls)} embedded URLs to cache")
       except Exception as e:
           logger.error(f"Error saving cache: {e}")
      
   async def fetch_freecodecamp_articles(self) -> list[str]:
       """Fetch up to 5 non-embedded article URLs from freeCodeCamp blog"""
       try:
           async with httpx.AsyncClient(timeout=30.0) as client:
               response = await client.get("https://www.freecodecamp.org/news/")
               response.raise_for_status()
               
               soup = BeautifulSoup(response.text, 'html.parser')
               article_links = []
               
               # Find article links (adjust selector based on actual HTML structure)
               for link in soup.find_all('a', href=True):
                   href = link['href']
                   if '/news/' in href and href not in self.embedded_urls:
                       full_url = f"https://www.freecodecamp.org{href}" if href.startswith('/') else href
                       if full_url not in self.embedded_urls:
                           article_links.append(full_url)
                           if len(article_links) >= 5:
                               break
               
               logger.info(f"Found {len(article_links)} non-embedded articles from freeCodeCamp")
               return article_links
               
       except Exception as e:
           logger.error(f"Error fetching freeCodeCamp articles: {e}")
           return []
  
   async def embed_articles(self, urls: list[str]):
       """Embed articles into the vector database"""
       for url in urls:
           try:
               logger.info(f"Embedding article: {url}")
              
               # Crawl the article
               crawled_data = await crawler_service.fetch_and_clean_page(url)
               if not crawled_data or not crawled_data.get("text"):
                   logger.warning(f"No content found at {url}")
                   continue
              
               # Embed the content with retry logic
               try:
                   num_chunks = embedder_service.embed_and_store_text(
                       text=crawled_data["text"],
                       source_url=url
                   )
                   
                   # Mark as embedded and save to cache
                   self.embedded_urls.add(url)
                   self._save_cache()
                   logger.info(f"Successfully embedded {url} with {num_chunks} chunks")
                   
               except RuntimeError as e:
                   if "startup timeout" in str(e).lower() or "weaviate is not ready" in str(e).lower():
                       logger.error(f"Weaviate connection issue while embedding {url}: {e}")
                       logger.info("Skipping this article due to Weaviate connectivity issues")
                       continue
                   else:
                       raise
              
               # Small delay to be respectful to the server
               await asyncio.sleep(2)
              
           except Exception as e:
               logger.error(f"Error embedding {url}: {e}")
  
   async def run_scheduled_job(self):
       """Main scheduled job that runs every hour"""
       while self.running:
           try:
               logger.info("Starting scheduled freeCodeCamp blog embedding job")
               self.last_run = datetime.now()
              
               # Fetch latest articles
               article_urls = await self.fetch_freecodecamp_articles()
              
               if article_urls:
                   # Embed the articles
                   await self.embed_articles(article_urls)
                   logger.info(f"Completed embedding {len(article_urls)} articles")
               else:
                   logger.info("No new articles found to embed")
              
               # Save cache after each run
               self._save_cache()
              
               # Wait for 12 hours (43200 seconds)
               await asyncio.sleep(43200)
              
           except Exception as e:
               logger.error(f"Error in scheduled job: {e}")
               # Wait 5 minutes before retrying on error
               await asyncio.sleep(300)
  
   def start(self):
       """Start the scheduler in a separate thread"""
       if self.running:
           logger.warning("Scheduler is already running")
           return
          
       self.running = True
       self.thread = threading.Thread(
           target=lambda: asyncio.run(self.run_scheduled_job()),
           daemon=True,
           name="BlogEmbedderScheduler"
       )
       self.thread.start()
       logger.info("Blog embedder scheduler started")
  
   def stop(self):
       """Stop the scheduler"""
       self.running = False
       if self.thread and self.thread.is_alive():
           self.thread.join(timeout=10)
       logger.info("Blog embedder scheduler stopped")


# Global scheduler instance
_scheduler = BlogEmbedderScheduler()


def start_scheduler():
   """Start the blog embedder scheduler"""
   _scheduler.start()


def stop_scheduler():
   """Stop the blog embedder scheduler"""
   _scheduler.stop()


def get_scheduler_status() -> dict:
   """Get the current status of the scheduler"""
   return {
       "running": _scheduler.running,
       "last_run": _scheduler.last_run.isoformat() if _scheduler.last_run else None,
       "embedded_count": len(_scheduler.embedded_urls),
       "thread_alive": _scheduler.thread.is_alive() if _scheduler.thread else False
   }

