import React, { useState } from 'react';
import { Button } from '@/components/ui/button';
import { Input } from '@/components/ui/input';
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from '@/components/ui/card';
import { Tabs, TabsContent, TabsList, TabsTrigger } from '@/components/ui/tabs';
import { Label } from '@/components/ui/label';
import { Badge } from '@/components/ui/badge';
import { 
  MessageSquare, 
  Globe, 
  Upload, 
  Sparkles,
  FileText,
  Image as ImageIcon
} from 'lucide-react';
import Navbar from '@/components/Navbar';
import Footer from '@/components/Footer';
import AIChatAssistant from '@/components/AIChatAssistant';
import { useToast } from '@/hooks/use-toast';

const AiCenter = () => {
  const { toast } = useToast();
  const [urlInput, setUrlInput] = useState('');
  const [isAnalyzingUrl, setIsAnalyzingUrl] = useState(false);
  const [uploadedFile, setUploadedFile] = useState<File | null>(null);
  const [isAnalyzingFile, setIsAnalyzingFile] = useState(false);

  const handleUrlAnalysis = async () => {
    if (!urlInput.trim()) {
      toast({
        title: "URL Required",
        description: "Please enter a URL to analyze.",
        variant: "destructive"
      });
      return;
    }

    setIsAnalyzingUrl(true);
    
    // Simulate URL analysis
    setTimeout(() => {
      toast({
        title: "URL Analysis Complete",
        description: `Analysis of ${urlInput} completed. This would connect to your web crawling service.`,
        variant: "default"
      });
      setIsAnalyzingUrl(false);
      setUrlInput('');
    }, 2000);
  };

  const handleFileUpload = (event: React.ChangeEvent<HTMLInputElement>) => {
    const file = event.target.files?.[0];
    if (file) {
      setUploadedFile(file);
    }
  };

  const handleFileAnalysis = async () => {
    if (!uploadedFile) {
      toast({
        title: "File Required",
        description: "Please upload a file to analyze.",
        variant: "destructive"
      });
      return;
    }

    setIsAnalyzingFile(true);
    
    // Simulate file analysis
    setTimeout(() => {
      toast({
        title: "File Analysis Complete",
        description: `Analysis of ${uploadedFile.name} completed. This would connect to your AI service for document/image analysis.`,
        variant: "default"
      });
      setIsAnalyzingFile(false);
      setUploadedFile(null);
    }, 2000);
  };

  const getFileIcon = (fileName: string) => {
    const extension = fileName.split('.').pop()?.toLowerCase();
    if (['pdf'].includes(extension || '')) return <FileText className="h-5 w-5" />;
    if (['jpg', 'jpeg', 'png', 'gif', 'webp'].includes(extension || '')) return <ImageIcon className="h-5 w-5" />;
    return <FileText className="h-5 w-5" />;
  };

  return (
    <div className="min-h-screen bg-gray-50">
      <Navbar />
      
      <div className="mx-auto max-w-7xl px-4 py-8 sm:px-6 lg:px-8">
        <div className="mb-8">
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

        <Tabs defaultValue="chat" className="space-y-6">
          <TabsList className="grid w-full grid-cols-3">
            <TabsTrigger value="chat" className="flex items-center space-x-2">
              <MessageSquare className="h-4 w-4" />
              <span>AI Chat</span>
            </TabsTrigger>
            <TabsTrigger value="url" className="flex items-center space-x-2">
              <Globe className="h-4 w-4" />
              <span>URL Analysis</span>
            </TabsTrigger>
            <TabsTrigger value="upload" className="flex items-center space-x-2">
              <Upload className="h-4 w-4" />
              <span>File Analysis</span>
            </TabsTrigger>
          </TabsList>

          {/* AI Chat Tab */}
          <TabsContent value="chat" className="space-y-4">
            <AIChatAssistant
              height="h-96"
              showHeader={true}
            />
          </TabsContent>

          {/* URL Analysis Tab */}
          <TabsContent value="url" className="space-y-4">
            <Card>
              <CardHeader>
                <CardTitle className="flex items-center space-x-2">
                  <Globe className="h-5 w-5 text-blue-600" />
                  <span>Web Page Analysis</span>
                </CardTitle>
                <CardDescription>
                  Enter a URL to analyze and extract learning content
                </CardDescription>
              </CardHeader>
              <CardContent className="space-y-4">
                <div className="space-y-2">
                  <Label htmlFor="url">Website URL</Label>
                  <Input
                    id="url"
                    type="url"
                    placeholder="https://example.com"
                    value={urlInput}
                    onChange={(e) => setUrlInput(e.target.value)}
                  />
                </div>
                <Button 
                  onClick={handleUrlAnalysis} 
                  disabled={!urlInput.trim() || isAnalyzingUrl}
                  className="w-full"
                >
                  {isAnalyzingUrl ? (
                    <>
                      <div className="animate-spin rounded-full h-4 w-4 border-b-2 border-white mr-2"></div>
                      Analyzing...
                    </>
                  ) : (
                    <>
                      <Sparkles className="h-4 w-4 mr-2" />
                      Analyze URL
                    </>
                  )}
                </Button>
              </CardContent>
            </Card>
          </TabsContent>

          {/* File Upload Tab */}
          <TabsContent value="upload" className="space-y-4">
            <Card>
              <CardHeader>
                <CardTitle className="flex items-center space-x-2">
                  <Upload className="h-5 w-5 text-green-600" />
                  <span>Document & Image Analysis</span>
                </CardTitle>
                <CardDescription>
                  Upload PDFs or images to get AI-powered summaries and insights
                </CardDescription>
              </CardHeader>
              <CardContent className="space-y-4">
                <div className="space-y-2">
                  <Label htmlFor="file">Upload File</Label>
                  <div className="border-2 border-dashed border-gray-300 rounded-lg p-6 text-center">
                    <input
                      type="file"
                      id="file"
                      accept=".pdf,.jpg,.jpeg,.png,.gif,.webp"
                      onChange={handleFileUpload}
                      className="hidden"
                    />
                    <label htmlFor="file" className="cursor-pointer">
                      <Upload className="h-8 w-8 text-gray-400 mx-auto mb-2" />
                      <p className="text-sm text-gray-600">
                        Click to upload or drag and drop
                      </p>
                      <p className="text-xs text-gray-500 mt-1">
                        PDF, JPG, PNG, GIF, WEBP (max 10MB)
                      </p>
                    </label>
                  </div>
                </div>
                
                {uploadedFile && (
                  <div className="flex items-center space-x-2 p-3 bg-gray-50 rounded-lg">
                    {getFileIcon(uploadedFile.name)}
                    <span className="text-sm font-medium">{uploadedFile.name}</span>
                    <Badge variant="secondary">
                      {(uploadedFile.size / 1024 / 1024).toFixed(2)} MB
                    </Badge>
                  </div>
                )}

                <Button 
                  onClick={handleFileAnalysis} 
                  disabled={!uploadedFile || isAnalyzingFile}
                  className="w-full"
                >
                  {isAnalyzingFile ? (
                    <>
                      <div className="animate-spin rounded-full h-4 w-4 border-b-2 border-white mr-2"></div>
                      Analyzing...
                    </>
                  ) : (
                    <>
                      <Sparkles className="h-4 w-4 mr-2" />
                      Analyze File
                    </>
                  )}
                </Button>
              </CardContent>
            </Card>
          </TabsContent>
        </Tabs>
      </div>

      <Footer />
    </div>
  );
};

export default AiCenter; 