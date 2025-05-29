import os
import dotenv
import weaviate
import json
import time
from weaviate.connect import ConnectionParams

# Load environment variables
dotenv.load_dotenv()

# Define the base HTTP URL and gRPC port (Weaviate uses both)
WEAVIATE_HOST = os.getenv("WEAVIATE_HOST", "localhost")
WEAVIATE_HTTP_PORT = int(os.getenv("WEAVIATE_HTTP_PORT", "1234"))
WEAVIATE_GRPC_PORT = int(os.getenv("WEAVIATE_GRPC_PORT", "50051"))

def test_weaviate_connection():
    print(f"Your Weaviate client library version is: {weaviate.__version__}")

    params = ConnectionParams.from_params(
        http_host=WEAVIATE_HOST,
        http_port=WEAVIATE_HTTP_PORT,
        grpc_host=WEAVIATE_HOST,
        grpc_port=WEAVIATE_GRPC_PORT,
        http_secure=False,
        grpc_secure=False,
    )
    print(f"Connecting to Weaviate at {WEAVIATE_HOST}:{WEAVIATE_HTTP_PORT} (HTTP) and {WEAVIATE_HOST}:{WEAVIATE_GRPC_PORT} (gRPC)")
    client = weaviate.WeaviateClient(connection_params=params)

    try:
        client.connect()
        time.sleep(2)  # allow Weaviate to finish startup

        # Fetch metadata
        meta = client.get_meta()
        print("Weaviate meta:\n", json.dumps(meta, indent=2))

        # Readiness check
        if client.is_ready():
            print("✅ Weaviate is fully ready")
        else:
            print("❌ Weaviate is reachable but not yet ready")
    finally:
        client.close()
