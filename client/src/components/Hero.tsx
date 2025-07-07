import React from 'react';
import { Link } from 'react-router-dom';
import { Button } from '@/components/ui/button';
import { BookOpen, Globe, Sparkles } from 'lucide-react';

const Hero = () => {
  return (
    <div className="relative overflow-hidden bg-gradient-to-br from-blue-50 to-indigo-100">
      <div className="mx-auto max-w-7xl px-4 py-24 sm:px-6 lg:px-8">
        <div className="grid items-center gap-12 lg:grid-cols-2">
          <div className="space-y-8">
            <div className="space-y-4">
              <h1 className="text-5xl font-bold leading-tight text-gray-900 lg:text-6xl">
                Master Skills with <span className="text-blue-600 whitespace-nowrap">AI-Driven</span> <span className="text-purple-600">Learning</span>
              </h1>
              <p className="text-xl leading-relaxed text-gray-600">
                Experience the future of learning with AI-curated courses and personalized learning experiences. 
                Join a global community of learners discovering their potential.
              </p>
            </div>

            <div className="flex flex-col gap-4 sm:flex-row">
              <Link to="/signup">
                <Button size="lg" className="w-full px-8 py-3 text-lg sm:w-auto">
                  Start Your Journey
                </Button>
              </Link>
              <Link to="/courses">
                <Button variant="outline" size="lg" className="w-full px-8 py-3 text-lg sm:w-auto">
                  Explore Courses
                </Button>
              </Link>
            </div>

            <div className="flex items-center space-x-8 pt-8">
              <div className="flex items-center space-x-2">
                <BookOpen className="h-6 w-6 text-blue-600" />
                <span className="font-medium text-gray-700">AI-Curated Courses</span>
              </div>
              <div className="flex items-center space-x-2">
                <Sparkles className="h-6 w-6 text-indigo-600" />
                <span className="font-medium text-gray-700">Personalized Learning</span>
              </div>
              <div className="flex items-center space-x-2">
                <Globe className="h-6 w-6 text-purple-600" />
                <div className="flex flex-col">
                  <span className="font-medium text-gray-700">Global Community</span>
                  <span className="inline-flex items-center rounded-full bg-gradient-to-r from-purple-100 to-indigo-100 px-2 py-1 text-xs font-medium text-purple-700 border border-purple-200 w-fit">
                    Coming Soon
                  </span>
                </div>
              </div>
            </div>
          </div>

          <div className="relative">
            <div className="relative z-10 rotate-3 transform rounded-2xl bg-white p-8 shadow-2xl transition-transform duration-300 hover:rotate-0">
              <div className="space-y-4">
                <div className="flex items-center space-x-3">
                  <div className="flex h-12 w-12 items-center justify-center rounded-full bg-gradient-to-r from-blue-100 to-purple-100">
                    <Sparkles className="h-6 w-6 text-blue-600" />
                  </div>
                  <div>
                    <h3 className="font-semibold text-gray-900">AI-Powered Learning</h3>
                    <p className="text-sm text-gray-600">Personalized Path</p>
                  </div>
                </div>
                <div className="h-2 w-full rounded-full bg-gray-200 overflow-hidden">
                  <div 
                    className="h-2 rounded-full bg-gradient-to-r from-blue-600 to-purple-600 animate-pulse"
                    style={{ 
                      width: '75%',
                      animation: 'progressFill 2s ease-out forwards'
                    }}
                  ></div>
                </div>
                <style dangerouslySetInnerHTML={{ __html: `
                  @keyframes progressFill {
                    from { width: 0%; }
                    to { width: 75%; }
                  }
                `}} />
                <div className="grid grid-cols-2 gap-4 pt-4">
                  <div className="rounded-lg bg-gradient-to-r from-blue-50 to-purple-50 p-3 text-center">
                    <div className="text-2xl font-bold text-blue-600">15</div>
                    <div className="text-sm text-gray-600">Skills Mastered</div>
                  </div>
                  <div className="rounded-lg bg-gradient-to-r from-purple-50 to-indigo-50 p-3 text-center">
                    <div className="text-2xl font-bold text-purple-600">8</div>
                    <div className="text-sm text-gray-600">Courses Completed</div>
                  </div>
                </div>
              </div>
            </div>
            <div className="absolute left-8 top-8 -z-10 h-full w-full rounded-2xl bg-gradient-to-r from-blue-200 to-purple-200"></div>
          </div>
        </div>
      </div>
    </div>
  );
};

export default Hero;
