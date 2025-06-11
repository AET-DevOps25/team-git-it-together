# genai/src/services/embedding/schemas.py
from pydantic import BaseModel, HttpUrl
from typing import List, Optional

# This schema will be re-used by our /embed endpoint
class EmbedRequest(BaseModel):
    """Request model for embedding a document from a URL"""
    url: HttpUrl

class EmbedResponse(BaseModel):
    """Response model for a successful embedding request"""
    url: str
    chunks_embedded: int
    message: str

# Schemas for the /query testing endpoint
class QueryRequest(BaseModel):
    """Request model for querying similar documents"""
    query_text: str
    limit: Optional[int] = 3

class DocumentResult(BaseModel):
    """Model for a single document chunk returned from a query"""
    content: str
    source_url: str

class QueryResponse(BaseModel):
    """Response model for a query request"""
    query: str
    results: List[DocumentResult]