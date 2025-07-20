# genai/src/services/llm/chat_service.py
import os
import uuid
import logging
import time
import random
from datetime import datetime, timedelta
from typing import Dict, List, Optional
from collections import defaultdict
import asyncio
import threading

from .schemas import (
    Message, MessageRole, ChatRequest, ChatResponse, 
    ConversationInfo, UserSession
)
from .llm_service import LLM_SINGLETON

logger = logging.getLogger(__name__)

class ChatService:
    """Manages chat conversations with context and user isolation."""
    
    def __init__(self):
        self._user_sessions: Dict[str, UserSession] = {}
        self._conversations: Dict[str, List[Message]] = defaultdict(list)
        self._lock = threading.RLock()
        self._cleanup_interval = 3600  # 1 hour
        self._session_timeout = 7200  # 2 hours
        self._max_context_window = 50
        self._default_context_window = 10
        
        # Start cleanup thread
        self._start_cleanup_thread()
    
    def _generate_conversation_name(self) -> str:
        """Generate a random conversation name."""
        adjectives = [
            'Cosmic', 'Quantum', 'Digital', 'Creative', 'Innovative', 'Dynamic', 'Strategic', 
            'Analytical', 'Technical', 'Practical', 'Theoretical', 'Experimental', 'Advanced',
            'Modern', 'Classic', 'Revolutionary', 'Traditional', 'Contemporary', 'Futuristic',
            'Ancient', 'Mystical', 'Scientific', 'Artistic', 'Logical', 'Intuitive', 'Systematic'
        ]
        
        nouns = [
            'Explorer', 'Pioneer', 'Navigator', 'Architect', 'Designer', 'Engineer', 'Scientist',
            'Artist', 'Thinker', 'Innovator', 'Creator', 'Builder', 'Developer', 'Researcher',
            'Scholar', 'Student', 'Teacher', 'Mentor', 'Guide', 'Advisor', 'Consultant',
            'Strategist', 'Analyst', 'Specialist', 'Expert', 'Master', 'Apprentice', 'Journey'
        ]
        
        random_adjective = random.choice(adjectives)
        random_noun = random.choice(nouns)
        
        return f"{random_adjective} {random_noun}"
    
    def _start_cleanup_thread(self):
        """Start background thread to clean up expired sessions."""
        def cleanup_worker():
            while True:
                try:
                    self._cleanup_expired_sessions()
                    time.sleep(self._cleanup_interval)
                except Exception as e:
                    logger.error(f"Error in cleanup thread: {e}")
                    time.sleep(60)  # Wait 1 minute before retrying
        
        thread = threading.Thread(target=cleanup_worker, daemon=True)
        thread.start()
    
    def _cleanup_expired_sessions(self):
        """Remove expired user sessions and conversations."""
        with self._lock:
            current_time = datetime.now()
            expired_users = []
            
            for user_id, session in self._user_sessions.items():
                if (current_time - session.last_activity).total_seconds() > self._session_timeout:
                    expired_users.append(user_id)
            
            for user_id in expired_users:
                self._remove_user_session(user_id)
                logger.info(f"Cleaned up expired session for user: {user_id}")
    
    def _remove_user_session(self, user_id: str):
        """Remove a user session and all associated conversations."""
        if user_id in self._user_sessions:
            session = self._user_sessions[user_id]
            # Remove all conversations for this user
            for conv_id in session.active_conversations.keys():
                if conv_id in self._conversations:
                    del self._conversations[conv_id]
            del self._user_sessions[user_id]
    
    def _get_or_create_user_session(self, user_id: str) -> UserSession:
        """Get existing user session or create a new one."""
        with self._lock:
            if user_id not in self._user_sessions:
                self._user_sessions[user_id] = UserSession(
                    user_id=user_id,
                    active_conversations={},
                    created_at=datetime.now(),
                    last_activity=datetime.now()
                )
            else:
                # Update last activity
                self._user_sessions[user_id].last_activity = datetime.now()
            
            return self._user_sessions[user_id]
    
    def _get_or_create_conversation(self, conversation_id: str, user_id: str, context_window: int) -> List[Message]:
        """Get existing conversation or create a new one."""
        with self._lock:
            # Get or create user session
            session = self._get_or_create_user_session(user_id)
            
            logger.info(f"Getting/converting conversation {conversation_id}. Existing conversations: {list(self._conversations.keys())}")
            
            if conversation_id not in self._conversations:
                logger.info(f"Creating new conversation {conversation_id}")
                self._conversations[conversation_id] = []
                
                # Register conversation in user session
                session.active_conversations[conversation_id] = ConversationInfo(
                    conversationId=conversation_id,
                    userId=user_id,
                    name=self._generate_conversation_name(),
                    messageCount=0,
                    createdAt=datetime.now(),
                    lastUpdated=datetime.now(),
                    contextWindow=context_window
                )
            else:
                logger.info(f"Using existing conversation {conversation_id} with {len(self._conversations[conversation_id])} messages")
                # Ensure conversation is registered in user session (in case it was missing)
                if conversation_id not in session.active_conversations:
                    session.active_conversations[conversation_id] = ConversationInfo(
                        conversationId=conversation_id,
                        userId=user_id,
                        name=self._generate_conversation_name(),
                        messageCount=len(self._conversations[conversation_id]),
                        createdAt=datetime.now(),
                        lastUpdated=datetime.now(),
                        contextWindow=context_window
                    )
                else:
                    # Ensure existing conversation has a name
                    conv_info = session.active_conversations[conversation_id]
                    if not conv_info.name:
                        conv_info.name = self._generate_conversation_name()
                        logger.info(f"Generated name for existing conversation {conversation_id}: {conv_info.name}")
            
            return self._conversations[conversation_id]
    
    def _add_message_to_conversation(self, conversation_id: str, message: Message):
        """Add a message to the conversation and update metadata."""
        with self._lock:
            self._conversations[conversation_id].append(message)
            logger.info(f"Added message to conversation {conversation_id}. Total messages: {len(self._conversations[conversation_id])}")
            
            # Update conversation info
            for user_id, session in self._user_sessions.items():
                if conversation_id in session.active_conversations:
                    conv_info = session.active_conversations[conversation_id]
                    conv_info.messageCount = len(self._conversations[conversation_id])
                    conv_info.lastUpdated = datetime.now()
                    break
    
    def _get_context_messages(self, conversation_id: str, context_window: int) -> List[Message]:
        """Get the most recent messages within the context window."""
        with self._lock:
            messages = self._conversations.get(conversation_id, [])
            logger.info(f"Retrieved {len(messages)} total messages for conversation {conversation_id}")
            # Return the last N messages (within context window)
            context_messages = messages[-context_window:] if messages else []
            logger.info(f"Returning {len(context_messages)} messages for context window {context_window}")
            return context_messages
    
    def _prepare_messages_for_llm(self, messages: List[Message], system_prompt: Optional[str] = None) -> List[Dict[str, str]]:
        """Convert internal messages to LLM format."""
        llm_messages = []
        
        # Add system prompt if provided
        if system_prompt:
            llm_messages.append({
                "role": MessageRole.SYSTEM.value,
                "content": system_prompt
            })
        
        # Add conversation messages
        for msg in messages:
            llm_messages.append({
                "role": msg.role.value,
                "content": msg.content
            })
        
        return llm_messages
    
    def chat(self, request: ChatRequest) -> ChatResponse:
        """Process a chat message with context management."""
        try:
            # Validate context window
            context_window = min(
                request.context_window or self._default_context_window,
                self._max_context_window
            )
            
            # Generate conversation ID if not provided
            conversation_id = request.conversation_id or str(uuid.uuid4())
            
            logger.info(f"Processing chat request - User: {request.user_id}, Conversation: {conversation_id}, Context Window: {context_window}")
            
            # Get or create conversation
            conversation = self._get_or_create_conversation(
                conversation_id, request.user_id, context_window
            )
            
            # Add user message to conversation
            user_message = Message(
                role=MessageRole.USER,
                content=request.message,
                timestamp=datetime.now()
            )
            self._add_message_to_conversation(conversation_id, user_message)
            
            # Get context messages
            context_messages = self._get_context_messages(conversation_id, context_window)
            logger.info(f"Context messages for conversation {conversation_id}: {len(context_messages)} messages")
            
            # Prepare messages for LLM
            llm_messages = self._prepare_messages_for_llm(
                context_messages, request.system_prompt
            )
            logger.info(f"LLM messages prepared: {len(llm_messages)} messages")
            for i, msg in enumerate(llm_messages):
                logger.info(f"  Message {i}: {msg['role']} - {msg['content'][:50]}...")
            
            # Generate response using LLM
            llm = LLM_SINGLETON
            response = llm.invoke(llm_messages)
            assistant_response = response.content if hasattr(response, 'content') else str(response)
            
            # Add assistant message to conversation
            assistant_message = Message(
                role=MessageRole.ASSISTANT,
                content=assistant_response,
                timestamp=datetime.now()
            )
            self._add_message_to_conversation(conversation_id, assistant_message)
            
            # Return response
            return ChatResponse(
                message=assistant_response,
                conversationId=conversation_id,
                userId=request.user_id,
                timestamp=datetime.now(),
                contextLength=len(context_messages),
                provider=os.getenv("LLM_PROVIDER", "local")
            )
            
        except Exception as e:
            logger.error(f"Error in chat service: {e}", exc_info=True)
            raise
    
    def get_conversation_history(self, conversation_id: str, user_id: str) -> List[Message]:
        """Get the full conversation history for a specific conversation."""
        with self._lock:
            # Verify user owns this conversation
            session = self._user_sessions.get(user_id)
            if not session or conversation_id not in session.active_conversations:
                raise ValueError("Conversation not found or access denied")
            
            return self._conversations.get(conversation_id, []).copy()
    
    def get_user_conversations(self, user_id: str) -> List[ConversationInfo]:
        """Get all conversations for a user."""
        with self._lock:
            session = self._user_sessions.get(user_id)
            if not session:
                return []
            
            return list(session.active_conversations.values())
    
    def delete_conversation(self, conversation_id: str, user_id: str) -> bool:
        """Delete a conversation for a user."""
        with self._lock:
            session = self._user_sessions.get(user_id)
            if not session or conversation_id not in session.active_conversations:
                return False
            
            # Remove conversation
            if conversation_id in self._conversations:
                del self._conversations[conversation_id]
            
            # Remove from user session
            del session.active_conversations[conversation_id]
            
            return True
    
    def clear_user_conversations(self, user_id: str) -> bool:
        """Clear all conversations for a user."""
        with self._lock:
            if user_id not in self._user_sessions:
                return False
            
            session = self._user_sessions[user_id]
            
            # Remove all conversations
            for conv_id in session.active_conversations.keys():
                if conv_id in self._conversations:
                    del self._conversations[conv_id]
            
            # Clear user session
            session.active_conversations.clear()
            
            return True
    
    def rename_conversation(self, conversation_id: str, user_id: str, new_name: str) -> bool:
        """Rename a conversation."""
        try:
            with self._lock:
                if user_id in self._user_sessions:
                    session = self._user_sessions[user_id]
                    if conversation_id in session.active_conversations:
                        session.active_conversations[conversation_id].name = new_name
                        return True
                return False
        except Exception as e:
            logger.error(f"Error renaming conversation: {e}")
            return False

# Global chat service instance
chat_service = ChatService() 