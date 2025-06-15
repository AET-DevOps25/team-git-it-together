import os
import dotenv
import weaviate
import json
import time
import logging

# Load environment variables
dotenv.load_dotenv()
# Call the logger
logger = logging.getLogger("skillforge.genai.vector_db_test")
# Define the base HTTP URL and gRPC port (Weaviate uses both)
WEAVIATE_HOST = os.getenv("WEAVIATE_HOST", "localhost")
WEAVIATE_HTTP_PORT = int(os.getenv("WEAVIATE_HTTP_PORT", "1234"))

def test_weaviate_connection():
    weaviate_url = f"http://{WEAVIATE_HOST}:{WEAVIATE_HTTP_PORT}"
    logger.info(f"Your Weaviate client library version is: {weaviate.__version__}")
    logger.info(f"Connecting to Weaviate at {WEAVIATE_HOST}:{WEAVIATE_HTTP_PORT} (HTTP)")
    client = weaviate.Client(url=weaviate_url)

    try:
        time.sleep(5)  # allow Weaviate to finish startup
        # Fetch metadata
        meta = client.get_meta()
        logger.info("Weaviate meta:\n %s", json.dumps(meta, indent=2))

        # Readiness check
        if client.is_ready():
            logger.info("✅ Weaviate is fully ready")
        else:
            logger.warning("⚠️ Weaviate is reachable but not yet ready")
    except weaviate.exceptions.ConnectionError as e:
        logger.error("❌ Connection error: %s", e)
        raise RuntimeError("Failed to connect to Weaviate")
    finally:
        logger.info("Closing Weaviate client connection")
        client._connection.close()
