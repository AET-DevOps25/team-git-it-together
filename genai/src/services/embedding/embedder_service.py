import os
from langchain_openai import OpenAIEmbeddings
from langchain.text_splitter import RecursiveCharacterTextSplitter
from langchain_community.vectorstores.weaviate import Weaviate
from .weaviate_service import get_weaviate_client, DOCUMENT_CLASS_NAME
import logging

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