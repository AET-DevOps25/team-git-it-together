import weaviate
import os
import logging
from typing import Optional
import threading

logger = logging.getLogger("skillforge.genai.weaviate_service")
DOCUMENT_CLASS_NAME = "DocumentChunk"

WEAVIATE_HOST = os.getenv("WEAVIATE_HOST", "localhost")
WEAVIATE_HTTP_PORT = int(os.getenv("WEAVIATE_HTTP_PORT", "8080"))
WEAVIATE_GRPC_PORT = int(os.getenv("WEAVIATE_GRPC_PORT", "50051"))

# Global client instance with thread safety
_client_lock = threading.Lock()
_weaviate_client: Optional[weaviate.Client] = None

def get_weaviate_client() -> weaviate.Client:
    """
    Returns a singleton Weaviate client with connection pooling.
    Thread-safe implementation to handle multiple concurrent requests.
    """
    global _weaviate_client
    
    with _client_lock:
        if _weaviate_client is None:
            weaviate_url = f"http://{WEAVIATE_HOST}:{WEAVIATE_HTTP_PORT}"
            if not weaviate_url:
                raise ValueError("WEAVIATE_URL environment variable not set.")
            
            logger.info(f"Creating new Weaviate client connection to {weaviate_url}")
            
            # Create client with connection pooling settings
            _weaviate_client = weaviate.Client(
                url=weaviate_url,
                # Add connection pooling configuration
                additional_headers={
                    "X-OpenAI-Api-Key": os.getenv("OPENAI_API_KEY", "")
                }
            )
            
            # Test the connection
            try:
                if _weaviate_client.is_ready():
                    logger.info("✅ Weaviate client connection established successfully")
                else:
                    logger.warning("⚠️ Weaviate client connected but not ready")
            except Exception as e:
                logger.error(f"❌ Failed to connect to Weaviate: {e}")
                _weaviate_client = None
                raise
    
    return _weaviate_client

def close_weaviate_client():
    """
    Safely close the Weaviate client connection.
    Should be called during application shutdown.
    """
    global _weaviate_client
    
    with _client_lock:
        if _weaviate_client is not None:
            try:
                logger.info("Closing Weaviate client connection")
                _weaviate_client._connection.close()
            except Exception as e:
                logger.warning(f"Error closing Weaviate client: {e}")
            finally:
                _weaviate_client = None

def ensure_schema_exists(client: weaviate.Client):
    """Checks if the DocumentChunk class exists in Weaviate and creates it if not (idempotent)."""
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
    try:
        if not client.schema.exists(DOCUMENT_CLASS_NAME):
            logger.info(f"Schema '{DOCUMENT_CLASS_NAME}' not found. Creating it...")
            client.schema.create_class(document_class_schema)
            logger.info("Schema created successfully.")
        else:
            logger.info(f"Schema '{DOCUMENT_CLASS_NAME}' already exists.")
    except Exception as exc:
        msg = str(getattr(exc, "error", exc))
        if "class already exists" in msg or "TYPE_ADD_CLASS" in msg:
            logger.info(f"Schema '{DOCUMENT_CLASS_NAME}' already exists (caught on creation attempt).")
        else:
            logger.error(f"Unexpected error while ensuring schema: {exc}")
            raise