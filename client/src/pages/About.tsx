import React, { useState, useEffect } from 'react';
import { Link } from 'react-router-dom';
import { Button } from '@/components/ui/button';
import Navbar from '@/components/Navbar';
import Footer from '@/components/Footer';
import { Github, BookOpen, Users, Sparkles, Target, MessageSquare, Award, Zap, Globe, Code, Database, User } from 'lucide-react';

// GitHub Profile Picture Component
interface GitHubAvatarProps {
  username: string;
  fallbackInitial: string;
  fallbackGradient: string;
  size?: number;
}

const GitHubAvatar: React.FC<GitHubAvatarProps> = ({ 
  username, 
  fallbackInitial, 
  fallbackGradient, 
  size = 80 
}) => {
  const [avatarUrl, setAvatarUrl] = useState<string | null>(null);
  const [isLoading, setIsLoading] = useState(true);
  const [hasError, setHasError] = useState(false);

  useEffect(() => {
    const fetchAvatar = async () => {
      try {
        setIsLoading(true);
        setHasError(false);
        
        // Fetch GitHub user data
        const response = await fetch(`https://api.github.com/users/${username}`);
        
        if (!response.ok) {
          throw new Error('GitHub user not found');
        }
        
        const userData = await response.json();
        setAvatarUrl(userData.avatar_url);
      } catch (error) {
        console.warn(`Failed to fetch GitHub avatar for ${username}:`, error);
        setHasError(true);
      } finally {
        setIsLoading(false);
      }
    };

    if (username) {
      fetchAvatar();
    }
  }, [username]);

  if (isLoading) {
    return (
      <div 
        className={`w-${size/4} h-${size/4} rounded-full mx-auto flex items-center justify-center bg-gray-200 animate-pulse`}
        style={{ width: size, height: size }}
      >
        <User className="h-6 w-6 text-gray-400" />
      </div>
    );
  }

  if (hasError || !avatarUrl) {
    return (
      <div 
        className={`w-${size/4} h-${size/4} rounded-full mx-auto flex items-center justify-center ${fallbackGradient}`}
        style={{ width: size, height: size }}
      >
        <span className="text-white font-semibold text-xl">{fallbackInitial}</span>
      </div>
    );
  }

  return (
    <img
      src={avatarUrl}
      alt={`${username} GitHub profile`}
      className="rounded-full mx-auto object-cover border-2 border-gray-200 hover:border-gray-300 transition-colors"
      style={{ width: size, height: size }}
      onError={() => setHasError(true)}
    />
  );
};

