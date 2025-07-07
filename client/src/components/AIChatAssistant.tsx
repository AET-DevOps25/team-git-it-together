import React, { useState, useEffect, useRef } from 'react';
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from '@/components/ui/card';
import { Button } from '@/components/ui/button';
import { Input } from '@/components/ui/input';
import { Bot, User, Send } from 'lucide-react';
import { getAIChatResponse } from '@/services/aiChat.service';

interface Message {
  id: string;
  text: string;
  sender: 'user' | 'ai';
  timestamp: Date;
}

interface AIChatAssistantProps {
  className?: string;
  userSkills?: string[];
  title?: string;
  description?: string;
  placeholder?: string;
  initialMessage?: string;
  height?: string;
  showHeader?: boolean;
}

const AIChatAssistant: React.FC<AIChatAssistantProps> = ({ 
  className, 
  userSkills = [], 
  title = "AI Learning Assistant",
  description = "Chat with your AI assistant to get help with your learning journey",
  placeholder = "Ask about your courses or what you want to learn next...",
  initialMessage = "👋 Hello! I'm your AI learning assistant. I'm here to help you learn and grow.\n\n💡 **Available Commands:**\n• `/help` - Show all available commands\n• `/generate <topic>` - Create a personalized course\n• `/explain <subject>` - Get a quick explanation\n\nAsk me anything about your courses or what you'd like to learn next!",
  height = "h-80",
  showHeader = true
}) => {
  const [messages, setMessages] = useState<Message[]>([
    {
      id: '1',
      text: initialMessage,
      sender: 'ai',
      timestamp: new Date()
    }
  ]);
  const [inputMessage, setInputMessage] = useState('');
  const [isTyping, setIsTyping] = useState(false);
  const messagesEndRef = useRef<HTMLDivElement>(null);

  const scrollToBottom = () => {
    messagesEndRef.current?.scrollIntoView({ behavior: 'smooth' });
  };

  useEffect(() => {
    scrollToBottom();
  }, [messages]);

  const handleSendMessage = async () => {
    if (!inputMessage.trim()) return;

    const userMessage: Message = {
      id: Date.now().toString(),
      text: inputMessage,
      sender: 'user',
      timestamp: new Date()
    };

    setMessages(prev => [...prev, userMessage]);
    setInputMessage('');
    setIsTyping(true);

    try {
      // Use the AI chat service for the response
      const aiText = await getAIChatResponse(inputMessage, userSkills);
      const aiMessage: Message = {
        id: (Date.now() + 1).toString(),
        text: aiText,
        sender: 'ai',
        timestamp: new Date()
      };
      setMessages(prev => [...prev, aiMessage]);
    } catch (error) {
      // Fallback response if AI service fails
      console.error('AI chat service failed:', error.message);
      const aiMessage: Message = {
        id: (Date.now() + 1).toString(),
        text: "I understand you're asking about that. Let me help you with that. This is a simulated response - in a real implementation, this would connect to your AI service.",
        sender: 'ai',
        timestamp: new Date()
      };
      setMessages(prev => [...prev, aiMessage]);
    } finally {
      setIsTyping(false);
    }
  };

  const handleKeyPress = (e: React.KeyboardEvent) => {
    if (e.key === 'Enter' && !e.shiftKey) {
      e.preventDefault();
      handleSendMessage();
    }
  };

  return (
    <Card className={`h-full ${className}`}>
      {showHeader && (
        <CardHeader>
          <CardTitle className="flex items-center space-x-2">
            <Bot className="h-5 w-5 text-blue-600" />
            <span>{title}</span>
          </CardTitle>
          <CardDescription>{description}</CardDescription>
        </CardHeader>
      )}
      <CardContent className="p-0">
        {/* Messages Container */}
        <div className={`${height} overflow-y-auto p-4 space-y-4`}>
          {messages.map((message) => (
            <div
              key={message.id}
              className={`flex ${message.sender === 'user' ? 'justify-end' : 'justify-start'}`}
            >
              <div
                className={`flex max-w-xs items-start space-x-2 rounded-lg px-3 py-2 ${
                  message.sender === 'user'
                    ? 'bg-blue-600 text-white'
                    : 'bg-gray-100 text-gray-900'
                }`}
              >
                {message.sender === 'ai' && (
                  <Bot className="h-4 w-4 mt-0.5 text-blue-600" />
                )}
                <div className="flex-1">
                  <div 
                    className="text-sm whitespace-pre-wrap"
                    dangerouslySetInnerHTML={{
                      __html: message.text
                        .replace(/\*\*(.*?)\*\*/g, '<strong>$1</strong>')
                        .replace(/\n/g, '<br>')
                    }}
                  />
                  <p className="text-xs opacity-70 mt-1">
                    {message.timestamp.toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' })}
                  </p>
                </div>
                {message.sender === 'user' && (
                  <User className="h-4 w-4 mt-0.5 text-blue-200" />
                )}
              </div>
            </div>
          ))}
          
          {isTyping && (
            <div className="flex justify-start">
              <div className="flex max-w-xs items-start space-x-2 rounded-lg bg-gray-100 px-3 py-2">
                <Bot className="h-4 w-4 mt-0.5 text-blue-600" />
                <div className="flex space-x-1">
                  <div className="h-2 w-2 animate-bounce rounded-full bg-gray-400"></div>
                  <div className="h-2 w-2 animate-bounce rounded-full bg-gray-400" style={{ animationDelay: '0.1s' }}></div>
                  <div className="h-2 w-2 animate-bounce rounded-full bg-gray-400" style={{ animationDelay: '0.2s' }}></div>
                </div>
              </div>
            </div>
          )}
          
          <div ref={messagesEndRef} />
        </div>
        
        {/* Input Area */}
        <div className="border-t p-4">
          <div className="flex space-x-2">
            <Input
              value={inputMessage}
              onChange={(e) => setInputMessage(e.target.value)}
              onKeyPress={handleKeyPress}
              placeholder={placeholder}
              className="flex-1"
            />
            <Button
              onClick={handleSendMessage}
              disabled={!inputMessage.trim() || isTyping}
              size="sm"
              className="px-3"
            >
              <Send className="h-4 w-4" />
            </Button>
          </div>
        </div>
      </CardContent>
    </Card>
  );
};

export default AIChatAssistant; 