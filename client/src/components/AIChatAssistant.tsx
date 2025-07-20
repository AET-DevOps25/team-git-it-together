import React, { useState, useEffect, useRef } from 'react';
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from '@/components/ui/card';
import { Button } from '@/components/ui/button';
import { Input } from '@/components/ui/input';
import { ScrollArea } from '@/components/ui/scroll-area';
import { Bot, User, Send, Plus, Trash2, History, Edit2, RefreshCw } from 'lucide-react';
import { getAIChatResponse, confirmCourse } from '@/services/aiChat.service';
import { chatService, ChatResponse, ConversationInfo } from '@/services/chat.service';
import { Dialog, DialogContent, DialogHeader, DialogTitle } from '@/components/ui/dialog';
import CoursePreview from '@/components/CoursePreview';
import ReactMarkdown from 'react-markdown';
import remarkGfm from 'remark-gfm';
import rehypeHighlight from 'rehype-highlight';
import { useAuth } from '@/hooks/useAuth';

// Custom CSS for better message overflow handling
const messageStyles = `
  .chat-message-content {
    word-wrap: break-word;
    word-break: break-word;
    overflow-wrap: break-word;
    hyphens: auto;
  }
  
  .chat-message-content pre {
    white-space: pre-wrap;
    word-wrap: break-word;
    overflow-x: auto;
    max-width: 100%;
  }
  
  .chat-message-content code {
    word-wrap: break-word;
    white-space: pre-wrap;
  }
  
  .chat-message-content p {
    word-wrap: break-word;
    overflow-wrap: break-word;
  }
`;

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
  showHeader?: boolean;
  disableCourseGeneration?: boolean;
  useNewChatService?: boolean; // Enable new context-aware chat service
  showConversationHistory?: boolean; // Show conversation history sidebar
}

