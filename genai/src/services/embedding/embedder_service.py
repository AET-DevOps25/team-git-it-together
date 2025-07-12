import os
from langchain_openai import OpenAIEmbeddings
from langchain.text_splitter import RecursiveCharacterTextSplitter
from langchain_community.vectorstores.weaviate import Weaviate
from .weaviate_service import get_weaviate_client, DOCUMENT_CLASS_NAME
import logging
from typing import List
import numpy as np


logger = logging.getLogger("skillforge.genai.embedder_service")

def embed_and_store_text(text: str, source_url: str) -> int:
    """
    Takes text, splits it, generates embeddings, and stores them in Weaviate.
    """
    logger.info(f"Embedding and storing text for URL: {source_url}")
    if not os.getenv("OPENAI_API_KEY"):
        raise ValueError("OPENAI_API_KEY environment variable not set.")
    
    text_splitter = RecursiveCharacterTextSplitter(chunk_size=1000, chunk_overlap=100)
    chunks = text_splitter.split_text(text)
    
    if not chunks:
        logger.warning(f"No text chunks were generated for URL {source_url}.")
        return 0
    
    metadatas = [{"source_url": source_url} for _ in chunks]
    embeddings_model = OpenAIEmbeddings(model="text-embedding-3-small", base_url="https://api.openai.com/v1")

    vectorstore = Weaviate.from_texts(
        client=get_weaviate_client(),
        index_name=DOCUMENT_CLASS_NAME,
        texts=chunks,
        embedding=embeddings_model,
        metadatas=metadatas,
        text_key="content",
    )
    num_chunks = len(chunks)

    logger.info(f"Storing {num_chunks} chunks in Weaviate for URL {source_url}.")

    if num_chunks == 0:
        logger.warning(f"No chunks were stored for URL {source_url}.")
    else:
        logger.info(f"Stored {num_chunks} chunks in Weaviate for URL {source_url}.")
    
    return num_chunks

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
