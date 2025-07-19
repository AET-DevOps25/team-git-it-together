import os
import time
import logging
from typing import List
import numpy as np
from langchain_openai import OpenAIEmbeddings
from langchain.text_splitter import RecursiveCharacterTextSplitter
from langchain_community.vectorstores.weaviate import Weaviate
from .weaviate_service import get_weaviate_client, DOCUMENT_CLASS_NAME

logger = logging.getLogger("skillforge.genai.embedder_service")

def wait_for_weaviate_ready(max_retries: int = 10, delay: float = 2.0) -> bool:
    """
    Wait for Weaviate to be ready with retry logic.
    Returns True if Weaviate is ready, False otherwise.
    """
    client = get_weaviate_client()
    
    for attempt in range(max_retries):
        try:
            logger.info(f"Checking Weaviate readiness (attempt {attempt + 1}/{max_retries})")
            if client.is_ready():
                logger.info("✅ Weaviate is ready")
                return True
            else:
                logger.warning(f"⚠️ Weaviate is reachable but not ready (attempt {attempt + 1})")
        except Exception as e:
            logger.warning(f"⚠️ Weaviate connection attempt {attempt + 1} failed: {e}")
        
        if attempt < max_retries - 1:
            logger.info(f"Waiting {delay} seconds before retry...")
            time.sleep(delay)
    
    logger.error(f"❌ Weaviate failed to become ready after {max_retries} attempts")
    return False

def embed_and_store_text(text: str, source_url: str) -> int:
    """
    Takes text, splits it, generates embeddings, and stores them in Weaviate.
    Includes retry logic for Weaviate connection issues.
    """
    logger.info(f"Embedding and storing text for URL: {source_url}")
    if not os.getenv("OPENAI_API_KEY"):
        raise ValueError("OPENAI_API_KEY environment variable not set.")
    
    # Wait for Weaviate to be ready before proceeding
    if not wait_for_weaviate_ready():
        raise RuntimeError("Weaviate is not ready after multiple attempts")
    
    text_splitter = RecursiveCharacterTextSplitter(chunk_size=1000, chunk_overlap=100)
    chunks = text_splitter.split_text(text)
    
    if not chunks:
        logger.warning(f"No text chunks were generated for URL {source_url}.")
        return 0
    
    metadatas = [{"source_url": source_url} for _ in chunks]
    embeddings_model = OpenAIEmbeddings(model="text-embedding-3-small", base_url="https://api.openai.com/v1")

    # Retry logic for Weaviate.from_texts with exponential backoff
    max_retries = 3
    base_delay = 1.0
    
    for attempt in range(max_retries):
        try:
            logger.info(f"Attempting to store embeddings in Weaviate (attempt {attempt + 1}/{max_retries})")
            
            vectorstore = Weaviate.from_texts(
                client=get_weaviate_client(),
                index_name=DOCUMENT_CLASS_NAME,
                texts=chunks,
                embedding=embeddings_model,
                metadatas=metadatas,
                text_key="content",
            )
            
            num_chunks = len(chunks)
            logger.info(f"Successfully stored {num_chunks} chunks in Weaviate for URL {source_url}")
            return num_chunks
            
        except Exception as e:
            error_msg = str(e)
            logger.warning(f"Embedding attempt {attempt + 1} failed: {error_msg}")
            
            # Check if it's a timeout/startup issue
            if "startup_period" in error_msg.lower() or "timeout" in error_msg.lower():
                if attempt < max_retries - 1:
                    delay = base_delay * (2 ** attempt)  # Exponential backoff
                    logger.info(f"Detected startup/timeout issue. Waiting {delay} seconds before retry...")
                    time.sleep(delay)
                    
                    # Re-check Weaviate readiness
                    if not wait_for_weaviate_ready(max_retries=3, delay=1.0):
                        logger.error("Weaviate became unavailable during retry")
                        continue
                else:
                    logger.error(f"Failed to embed after {max_retries} attempts due to startup/timeout issues")
                    raise RuntimeError(f"Weaviate startup timeout after {max_retries} attempts: {error_msg}")
            else:
                # For non-timeout errors, don't retry
                logger.error(f"Non-timeout error during embedding: {error_msg}")
                raise
    
    # This should never be reached, but just in case
    raise RuntimeError("Unexpected error in embed_and_store_text")

from .schemas import QueryResponse, QueryRequest, DocumentResult  # reuse existing pydantic model

def query_similar_chunks(query_text: str, limit: int = 3) -> QueryResponse:
    """
    Stateless helper – identical logic to the /query endpoint but callable in-process.
    """
    client = get_weaviate_client()
    embeddings_model = OpenAIEmbeddings(model="text-embedding-3-small")
    vector = embeddings_model.embed_query(query_text)

    result = (
        client.query
        .get(DOCUMENT_CLASS_NAME, ["content", "source_url"])
        .with_near_vector({"vector": vector})
        .with_limit(limit)
        .do()
    )
    docs = [DocumentResult(**d) for d in result["data"]["Get"][DOCUMENT_CLASS_NAME]]
    return QueryResponse(query=query_text, results=docs)


_embeddings_model = OpenAIEmbeddings(model="text-embedding-3-small")

def embed_text(text: str) -> List[float]:
    """Generate a single embedding vector from raw text."""
    return _embeddings_model.embed_query(text)

def cosine_similarity(v1: List[float], v2: List[float]) -> float:
    """Simple cosine similarity between two vectors."""
    a = np.array(v1)
    b = np.array(v2)
    return float(np.dot(a, b) / (np.linalg.norm(a) * np.linalg.norm(b)))
