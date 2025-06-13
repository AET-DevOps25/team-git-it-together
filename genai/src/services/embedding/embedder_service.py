# genai/src/services/embedding/embedder_service.py
import os
from langchain_openai import OpenAIEmbeddings
from langchain.text_splitter import RecursiveCharacterTextSplitter
from langchain_community.vectorstores.weaviate import Weaviate
from .weaviate_service import get_weaviate_client, DOCUMENT_CLASS_NAME

def embed_and_store_text(text: str, source_url: str) -> int:
    """
    Takes text, splits it, generates embeddings, and stores them in Weaviate.
    """
    if not os.getenv("OPENAI_API_KEY"):
        raise ValueError("OPENAI_API_KEY environment variable not set.")
        
    text_splitter = RecursiveCharacterTextSplitter(chunk_size=1000, chunk_overlap=100)
    chunks = text_splitter.split_text(text)
    
    if not chunks:
        print(f"Warning: No text chunks were generated for URL {source_url}.")
        return 0
    
    metadatas = [{"source_url": source_url} for _ in chunks]
    embeddings_model = OpenAIEmbeddings(model="text-embedding-3-small")
    
    Weaviate.from_texts(
        client=get_weaviate_client(),
        index_name=DOCUMENT_CLASS_NAME,
        texts=chunks,
        embedding=embeddings_model,
        metadatas=metadatas,
        text_key="content",
    )
    
    print(f"Successfully embedded {len(chunks)} chunks from {source_url}.")
    return len(chunks)