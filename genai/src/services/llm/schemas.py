# genai/src/services/llm/schemas.py
from pydantic import BaseModel

class GenerateRequest(BaseModel):
    prompt: str

class GenerateResponse(BaseModel):
    prompt: str
    generated_text: str
    provider: str