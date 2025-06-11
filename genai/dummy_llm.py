# genai/dummy_llm.py
from fastapi import FastAPI
import uvicorn

# This dummy server mimics the API of local LLM hosts like LM Studio or Ollama
app = FastAPI()

@app.post("/v1/chat/completions")
def dummy_completion():
    return {
        "choices": [{
            "message": {
                "role": "assistant",
                "content": "This is a dummy summary from the local model."
            }
        }]
    }

if __name__ == "__main__":
    print("Starting Dummy LLM Server on port 8001...")
    uvicorn.run(app, host="0.0.0.0", port=8001)