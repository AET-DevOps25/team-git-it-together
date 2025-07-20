import { API_BASE_URL } from '@/constants/app';

export interface ChatRequest {
  message: string;
  user_id: string; // Match the server's snake_case format
  conversation_id?: string; // Match the server's snake_case format
  context_window?: number; // Match the server's snake_case format
  system_prompt?: string; // Match the server's snake_case format
}

export interface ChatResponse {
  message: string;
  conversationId: string;
  userId: string;
  timestamp: string;
  contextLength: number;
  provider: string;
}

export interface ConversationInfo {
  conversationId: string;
  userId: string;
  name?: string;
  messageCount: number;
  createdAt: string;
  lastUpdated: string;
  contextWindow: number;
}

export interface Message {
  role: 'SYSTEM' | 'USER' | 'ASSISTANT';
  content: string;
  timestamp: string;
}

class ChatService {
  private baseUrl: string;
  private authToken: string | null = null;

  constructor() {
    this.baseUrl = `${API_BASE_URL}/chat`;
  }

  setAuthToken(token: string | null) {
    this.authToken = token;
  }

  private getHeaders(): HeadersInit {
    const headers: HeadersInit = {
      'Content-Type': 'application/json',
    };

    if (this.authToken) {
      headers['Authorization'] = `Bearer ${this.authToken}`;
    }

    return headers;
  }

  async sendMessage(request: ChatRequest): Promise<ChatResponse> {
    try {
      const response = await fetch(`${this.baseUrl}`, {
        method: 'POST',
        headers: this.getHeaders(),
        body: JSON.stringify(request),
      });

      if (!response.ok) {
        throw new Error(`Chat request failed: ${response.statusText}`);
      }

      return await response.json();
    } catch (error) {
      console.error('Error sending chat message:', error);
      throw error;
    }
  }

  async getUserConversations(userId: string): Promise<ConversationInfo[]> {
    try {
      const response = await fetch(`${this.baseUrl}/conversations/${userId}`, {
        method: 'GET',
        headers: this.getHeaders(),
      });

      if (!response.ok) {
        throw new Error(`Failed to fetch conversations: ${response.statusText}`);
      }

      return await response.json();
    } catch (error) {
      console.error('Error fetching user conversations:', error);
      throw error;
    }
  }

  async getConversationHistory(conversationId: string, userId: string): Promise<Message[]> {
    try {
      const response = await fetch(`${this.baseUrl}/history/${conversationId}/${userId}`, {
        method: 'GET',
        headers: this.getHeaders(),
      });

      if (!response.ok) {
        throw new Error(`Failed to fetch conversation history: ${response.statusText}`);
      }

      return await response.json();
    } catch (error) {
      console.error('Error fetching conversation history:', error);
      throw error;
    }
  }

  async deleteConversation(conversationId: string, userId: string): Promise<boolean> {
    try {
      const response = await fetch(`${this.baseUrl}/conversation/${conversationId}/${userId}`, {
        method: 'DELETE',
        headers: this.getHeaders(),
      });

      return response.ok;
    } catch (error) {
      console.error('Error deleting conversation:', error);
      return false;
    }
  }

  async clearUserConversations(userId: string): Promise<boolean> {
    try {
      const response = await fetch(`${this.baseUrl}/conversations/${userId}`, {
        method: 'DELETE',
        headers: this.getHeaders(),
      });

      return response.ok;
    } catch (error) {
      console.error('Error clearing user conversations:', error);
      return false;
    }
  }

  async renameConversation(conversationId: string, userId: string, newName: string): Promise<boolean> {
    try {
      const response = await fetch(`${this.baseUrl}/conversation/${conversationId}/${userId}/rename`, {
        method: 'PUT',
        headers: this.getHeaders(),
        body: JSON.stringify({ newName: newName }),
      });

      return response.ok;
    } catch (error) {
      console.error('Error renaming conversation:', error);
      return false;
    }
  }

  // Helper method to create a new conversation
  async startNewConversation(userId: string, initialMessage: string, systemPrompt?: string): Promise<ChatResponse> {
    const request: ChatRequest = {
      message: initialMessage,
      user_id: userId,
      context_window: 10,
      system_prompt: systemPrompt,
    };

    return this.sendMessage(request);
  }

  // Helper method to continue an existing conversation
  async continueConversation(conversationId: string, userId: string, message: string): Promise<ChatResponse> {
    const request: ChatRequest = {
      message,
      user_id: userId,
      conversation_id: conversationId,
      context_window: 10,
    };

    return this.sendMessage(request);
  }
}

export const chatService = new ChatService(); 