const AIChatAssistant: React.FC<AIChatAssistantProps> = ({ 
  className, 
  userSkills = [], 
  title = "AI Learning Assistant",
  description = "Chat with your AI assistant to get help with your learning journey",
  placeholder = "Ask about your courses or what you want to learn next...",
  initialMessage = "👋 Hello! I'm your AI learning assistant. I'm here to help you learn and grow.\n\n💡 **Available Commands:**\n\n• `/help` - Show all available commands\n\n• `/generate <topic>` - Create a personalized course\n\n • `/explain <subject>` - Get a quick explanation\n\nAsk me anything about your courses or what you'd like to learn next!",
  showHeader = true,
  disableCourseGeneration = false,
  useNewChatService = true, // Enable new chat service by default
  showConversationHistory = false // Show conversation history sidebar
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
  const [currentConversationId, setCurrentConversationId] = useState<string | null>(null);
  const [conversations, setConversations] = useState<ConversationInfo[]>([]);
  const [editingConversationId, setEditingConversationId] = useState<string | null>(null);
  const [editingName, setEditingName] = useState<string>('');
  
  // Use a ref to track the current conversation ID for immediate access
  const currentConversationIdRef = useRef<string | null>(null);

  const scrollToBottom = () => {
    messagesEndRef.current?.scrollIntoView({ behavior: 'smooth' });
  };

  useEffect(() => {
    scrollToBottom();
  }, [messages]);

  // Load conversations when user changes
  useEffect(() => {
    if (showConversationHistory && user?.id) {
      loadConversations();
    }
  }, [user?.id, showConversationHistory]);

  // Keep ref in sync with state
  useEffect(() => {
    currentConversationIdRef.current = currentConversationId;
  }, [currentConversationId]);

  const loadConversations = async () => {
    if (!user?.id) return;
    
    try {
      const userConversations = await chatService.getUserConversations(user.id);
      setConversations(userConversations);
    } catch (error) {
      console.error('Failed to load conversations:', error);
    }
  };

  const refreshConversations = async () => {
    if (!user?.id) return;
    
    try {
      console.log('Refreshing conversation list...');
      const userConversations = await chatService.getUserConversations(user.id);
      console.log('Refreshed conversations:', userConversations);
      setConversations(userConversations);
    } catch (error) {
      console.error('Failed to refresh conversations:', error);
    }
  };

  const loadConversation = async (conversationId: string) => {
    if (!user?.id) return;

    try {
      console.log('Loading conversation history for:', conversationId);
      const history = await chatService.getConversationHistory(conversationId, user.id);
      console.log('Raw conversation history:', history);
      
      // Convert the history to the format expected by this component
      const convertedMessages = history.map((msg, index) => {
        // Handle both uppercase and lowercase role values
        const role = msg.role?.toUpperCase() || '';
        const sender = role === 'USER' ? 'user' : 'ai' as 'user' | 'ai';
        console.log(`Message ${index}: role=${msg.role}, normalized_role=${role}, sender=${sender}, content=${msg.content.substring(0, 50)}...`);
        return {
          id: index.toString(),
          text: msg.content,
          sender: sender,
          timestamp: new Date(msg.timestamp)
        };
      });
      
      console.log('Converted messages:', convertedMessages);
      setMessages(convertedMessages);
      setCurrentConversationId(conversationId);
      currentConversationIdRef.current = conversationId;
    } catch (error) {
      console.error('Failed to load conversation:', error);
      
      // If conversation not found, remove it from the list and show a message
      if (error instanceof Error && error.message.includes('404') || error.message.includes('not found')) {
        console.log('Conversation not found, removing from list:', conversationId);
        
        // Remove the conversation from the list
        setConversations(prev => prev.filter(conv => conv.conversationId !== conversationId));
        
        // Show a message to the user
        setMessages([{
          id: '1',
          text: 'This conversation has expired or was not found. Starting a new conversation.',
          sender: 'ai',
          timestamp: new Date()
        }]);
        
        // Clear the current conversation ID
        setCurrentConversationId(null);
        currentConversationIdRef.current = null;
      }
    }
  };

  const deleteConversation = async (conversationId: string) => {
    if (!user?.id) return;

    try {
      const success = await chatService.deleteConversation(conversationId, user.id);
      if (success) {
        if (currentConversationId === conversationId) {
          setCurrentConversationId(null);
          currentConversationIdRef.current = null;
          setMessages([{
            id: '1',
            text: initialMessage,
            sender: 'ai',
            timestamp: new Date()
          }]);
        }
        await loadConversations();
      }
    } catch (error) {
      console.error('Failed to delete conversation:', error);
    }
  };

  const startNewChat = () => {
    console.log('Starting new chat - clearing current conversation ID');
    console.log('Current conversation ID before clearing:', currentConversationId);
    // Clear current conversation ID to ensure a new conversation is created
    setCurrentConversationId(null);
    currentConversationIdRef.current = null;
    // Reset messages to initial state
    setMessages([{
      id: '1',
      text: initialMessage,
      sender: 'ai',
      timestamp: new Date()
    }]);
    // Clear any course preview
    setCoursePreview(null);
    setLastCoursePrompt(null);
    console.log('New chat started - currentConversationId should be null');
  };

  const startEditingConversation = (conversationId: string, currentName: string) => {
    setEditingConversationId(conversationId);
    setEditingName(currentName || '');
  };

  const saveConversationName = async () => {
    if (!user?.id || !editingConversationId || !editingName.trim()) return;

    try {
      const success = await chatService.renameConversation(editingConversationId, user.id, editingName.trim());
      if (success) {
        await loadConversations();
        setEditingConversationId(null);
        setEditingName('');
      }
    } catch (error) {
      console.error('Failed to rename conversation:', error);
    }
  };

  const cancelEditing = () => {
    setEditingConversationId(null);
    setEditingName('');
  };

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
      let aiText: string | any;
      
      if (useNewChatService && userId) {
        // Use new context-aware chat service
        try {
          let response: ChatResponse;
          
          // Use the ref to get the current conversation ID immediately
          const conversationId = currentConversationIdRef.current;
          console.log('Current conversation ID at start of request:', conversationId);
          
          if (conversationId) {
            // Continue existing conversation
            console.log('Continuing existing conversation:', conversationId);
            response = await chatService.continueConversation(conversationId, userId, customPrompt ?? userMessage.text);
          } else {
            // Start new conversation
            console.log('Starting new conversation');
            response = await chatService.startNewConversation(userId, customPrompt ?? userMessage.text);
            console.log('New conversation created with ID:', response.conversationId);
            setCurrentConversationId(response.conversationId);
            currentConversationIdRef.current = response.conversationId;
          }
          
          aiText = response.message;
          
          // Reload conversations if conversation history is enabled
          if (showConversationHistory) {
            await loadConversations();
          }
        } catch (chatError) {
          console.error('New chat service failed, falling back to old service:', chatError);
          // Fall back to old service if new one fails
          aiText = await getAIChatResponse(userId, customPrompt ?? userMessage.text, userSkills, disableCourseGeneration);
        }
      } else {
        // Use old chat service
        aiText = await getAIChatResponse(userId, customPrompt ?? userMessage.text, userSkills, disableCourseGeneration);
      }
      
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
    <div className={`h-full ${className}`}>
      <style>{messageStyles}</style>
      {showConversationHistory ? (
        // Layout with conversation history sidebar
        <div className="flex h-full overflow-hidden">
          {/* Conversation History Sidebar */}
          <div className="w-80 border-r bg-gray-50 flex flex-col overflow-hidden">
            <div className="p-4 border-b">
              <div className="flex items-center justify-between mb-3">
                <h3 className="font-semibold flex items-center gap-2">
                  <History className="h-4 w-4" />
                  Conversations
                </h3>
                <div className="flex gap-1">
                  <Button
                    variant="outline"
                    size="sm"
                    onClick={refreshConversations}
                  >
                    <RefreshCw className="h-4 w-4" />
                  </Button>
                  <Button
                    variant="outline"
                    size="sm"
                    onClick={startNewChat}
                  >
                    <Plus className="h-4 w-4" />
                  </Button>
                </div>
              </div>
              <p className="text-sm text-muted-foreground">
                {conversations.length} conversation{conversations.length !== 1 ? 's' : ''}
              </p>
            </div>
            
            <ScrollArea className="flex-1">
              <div className="p-2 space-y-1">
                {conversations.length === 0 ? (
                  <p className="text-sm text-muted-foreground p-4 text-center">
                    No conversations yet
                  </p>
                ) : (
                  conversations.map((conv) => (
                    <div
                      key={conv.conversationId}
                      className={`group flex items-center justify-between p-3 rounded-lg cursor-pointer transition-colors ${
                        currentConversationId === conv.conversationId
                          ? 'bg-blue-100 border border-blue-200'
                          : 'hover:bg-gray-100'
                      }`}
                      onClick={() => loadConversation(conv.conversationId)}
                    >
                      <div className="flex-1 min-w-0">
                        {editingConversationId === conv.conversationId ? (
                          <div className="space-y-1">
                            <Input
                              value={editingName}
                              onChange={(e) => setEditingName(e.target.value)}
                              onKeyPress={(e) => {
                                if (e.key === 'Enter') {
                                  saveConversationName();
                                } else if (e.key === 'Escape') {
                                  cancelEditing();
                                }
                              }}
                              className="h-6 text-xs"
                              autoFocus
                            />
                            <div className="flex gap-1">
                              <Button
                                variant="ghost"
                                size="sm"
                                onClick={(e) => {
                                  e.stopPropagation();
                                  saveConversationName();
                                }}
                                className="h-4 px-1 text-xs"
                              >
                                Save
                              </Button>
                              <Button
                                variant="ghost"
                                size="sm"
                                onClick={(e) => {
                                  e.stopPropagation();
                                  cancelEditing();
                                }}
                                className="h-4 px-1 text-xs"
                              >
                                Cancel
                              </Button>
                            </div>
                          </div>
                        ) : (
                          <>
                            <p className="text-sm font-medium truncate">
                              {conv.name || `Conversation ${conv.conversationId.slice(0, 8)}...`}
                            </p>
                            <p className="text-xs text-muted-foreground">
                              {conv.messageCount} messages • {new Date(conv.lastUpdated).toLocaleDateString()}
                            </p>
                          </>
                        )}
                      </div>
                      <div className="flex gap-1 opacity-0 group-hover:opacity-100 transition-opacity">
                        <Button
                          variant="ghost"
                          size="sm"
                          onClick={(e) => {
                            e.stopPropagation();
                            startEditingConversation(conv.conversationId, conv.name || '');
                          }}
                          className="h-6 w-6 p-0"
                        >
                          <Edit2 className="h-3 w-3" />
                        </Button>
                        <Button
                          variant="ghost"
                          size="sm"
                          onClick={(e) => {
                            e.stopPropagation();
                            deleteConversation(conv.conversationId);
                          }}
                          className="h-6 w-6 p-0"
                        >
                          <Trash2 className="h-3 w-3" />
                        </Button>
                      </div>
                    </div>
                  ))
                )}
              </div>
            </ScrollArea>
          </div>

          {/* Main Chat Area */}
          <div className="flex-1 flex flex-col overflow-hidden">
            <Card className="h-full flex flex-col overflow-hidden">
              {showHeader && (
                <CardHeader className="pb-3">
                  <CardTitle className="flex items-center space-x-2">
                    <Bot className="h-5 w-5 text-blue-600" />
                    <span>{title}</span>
                  </CardTitle>
                  <CardDescription>{description}</CardDescription>
                </CardHeader>
              )}
              <CardContent className="p-0 flex-1 flex flex-col overflow-hidden">
                {/* Messages Container */}
                <div className="overflow-y-auto p-4 space-y-4 flex-1" style={{ minHeight: 0 }}>
                  {messages.map((message) => (
                    <div
                      key={message.id}
                      className={`flex ${message.sender === 'user' ? 'justify-end' : 'justify-start'}`}
                    >
                      <div
                        className={`
                          flex max-w-[70%] break-words overflow-hidden
                          items-start space-x-2 rounded-lg px-3 py-2
                          ${message.sender === 'user'
                            ? 'bg-blue-600 text-white'
                            : 'bg-gray-100 text-gray-900'}
                        `}
                      >
                        {message.sender === 'ai' && (
                          <Bot className="h-4 w-4 mt-0.5 text-blue-600 flex-shrink-0" />
                        )}
                        <div className="flex-1 min-w-0 overflow-hidden">
                          {message.isHtml ? (
                            <div
                              className="text-sm whitespace-pre-wrap break-words overflow-hidden chat-message-content"
                              dangerouslySetInnerHTML={{ __html: message.text }}
                            />
                          ) : (
                            <div className={`prose prose-sm max-w-none overflow-hidden chat-message-content ${message.sender === 'user' ? 'prose-invert' : ''}`}>
                              <ReactMarkdown
                                remarkPlugins={[remarkGfm]}
                                rehypePlugins={[rehypeHighlight]}
                                components={{
                                  p: ({ children }) => <p className="break-words overflow-hidden">{children}</p>,
                                  code: ({ children }) => <code className="break-words overflow-hidden">{children}</code>,
                                  pre: ({ children }) => <pre className="break-words overflow-x-auto">{children}</pre>
                                }}
                              >
                                {message.text}
                              </ReactMarkdown>
                            </div>
                          )}
                          <p className="text-xs opacity-70 mt-1 break-words">
                            {message.timestamp.toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' })}
                          </p>
                        </div>
                        {message.sender === 'user' && (
                          <User className="h-4 w-4 mt-0.5 text-blue-200 flex-shrink-0" />
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

                {/* Course Preview */}
                {coursePreview && (
                  <div className="mx-4 mb-4 p-4 border border-blue-200 rounded-lg bg-blue-50 shadow flex-shrink-0">
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
                <div className="border-t p-4 flex-shrink-0">
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
            </Card>
          </div>
        </div>
      ) : (
        // Original layout without conversation history
        <Card className="h-full flex flex-col">
      {showHeader && (
            <CardHeader className="pb-3">
          <CardTitle className="flex items-center space-x-2">
            <Bot className="h-5 w-5 text-blue-600" />
            <span>{title}</span>
          </CardTitle>
          <CardDescription>{description}</CardDescription>
        </CardHeader>
      )}
          <CardContent className="p-0 flex-1 flex flex-col overflow-hidden">
        {/* Messages Container */}
        <div className="overflow-y-auto p-4 space-y-4 flex-1" style={{ minHeight: 0 }}>
          {messages.map((message) => (
            <div
              key={message.id}
              className={`flex ${message.sender === 'user' ? 'justify-end' : 'justify-start'}`}
            >
              <div
                className={`
                      flex max-w-[80%] break-words overflow-hidden
                  items-start space-x-2 rounded-lg px-3 py-2
                  ${message.sender === 'user'
                    ? 'bg-blue-600 text-white'
                    : 'bg-gray-100 text-gray-900'}
                `}
              >
                {message.sender === 'ai' && (
                      <Bot className="h-4 w-4 mt-0.5 text-blue-600 flex-shrink-0" />
                )}
                    <div className="flex-1 min-w-0 overflow-hidden">
                  {message.isHtml ? (
                    <div
                      className="text-sm whitespace-pre-wrap break-words overflow-hidden chat-message-content"
                      dangerouslySetInnerHTML={{ __html: message.text }}
                    />
                  ) : (
                    <div className={`prose prose-sm max-w-none overflow-hidden chat-message-content ${message.sender === 'user' ? 'prose-invert' : ''}`}>
                      <ReactMarkdown
                        remarkPlugins={[remarkGfm]}
                        rehypePlugins={[rehypeHighlight]}
                        components={{
                          p: ({ children }) => <p className="break-words overflow-hidden">{children}</p>,
                          code: ({ children }) => <code className="break-words overflow-hidden">{children}</code>,
                          pre: ({ children }) => <pre className="break-words overflow-x-auto">{children}</pre>
                        }}
                      >
                        {message.text}
                      </ReactMarkdown>
                    </div>
                  )}
                  <p className="text-xs opacity-70 mt-1 break-words">
                    {message.timestamp.toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' })}
                  </p>
                </div>
                {message.sender === 'user' && (
                      <User className="h-4 w-4 mt-0.5 text-blue-200 flex-shrink-0" />
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

            {/* Course Preview */}
        {coursePreview && (
              <div className="mx-4 mb-4 p-4 border border-blue-200 rounded-lg bg-blue-50 shadow flex-shrink-0">
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
        <div className="border-t p-4 flex-shrink-0">
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
        </Card>
      )}

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
    </div>
  );
};

export default AIChatAssistant; 