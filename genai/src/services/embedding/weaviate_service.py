# genai/src/services/embedding/weaviate_service.py
import weaviate
import os

DOCUMENT_CLASS_NAME = "DocumentChunk"

def get_weaviate_client() -> weaviate.Client:
    """Initializes and returns a Weaviate v3 client."""
    weaviate_url = os.getenv("WEAVIATE_URL")
    if not weaviate_url:
        raise ValueError("WEAVIATE_URL environment variable not set.")
    # This is the correct v3 syntax for creating a client
    return weaviate.Client(url=weaviate_url)

def ensure_schema_exists(client: weaviate.Client):
    """Checks if the DocumentChunk class exists in Weaviate and creates it if not."""
    document_class_schema = {
        "class": DOCUMENT_CLASS_NAME,
        "description": "A chunk of text from a crawled document.",
        "vectorizer": "none",
        "properties": [
            {"name": "content", "dataType": ["text"]},
            {"name": "source_url", "dataType": ["string"]},
        ],
    }
    
    # Use the v3 client's schema methods
    if not client.schema.exists(DOCUMENT_CLASS_NAME):
        print(f"Schema '{DOCUMENT_CLASS_NAME}' not found. Creating it...")
        client.schema.create_class(document_class_schema)
        print("Schema created successfully.")
    else:
        print(f"Schema '{DOCUMENT_CLASS_NAME}' already exists.")