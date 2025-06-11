# genai/src/services/crawler/schemas.py
from pydantic import BaseModel, HttpUrl

class CrawlRequest(BaseModel):
    url: HttpUrl

class CrawlResponse(BaseModel):
    url: str
    text: str
    message: str