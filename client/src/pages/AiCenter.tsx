import React, { useState } from 'react';
import { Button } from '@/components/ui/button';
import { Input } from '@/components/ui/input';
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from '@/components/ui/card';
import { Tabs, TabsContent, TabsList, TabsTrigger } from '@/components/ui/tabs';
import { Label } from '@/components/ui/label';
import { Badge } from '@/components/ui/badge';
import { 
  MessageSquare, 
  Upload, 
  Sparkles,
  Heart
} from 'lucide-react';
import Navbar from '@/components/Navbar';
import AIChatAssistant from '@/components/AIChatAssistant';
import { useToast } from '@/hooks/use-toast';

const AiCenter = () => {
  const { toast } = useToast();
  const [urlInput, setUrlInput] = useState('');
  const [isAnalyzingUrl, setIsAnalyzingUrl] = useState(false);

  // URL validation function
  const isValidUrl = (url: string): boolean => {
    try {
      new URL(url);
      return true;
    } catch {
      return false;
    }
  };

  const handleUrlAnalysis = async () => {
    if (!urlInput.trim()) {
      toast({
        title: "URL Required",
        description: "Please enter a URL to contribute to our knowledge base.",
        variant: "destructive"
      });
      return;
    }

    if (!isValidUrl(urlInput)) {
      toast({
        title: "Invalid URL",
        description: "Please enter a valid URL (e.g., https://example.com)",
        variant: "destructive"
      });
      return;
    }

    setIsAnalyzingUrl(true);
    
    try {
      // Call the crawlAndEmbedUrl service
      // This would be your actual API call to the backend
      // await crawlAndEmbedUrl(urlInput);
      
      // Simulate the API call for now
      await new Promise(resolve => setTimeout(resolve, 3000));
      
      toast({
        title: "🎉 Thank You for Contributing!",
        description: "Your URL has been successfully analyzed and added to the SkillForge knowledge base. This will help improve our AI's learning capabilities for everyone!",
        variant: "default"
      });
      
      setUrlInput('');
    } catch {
      toast({
        title: "Analysis Failed",
        description: "There was an error analyzing the URL. Please try again.",
        variant: "destructive"
      });
    } finally {
      setIsAnalyzingUrl(false);
    }
  };

  return (
    <div className="min-h-screen bg-gray-50">
      <Navbar />
      
      <div className="mx-auto max-w-7xl px-4 py-6 sm:px-6 lg:px-8">
        <div className="mb-6">
          <div className="flex items-center space-x-3 mb-4">
            <div className="flex h-12 w-12 items-center justify-center rounded-full bg-gradient-to-r from-purple-100 to-blue-100">
              <Sparkles className="h-6 w-6 text-purple-600" />
            </div>
            <div>
              <h1 className="text-3xl font-bold text-gray-900">AI Center</h1>
              <p className="text-gray-600">Your personal AI learning assistant</p>
            </div>
          </div>
        </div>

        <Tabs defaultValue="chat" className="space-y-4">
          <TabsList className="grid w-full grid-cols-3">
            <TabsTrigger value="chat" className="flex items-center space-x-2">
              <MessageSquare className="h-4 w-4" />
              <span>AI Chat</span>
            </TabsTrigger>
            <TabsTrigger value="url" className="flex items-center space-x-2">
              <Heart className="h-4 w-4" />
              <span>Knowledge Contribution</span>
            </TabsTrigger>
            <TabsTrigger value="upload" className="flex items-center space-x-2 opacity-50 cursor-not-allowed" disabled>
              <Upload className="h-4 w-4" />
              <span>File Analysis</span>
              <Badge variant="secondary" className="ml-1 text-xs">Coming Soon</Badge>
            </TabsTrigger>
          </TabsList>

          {/* AI Chat Tab */}
          <TabsContent value="chat" className="space-y-3">
            <Card>
              <CardHeader className="pb-3">
                <CardTitle className="flex items-center space-x-2">
                  <MessageSquare className="h-5 w-5 text-blue-600" />
                  <span>Context-Aware AI Chat</span>
                </CardTitle>
                <CardDescription>
                  Chat with our AI assistant that remembers your conversation history and maintains context across messages.
                </CardDescription>
              </CardHeader>
              <CardContent className="space-y-2">
                <div className="grid grid-cols-1 md:grid-cols-3 gap-3 text-sm">
                  <div className="flex items-start space-x-2">
                    <div className="w-2 h-2 bg-blue-500 rounded-full mt-2 flex-shrink-0"></div>
                    <div>
                      <p className="font-medium">Conversation History</p>
                      <p className="text-muted-foreground">View and switch between your previous conversations</p>
                    </div>
                  </div>
                  <div className="flex items-start space-x-2">
                    <div className="w-2 h-2 bg-green-500 rounded-full mt-2 flex-shrink-0"></div>
                    <div>
                      <p className="font-medium">Context Memory</p>
                      <p className="text-muted-foreground">AI remembers previous messages and maintains context</p>
                    </div>
                  </div>
                  <div className="flex items-start space-x-2">
                    <div className="w-2 h-2 bg-purple-500 rounded-full mt-2 flex-shrink-0"></div>
                    <div>
                      <p className="font-medium">Manage Conversations</p>
                      <p className="text-muted-foreground">Delete individual conversations or clear all history</p>
                    </div>
                  </div>
                </div>
              </CardContent>
            </Card>
            <AIChatAssistant 
              className="h-[800px]"
              title="Context-Aware AI Chat"
              description="Chat with our AI assistant that remembers your conversation history and maintains context across messages."
              placeholder="Ask questions, get explanations, or generate courses..."
              useNewChatService={true}
              disableCourseGeneration={false}
              showConversationHistory={true}
            />
          </TabsContent>

          {/* Knowledge Contribution Tab */}
          <TabsContent value="url" className="space-y-4">
            <Card>
              <CardHeader>
                <CardTitle className="flex items-center space-x-2">
                  <Heart className="h-5 w-5 text-red-600" />
                  <span>Contribute to Knowledge Base</span>
                </CardTitle>
                <CardDescription>
                  Share educational content by contributing URLs to expand SkillForge's knowledge base
                </CardDescription>
              </CardHeader>
              <CardContent className="space-y-4">
                <div className="space-y-2">
                  <Label htmlFor="url">Educational Website URL</Label>
                  <Input
                    id="url"
                    type="url"
                    placeholder="https://example.com/educational-content"
                    value={urlInput}
                    onChange={(e) => setUrlInput(e.target.value)}
                  />
                  <p className="text-sm text-gray-500">
                    Share educational articles, tutorials, documentation, or learning resources
                  </p>
                </div>
                <Button 
                  onClick={handleUrlAnalysis} 
                  disabled={!urlInput.trim() || isAnalyzingUrl}
                  className="w-full"
                >
                  {isAnalyzingUrl ? (
                    <>
                      <div className="animate-spin rounded-full h-4 w-4 border-b-2 border-white mr-2"></div>
                      Analyzing & Contributing...
                    </>
                  ) : (
                    <>
                      <Heart className="h-4 w-4 mr-2" />
                      Contribute URL
                    </>
                  )}
                </Button>
                <div className="text-center text-sm text-gray-500">
                  <p>Your contributions help improve AI learning for everyone! 🚀</p>
                </div>
              </CardContent>
            </Card>
          </TabsContent>

          {/* File Upload Tab - Coming Soon */}
          <TabsContent value="upload" className="space-y-4">
            <Card className="opacity-50">
              <CardHeader>
                <CardTitle className="flex items-center space-x-2">
                  <Upload className="h-5 w-5 text-green-600" />
                  <span>File Analysis</span>
                  <Badge variant="secondary">Coming Soon</Badge>
                </CardTitle>
                <CardDescription>
                  Upload documents or images for AI analysis and learning content extraction
                </CardDescription>
              </CardHeader>
              <CardContent className="space-y-4">
                <div className="text-center py-12">
                  <Upload className="h-16 w-16 text-gray-400 mx-auto mb-4" />
                  <h3 className="text-lg font-semibold text-gray-900 mb-2">File Analysis Coming Soon</h3>
                  <p className="text-gray-500 mb-4">
                    We're working on bringing you the ability to upload and analyze documents, PDFs, and images.
                      </p>
                  <p className="text-sm text-gray-400">
                    This feature will allow you to extract learning content from your files and contribute to our knowledge base.
                  </p>
                </div>
              </CardContent>
            </Card>
          </TabsContent>
        </Tabs>
      </div>
    </div>
  );
};

export default AiCenter; 