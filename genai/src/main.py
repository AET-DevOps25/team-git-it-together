from fastapi import FastAPI
import uvicorn
from dotenv import load_dotenv
import os
from utils.vector_db_test import test_weaviate_connection

# Load environment variables
load_dotenv()
PORT = int(os.getenv("PORT", "8082"))

app = FastAPI()

@app.get("/health")
def health():
    return {"status": "ok"}

# Optional: test connection via HTTP route
@app.get("/test-weaviate")
def test_connection():
    test_weaviate_connection()
    return {"status": "tested"}

if __name__ == "__main__":
    print("🔌 Testing Weaviate connection on startup...")
    test_weaviate_connection()
    print("✅ Weaviate connection test complete.")
    uvicorn.run(app, host="0.0.0.0", port=PORT)