const About = () => {
  const [currentTechIndex, setCurrentTechIndex] = useState(0);

  useEffect(() => {
    const interval = setInterval(() => {
      setCurrentTechIndex((prevIndex) => (prevIndex + 1) % 4);
    }, 5000);

    return () => clearInterval(interval);
  }, []);

  return (
    <div className="min-h-screen bg-white">
      <Navbar />
      
      <div className="mx-auto max-w-4xl px-4 py-16 sm:px-6 lg:px-8">
        <div className="text-center mb-16">
          <h1 className="text-4xl font-bold text-gray-900 mb-4">
            About <span className="text-blue-600">SkillForge</span>
          </h1>
          <p className="text-xl text-gray-600 max-w-3xl mx-auto">
            Empowering learners worldwide with AI-curated courses and personalized learning experiences.
          </p>
          <p className="text-sm text-gray-500 mt-2">
            Last updated: July 2025
          </p>
        </div>

        <div className="space-y-12">
          {/* Project Overview */}
          <div className="space-y-6">
            <h2 className="text-2xl font-semibold text-gray-900">Project Overview</h2>
            <p className="text-gray-600 leading-relaxed">
              SkillForge is an innovative AI-powered learning platform that revolutionizes how people acquire new skills and knowledge. 
              Our mission is to make quality education accessible, personalized, and engaging for everyone, regardless of their location 
              or background.
            </p>
            <p className="text-gray-600 leading-relaxed">
              Built with cutting-edge technologies and modern development practices, SkillForge combines the power of artificial intelligence 
              with comprehensive learning management to create a truly personalized educational experience. Our platform adapts to each 
              learner's pace, preferences, and goals, ensuring maximum engagement and retention.
            </p>
          </div>

          {/* Features Grid */}
          <div className="grid gap-8 md:grid-cols-2">
            {/* Current Features */}
            <div className="space-y-6">
              <h2 className="text-2xl font-semibold text-gray-900">Current Features</h2>
              <div className="space-y-4">
                <div className="flex items-center space-x-3">
                  <Sparkles className="h-5 w-5 text-blue-600" />
                  <span className="text-gray-700">AI-Curated Course Generation</span>
                </div>
                <div className="flex items-center space-x-3">
                  <MessageSquare className="h-5 w-5 text-green-600" />
                  <span className="text-gray-700">Interactive AI Chat Assistant</span>
                </div>
                <div className="flex items-center space-x-3">
                  <Target className="h-5 w-5 text-purple-600" />
                  <span className="text-gray-700">Personalized Learning Paths</span>
                </div>
                <div className="flex items-center space-x-3">
                  <Award className="h-5 w-5 text-orange-600" />
                  <span className="text-gray-700">Achievement & Badge System</span>
                </div>
                <div className="flex items-center space-x-3">
                  <BookOpen className="h-5 w-5 text-red-600" />
                  <span className="text-gray-700">Course Bookmarking</span>
                </div>
                <div className="flex items-center space-x-3">
                  <Zap className="h-5 w-5 text-yellow-600" />
                  <span className="text-gray-700">Real-time Progress Updates</span>
                </div>
              </div>
            </div>

            {/* Planned Features */}
            <div className="space-y-6">
              <h2 className="text-2xl font-semibold text-gray-900">Planned Features</h2>
              <div className="space-y-4">
                <div className="flex items-center space-x-3">
                  <Users className="h-5 w-5 text-gray-400" />
                  <span className="text-gray-500">Global Learning Community</span>
                  <span className="inline-flex items-center rounded-full bg-gradient-to-r from-purple-100 to-indigo-100 px-2 py-1 text-xs font-medium text-purple-700 border border-purple-200">
                    Coming Soon
                  </span>
                </div>
                <div className="flex items-center space-x-3">
                  <Globe className="h-5 w-5 text-gray-400" />
                  <span className="text-gray-500">Multi-language Support</span>
                  <span className="inline-flex items-center rounded-full bg-gradient-to-r from-purple-100 to-indigo-100 px-2 py-1 text-xs font-medium text-purple-700 border border-purple-200">
                    Coming Soon
                  </span>
                </div>
                <div className="flex items-center space-x-3">
                  <Database className="h-5 w-5 text-gray-400" />
                  <span className="text-gray-500">Advanced Analytics Dashboard</span>
                  <span className="inline-flex items-center rounded-full bg-gradient-to-r from-purple-100 to-indigo-100 px-2 py-1 text-xs font-medium text-purple-700 border border-purple-200">
                    Coming Soon
                  </span>
                </div>
                <div className="flex items-center space-x-3">
                  <Code className="h-5 w-5 text-gray-400" />
                  <span className="text-gray-500">Interactive Code Playground</span>
                  <span className="inline-flex items-center rounded-full bg-gradient-to-r from-purple-100 to-indigo-100 px-2 py-1 text-xs font-medium text-purple-700 border border-purple-200">
                    Coming Soon
                  </span>
                </div>
                <div className="flex items-center space-x-3">
                  <Award className="h-5 w-5 text-gray-400" />
                  <span className="text-gray-500">Certification Programs</span>
                  <span className="inline-flex items-center rounded-full bg-gradient-to-r from-purple-100 to-indigo-100 px-2 py-1 text-xs font-medium text-purple-700 border border-purple-200">
                    Coming Soon
                  </span>
                </div>
              </div>
            </div>
          </div>
        </div>

        {/* Tech Stack */}
        <div className="mt-16 space-y-6">
          <h2 className="text-2xl font-semibold text-gray-900 text-center">Technology Stack</h2>
          <div className="relative max-w-2xl mx-auto">
            <div className="overflow-hidden rounded-xl">
              <div className="flex transition-transform duration-500 ease-in-out" style={{ transform: `translateX(-${currentTechIndex * 100}%)` }}>
                <div className="w-full flex-shrink-0 px-2">
                  <div className="bg-white rounded-xl p-6 shadow-lg border border-gray-200">
                    <div className="flex items-center mb-4">
                      <div className="w-10 h-10 bg-blue-500 rounded-lg flex items-center justify-center mr-3">
                        <Code className="h-5 w-5 text-white" />
                      </div>
                      <h3 className="font-bold text-gray-900 text-lg">Frontend</h3>
                    </div>
                    <div className="grid grid-cols-2 gap-2 text-sm">
                      <span className="bg-gray-100 rounded-lg px-3 py-1 text-gray-700">React + TS</span>
                      <span className="bg-gray-100 rounded-lg px-3 py-1 text-gray-700">Tailwind CSS</span>
                      <span className="bg-gray-100 rounded-lg px-3 py-1 text-gray-700">Shadcn/ui</span>
                      <span className="bg-gray-100 rounded-lg px-3 py-1 text-gray-700">React Router</span>
                    </div>
                  </div>
                </div>
                <div className="w-full flex-shrink-0 px-2">
                  <div className="bg-white rounded-xl p-6 shadow-lg border border-gray-200">
                    <div className="flex items-center mb-4">
                      <div className="w-10 h-10 bg-green-500 rounded-lg flex items-center justify-center mr-3">
                        <Database className="h-5 w-5 text-white" />
                      </div>
                      <h3 className="font-bold text-gray-900 text-lg">Backend</h3>
                    </div>
                    <div className="grid grid-cols-2 gap-2 text-sm">
                      <span className="bg-gray-100 rounded-lg px-3 py-1 text-gray-700">Spring Boot</span>
                      <span className="bg-gray-100 rounded-lg px-3 py-1 text-gray-700">MongoDB</span>
                      <span className="bg-gray-100 rounded-lg px-3 py-1 text-gray-700">JWT Auth</span>
                      <span className="bg-gray-100 rounded-lg px-3 py-1 text-gray-700">REST APIs</span>
                    </div>
                  </div>
                </div>
                <div className="w-full flex-shrink-0 px-2">
                  <div className="bg-white rounded-xl p-6 shadow-lg border border-gray-200">
                    <div className="flex items-center mb-4">
                      <div className="w-10 h-10 bg-purple-500 rounded-lg flex items-center justify-center mr-3">
                        <Sparkles className="h-5 w-5 text-white" />
                      </div>
                      <h3 className="font-bold text-gray-900 text-lg">GenAI & AI</h3>
                    </div>
                    <div className="grid grid-cols-2 gap-2 text-sm">
                      <span className="bg-gray-100 rounded-lg px-3 py-1 text-gray-700">Python</span>
                      <span className="bg-gray-100 rounded-lg px-3 py-1 text-gray-700">OpenAI GPT</span>
                      <span className="bg-gray-100 rounded-lg px-3 py-1 text-gray-700">Weaviate</span>
                      <span className="bg-gray-100 rounded-lg px-3 py-1 text-gray-700">RAG</span>
                    </div>
                  </div>
                </div>
                <div className="w-full flex-shrink-0 px-2">
                  <div className="bg-white rounded-xl p-6 shadow-lg border border-gray-200">
                    <div className="flex items-center mb-4">
                      <div className="w-10 h-10 bg-orange-500 rounded-lg flex items-center justify-center mr-3">
                        <Zap className="h-5 w-5 text-white" />
                      </div>
                      <h3 className="font-bold text-gray-900 text-lg">Infrastructure</h3>
                    </div>
                    <div className="grid grid-cols-2 gap-2 text-sm">
                      <span className="bg-gray-100 rounded-lg px-3 py-1 text-gray-700">Docker</span>
                      <span className="bg-gray-100 rounded-lg px-3 py-1 text-gray-700">Kubernetes</span>
                      <span className="bg-gray-100 rounded-lg px-3 py-1 text-gray-700">AWS</span>
                      <span className="bg-gray-100 rounded-lg px-3 py-1 text-gray-700">CI/CD</span>
                    </div>
                  </div>
                </div>
              </div>
            </div>
            
            {/* Navigation Dots */}
            <div className="flex justify-center mt-6 space-x-3">
              {[0, 1, 2, 3].map((index) => (
                <button
                  key={index}
                  onClick={() => setCurrentTechIndex(index)}
                  className={`w-4 h-4 rounded-full transition-all duration-300 ${
                    currentTechIndex === index 
                      ? 'bg-blue-600 scale-110 shadow-lg' 
                      : 'bg-gray-300 hover:bg-gray-400'
                  }`}
                />
              ))}
            </div>
          </div>
        </div>

        {/* Key Statistics */}
        <div className="mt-16 space-y-6">
          <h2 className="text-2xl font-semibold text-gray-900 text-center">Platform Statistics</h2>
          <div className="grid gap-6 md:grid-cols-4">
            <div className="text-center space-y-2">
              <div className="text-3xl font-bold text-blue-600">100+</div>
              <div className="text-sm text-gray-600">AI-Generated Courses</div>
            </div>
            <div className="text-center space-y-2">
              <div className="text-3xl font-bold text-green-600">50+</div>
              <div className="text-sm text-gray-600">Learning Categories</div>
            </div>
            <div className="text-center space-y-2">
              <div className="text-3xl font-bold text-purple-600">1000+</div>
              <div className="text-sm text-gray-600">Interactive Lessons</div>
            </div>
            <div className="text-center space-y-2">
              <div className="text-3xl font-bold text-orange-600">24/7</div>
              <div className="text-sm text-gray-600">AI Assistant Support</div>
            </div>
          </div>
        </div>

        {/* Open Source */}
        <div className="mt-16 space-y-6">
          <h2 className="text-2xl font-semibold text-gray-900 text-center">Open Source</h2>
          <div className="text-center space-y-4">
            <p className="text-gray-600 max-w-2xl mx-auto">
              SkillForge is an open-source project developed by the git-it-together team. 
              We believe in the power of collaboration, transparency, and community-driven development 
              to create the best possible learning experience for everyone.
            </p>
            <div className="flex flex-col sm:flex-row gap-4 justify-center">
              <a 
                href="https://github.com/AET-DevOps25/team-git-it-together" 
                target="_blank" 
                rel="noopener noreferrer"
                className="inline-flex items-center justify-center space-x-2 rounded-lg bg-gray-900 px-6 py-3 text-white transition-colors hover:bg-gray-800"
              >
                <Github className="h-5 w-5" />
                <span>View on GitHub</span>
              </a>
              <Link to="/">
                <Button variant="outline" size="lg">
                  Back to Home
                </Button>
              </Link>
            </div>
          </div>
        </div>

        {/* Team Info */}
        <div className="mt-16 space-y-6">
          <h2 className="text-2xl font-semibold text-gray-900 text-center">Development Team</h2>
          <div className="grid gap-8 md:grid-cols-2 max-w-3xl mx-auto">
            <div className="text-center space-y-4">
              <GitHubAvatar 
                username="GravityDarkLab"
                fallbackInitial="A"
                fallbackGradient="bg-gradient-to-r from-blue-500 to-purple-600"
                size={80}
              />
              <div>
                <h3 className="font-semibold text-gray-900 text-lg">Achraf Labidi</h3>
                <p className="text-sm text-gray-600">Full Stack Developer</p>
              </div>
              <div className="flex justify-center space-x-3">
                <a href="https://github.com/GravityDarkLab" target="_blank" rel="noopener noreferrer" className="text-gray-400 hover:text-gray-600">
                  <Github className="h-5 w-5" />
                </a>
              </div>
            </div>
            <div className="text-center space-y-4">
              <GitHubAvatar 
                username="mahdibayouli"
                fallbackInitial="M"
                fallbackGradient="bg-gradient-to-r from-green-500 to-blue-600"
                size={80}
              />
              <div>
                <h3 className="font-semibold text-gray-900 text-lg">Mahdi Bayouli</h3>
                <p className="text-sm text-gray-600">Full Stack Developer</p>
              </div>
              <div className="flex justify-center space-x-3">
                <a href="https://github.com/mahdibayouli" target="_blank" rel="noopener noreferrer" className="text-gray-400 hover:text-gray-600">
                  <Github className="h-5 w-5" />
                </a>
              </div>
            </div>
          </div>
        </div>

        {/* Mission Statement */}
        <div className="mt-16 space-y-6">
          <h2 className="text-2xl font-semibold text-gray-900 text-center">Our Mission</h2>
          <div className="text-center space-y-4">
            <p className="text-gray-600 max-w-3xl mx-auto leading-relaxed">
              To democratize education by leveraging artificial intelligence to create personalized, 
              engaging, and accessible learning experiences for everyone. We believe that knowledge 
              should be free, learning should be fun, and everyone deserves the opportunity to grow 
              and develop their skills regardless of their background or circumstances.
            </p>
            <p className="text-gray-600 max-w-3xl mx-auto leading-relaxed">
              Through continuous innovation and community collaboration, we're building the future 
              of education—one that adapts to individual needs, celebrates progress, and empowers 
              learners to achieve their full potential.
            </p>
          </div>
        </div>
      </div>

      <Footer />
    </div>
  );
};

export default About; 