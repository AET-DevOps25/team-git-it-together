import React, { useState, useEffect, useRef } from 'react';
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from '@/components/ui/card';
import { Button } from '@/components/ui/button';
import { Input } from '@/components/ui/input';
import { Bot, User, Send } from 'lucide-react';
import { getAIChatResponse, confirmCourse } from '@/services/aiChat.service';
import { Dialog, DialogContent, DialogHeader, DialogTitle } from '@/components/ui/dialog';
import CoursePreview from '@/components/CoursePreview';
import ReactMarkdown from 'react-markdown';
import remarkGfm from 'remark-gfm';
import rehypeHighlight from 'rehype-highlight';
import { useAuth } from '@/hooks/useAuth';

interface Message {
  id: string;
  text: string;
  sender: 'user' | 'ai';
  timestamp: Date;
  isHtml?: boolean;
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
  disableCourseGeneration?: boolean;
}

const AIChatAssistant: React.FC<AIChatAssistantProps> = ({ 
  className, 
  userSkills = [], 
  title = "AI Learning Assistant",
  description = "Chat with your AI assistant to get help with your learning journey",
  placeholder = "Ask about your courses or what you want to learn next...",
  initialMessage = "👋 Hello! I'm your AI learning assistant. I'm here to help you learn and grow.\n\n💡 **Available Commands:**\n\n• `/help` - Show all available commands\n\n• `/generate <topic>` - Create a personalized course\n\n • `/explain <subject>` - Get a quick explanation\n\nAsk me anything about your courses or what you'd like to learn next!",
  height = "h-80",
  showHeader = true,
  disableCourseGeneration = false
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
  const [isCoursePreviewOpen, setIsCoursePreviewOpen] = useState(false);
  const { user } = useAuth();
  const userId = user?.id || '';
  const [coursePreview, setCoursePreview] = useState<any | null>(null);
  const [lastCoursePrompt, setLastCoursePrompt] = useState<string | null>(null);
  const [actionLoading, setActionLoading] = useState(false);

  const scrollToBottom = () => {
    messagesEndRef.current?.scrollIntoView({ behavior: 'smooth' });
  };

  useEffect(() => {
    scrollToBottom();
  }, [messages]);

  const handleSendMessage = async (customPrompt?: string) => {
    if (!(customPrompt ?? inputMessage).trim()) return;

    const userMessage: Message = {
      id: Date.now().toString(),
      text: customPrompt ?? inputMessage,
      sender: 'user',
      timestamp: new Date()
    };

    setMessages(prev => [...prev, userMessage]);
    setInputMessage('');
    setIsTyping(true);
    setCoursePreview(null);

    // Check if this is a course generation command
    const isCourseGeneration = (customPrompt ?? inputMessage).toLowerCase().includes('/generate') || 
                              (customPrompt ?? inputMessage).toLowerCase().includes('generate') ||
                              (customPrompt ?? inputMessage).toLowerCase().includes('create');

    // Show astro-themed waiting message for course generation
    if (isCourseGeneration && !disableCourseGeneration) {
      setMessages(prev => [...prev, {
        id: (Date.now() + 0.5).toString(),
        text: `🚀 **Launching Course Generation...**\n\n✨ I'm crafting your personalized learning journey through the cosmos of knowledge! This might take a moment as I:\n\n• 🌟 Analyze your skills\n\n• 🪐 Navigate through my knowledge base\n\n• ⭐ Structure the perfect learning path for you\n\n**Please hold on while I work my AI magic!** 🔮\n\n*This process can take up to 5 minutes...*`,
        sender: 'ai',
        timestamp: new Date()
      }]);
    }

    try {
      const aiText = await getAIChatResponse(userId, customPrompt ?? userMessage.text, userSkills, disableCourseGeneration);
      
      if (typeof aiText === 'object' && aiText && aiText.title && aiText.description) {
        setCoursePreview(aiText);
        setLastCoursePrompt(customPrompt ?? userMessage.text);
        setMessages(prev => [...prev, {
          id: (Date.now() + 1).toString(),
          text: 'A course has been generated. Please review and confirm.',
          sender: 'ai',
          timestamp: new Date()
        }]);
      } else {
        setMessages(prev => [...prev, {
          id: (Date.now() + 1).toString(),
          text: typeof aiText === 'string' ? aiText : JSON.stringify(aiText),
          sender: 'ai',
          timestamp: new Date()
        }]);
      }
    } catch (error) {
      console.error('AI chat service failed:', error);
      setMessages(prev => [...prev, {
        id: (Date.now() + 1).toString(),
        text: "I understand you're asking about that. Let me help you with that. This is a simulated response - in a real implementation, this would connect to your AI service.",
        sender: 'ai',
        timestamp: new Date()
      }]);
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

  const handleCourseAction = async (action: 'confirm' | 'regenerate' | 'abort') => {
    if (!coursePreview) return;
    setActionLoading(true);
    if (action === 'confirm') {
      try {
        const result = await confirmCourse(userId);
        let messageText: string;
        if (typeof result === 'object' && result && result.id) {
          messageText = `🎉 Course created successfully! [Go to course](/courses/${result.id})\n\nGood luck on your learning journey!`;
        } else {
          // Handle case where backend returns simple confirmation string
          const resultString = typeof result === 'string' ? result : 'Course confirmed.';
          if (resultString.toLowerCase().includes('confirmed') || resultString.toLowerCase().includes('success')) {
            messageText = `🎉 Course created successfully!\n\nGood luck on your learning journey! You can find your new course in your dashboard.`;
          } else {
            messageText = resultString;
          }
        }
        setMessages(prev => [...prev, {
          id: (Date.now() + 1).toString(),
          text: messageText,
          sender: 'ai',
          timestamp: new Date()
        }]);
        setCoursePreview(null);
        setLastCoursePrompt(null);
      } catch {
        setMessages(prev => [...prev, {
          id: (Date.now() + 1).toString(),
          text: '❌ Failed to confirm course generation. Please try again.',
          sender: 'ai',
          timestamp: new Date()
        }]);
      } finally {
        setActionLoading(false);
      }
    } else if (action === 'regenerate') {
      if (lastCoursePrompt) {
        await handleSendMessage(lastCoursePrompt);
      }
      setActionLoading(false);
    } else if (action === 'abort') {
      setCoursePreview(null);
      setLastCoursePrompt(null);
      setActionLoading(false);
      setMessages(prev => [...prev, {
        id: (Date.now() + 1).toString(),
        text: 'Course generation aborted.',
        sender: 'ai',
        timestamp: new Date()
      }]);
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
                className={`
                  flex max-w-[80vw] break-words overflow-x-auto
                  items-start space-x-2 rounded-lg px-3 py-2
                  ${message.sender === 'user'
                    ? 'bg-blue-600 text-white'
                    : 'bg-gray-100 text-gray-900'}
                `}
              >
                {message.sender === 'ai' && (
                  <Bot className="h-4 w-4 mt-0.5 text-blue-600" />
                )}
                <div className="flex-1">
                  {message.isHtml ? (
                    <div
                      className="text-sm whitespace-pre-wrap"
                      dangerouslySetInnerHTML={{ __html: message.text }}
                    />
                  ) : (
                    <div className={`prose prose-sm max-w-none ${message.sender === 'user' ? 'prose-invert' : ''}`}>
                      <ReactMarkdown
                        remarkPlugins={[remarkGfm]}
                        rehypePlugins={[rehypeHighlight]}
                      >
                        {message.text}
                      </ReactMarkdown>
                    </div>
                  )}
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
        {/* Course Preview Modal (inline, not dialog) */}
        {coursePreview && (
          <div className="w-full max-w-2xl mx-auto mt-6 mb-2 p-4 border border-blue-200 rounded-lg bg-blue-50 shadow">
            <CoursePreview course={coursePreview} />
            <div className="flex gap-4 mt-6 justify-end">
              <Button onClick={() => handleCourseAction('confirm')} disabled={actionLoading} className="bg-green-600 text-white">
                {actionLoading ? 'Confirming...' : 'Confirm'}
              </Button>
              <Button onClick={() => handleCourseAction('regenerate')} disabled={actionLoading} className="bg-yellow-500 text-white">
                {actionLoading ? 'Regenerating...' : 'Regenerate'}
              </Button>
              <Button onClick={() => handleCourseAction('abort')} disabled={actionLoading} className="bg-red-500 text-white">
                Abort
              </Button>
            </div>
            {actionLoading && <div className="text-xs text-gray-500 mt-2">This may take a while. Please wait...</div>}
          </div>
        )}
        {/* Input Area */}
        <div className="border-t p-4">
          <div className="flex space-x-2">
            <Input
              value={inputMessage}
              onChange={(e) => setInputMessage(e.target.value)}
              onKeyPress={handleKeyPress}
              placeholder={placeholder}
              className="flex-1"
              disabled={isTyping || !!coursePreview}
            />
            <Button
              onClick={() => handleSendMessage()}
              disabled={!inputMessage.trim() || isTyping || !!coursePreview}
              size="sm"
              className="px-3"
            >
              <Send className="h-4 w-4" />
            </Button>
          </div>
        </div>
      </CardContent>
      {/* Course Preview Modal (stub) */}
      <Dialog open={isCoursePreviewOpen} onOpenChange={setIsCoursePreviewOpen}>
        <DialogContent className="max-w-2xl">
          <DialogHeader>
            <DialogTitle>Course Preview</DialogTitle>
          </DialogHeader>
          <div className="p-4">
            <CoursePreview course={null} />
          </div>
        </DialogContent>
      </Dialog>
    </Card>
  );
};

export default AIChatAssistant; 