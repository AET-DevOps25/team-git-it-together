import React from 'react';
import AIChatAssistant from '@/components/AIChatAssistant';
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from '@/components/ui/card';
import { MessageSquare, Brain, Users, Clock } from 'lucide-react';

export default function ChatPage() {
  return (
    <div className="container mx-auto p-6 max-w-6xl">
      <div className="mb-8">
        <h1 className="text-3xl font-bold mb-2">AI Chat Assistant</h1>
        <p className="text-muted-foreground">
          Experience context-aware conversations with our AI assistant
        </p>
      </div>

      <div className="grid grid-cols-1 lg:grid-cols-3 gap-6">
        {/* Main Chat Interface */}
        <div className="lg:col-span-2">
          <AIChatAssistant 
            className="h-[600px]"
            title="Context-Aware AI Chat"
            description="Experience context-aware conversations with our AI assistant"
            placeholder="Ask questions, get explanations, or generate courses..."
            useNewChatService={true}
            disableCourseGeneration={false}
          />
        </div>

        {/* Features Panel */}
        <div className="space-y-6">
          <Card>
            <CardHeader>
              <CardTitle className="flex items-center gap-2">
                <Brain className="h-5 w-5" />
                Smart Features
              </CardTitle>
              <CardDescription>
                What makes this chat different
              </CardDescription>
            </CardHeader>
            <CardContent className="space-y-4">
              <div className="flex items-start gap-3">
                <MessageSquare className="h-5 w-5 mt-0.5 text-blue-600" />
                <div>
                  <h4 className="font-semibold">Context Memory</h4>
                  <p className="text-sm text-muted-foreground">
                    The AI remembers your conversation history and maintains context across messages.
                  </p>
                </div>
              </div>
              
              <div className="flex items-start gap-3">
                <Users className="h-5 w-5 mt-0.5 text-green-600" />
                <div>
                  <h4 className="font-semibold">User Isolation</h4>
                  <p className="text-sm text-muted-foreground">
                    Each user has completely isolated conversations with no cross-contamination.
                  </p>
                </div>
              </div>
              
              <div className="flex items-start gap-3">
                <Clock className="h-5 w-5 mt-0.5 text-purple-600" />
                <div>
                  <h4 className="font-semibold">Conversation Management</h4>
                  <p className="text-sm text-muted-foreground">
                    Switch between conversations, view history, and manage your chat sessions.
                  </p>
                </div>
              </div>
            </CardContent>
          </Card>

          <Card>
            <CardHeader>
              <CardTitle>Quick Tips</CardTitle>
            </CardHeader>
            <CardContent className="space-y-3">
              <div className="text-sm">
                <p className="font-medium mb-1">💡 Start a new conversation</p>
                <p className="text-muted-foreground">Click the "+" button to begin a fresh chat.</p>
              </div>
              
              <div className="text-sm">
                <p className="font-medium mb-1">🔄 Switch conversations</p>
                <p className="text-muted-foreground">Click "Conversations" to view and switch between your chats.</p>
              </div>
              
              <div className="text-sm">
                <p className="font-medium mb-1">🗑️ Clean up</p>
                <p className="text-muted-foreground">Delete individual conversations or clear all chats.</p>
              </div>
              
              <div className="text-sm">
                <p className="font-medium mb-1">⚡ Context window</p>
                <p className="text-muted-foreground">The AI maintains context from your last 10 messages by default.</p>
              </div>
            </CardContent>
          </Card>
        </div>
      </div>
    </div>
  );
} 