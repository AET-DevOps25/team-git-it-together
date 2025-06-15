import weaviate
import os
import logging

logger = logging.getLogger("skillforge.genai.weaviate_service")
DOCUMENT_CLASS_NAME = "DocumentChunk"

WEAVIATE_HOST = os.getenv("WEAVIATE_HOST", "localhost")
WEAVIATE_HTTP_PORT = int(os.getenv("WEAVIATE_HTTP_PORT", "1234"))
WEAVIATE_GRPC_PORT = int(os.getenv("WEAVIATE_GRPC_PORT", "50051"))

def get_weaviate_client() -> weaviate.Client:
    """Initializes and returns a Weaviate v3 client."""
    weaviate_url = f"http://{WEAVIATE_HOST}:{WEAVIATE_HTTP_PORT}"
    if not weaviate_url:
        raise ValueError("WEAVIATE_URL environment variable not set.")
    # This is the correct v3 syntax for creating a client
    return weaviate.Client(url=weaviate_url)

def ensure_schema_exists(client: weaviate.Client):
    """Checks if the DocumentChunk class exists in Weaviate and creates it if not."""
    logger.info("Ensuring Weaviate schema exists...")
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