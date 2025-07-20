# genai/src/services/llm/schemas.py
from pydantic import BaseModel
from typing import List, Optional, Dict, Any
from datetime import datetime
from enum import Enum

class MessageRole(str, Enum):
    SYSTEM = "system"
    USER = "user"
    ASSISTANT = "assistant"

class Message(BaseModel):
    role: MessageRole
    content: str
    timestamp: Optional[datetime] = None

class GenerateRequest(BaseModel):
    prompt: str

class GenerateResponse(BaseModel):
    prompt: str
    generated_text: str
    provider: str

class ChatRequest(BaseModel):
    message: str
    user_id: str
    conversation_id: Optional[str] = None
    context_window: Optional[int] = 10  # Number of messages to keep in context
    system_prompt: Optional[str] = None

class ChatResponse(BaseModel):
    message: str
    conversationId: str
    userId: str
    timestamp: datetime
    contextLength: int
    provider: str

class ConversationInfo(BaseModel):
    conversationId: str
    userId: str
    name: Optional[str] = None
    messageCount: int
    createdAt: datetime
    lastUpdated: datetime
    contextWindow: int

class UserSession(BaseModel):
    user_id: str
    active_conversations: Dict[str, ConversationInfo]
    created_at: datetime
    last_activity: datetime