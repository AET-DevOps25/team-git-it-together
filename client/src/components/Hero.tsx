import React from 'react';
import { Link } from 'react-router-dom';
import { Button } from '@/components/ui/button';
import { BookOpen, Users, User } from 'lucide-react';

const Hero = () => {
  return (
    <div className="relative bg-gradient-to-br from-blue-50 to-indigo-100 overflow-hidden">
      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-24">
        <div className="grid lg:grid-cols-2 gap-12 items-center">
          <div className="space-y-8">
            <div className="space-y-4">
              <h1 className="text-5xl lg:text-6xl font-bold text-gray-900 leading-tight">
                Learn <span className="text-blue-600">Anything</span> 
                <br />at Your Own Pace
              </h1>
              <p className="text-xl text-gray-600 leading-relaxed">
                Join thousands of learners on our platform. Access Ai-curated courses,
                track your progress, and build the skills you need to succeed.
              </p>
            </div>
            
            <div className="flex flex-col sm:flex-row gap-4">
              <Link to="/signup">
                <Button size="lg" className="w-full sm:w-auto text-lg px-8 py-3">
                  Start Learning Today
                </Button>
              </Link>
              <Link to="/courses">
                <Button variant="outline" size="lg" className="w-full sm:w-auto text-lg px-8 py-3">
                  Browse Courses
                </Button>
              </Link>
            </div>

            <div className="flex items-center space-x-8 pt-8">
              <div className="flex items-center space-x-2">
                <BookOpen className="h-6 w-6 text-blue-600" />
                <span className="text-gray-700 font-medium">500+ Courses</span>
              </div>
              <div className="flex items-center space-x-2">
                <Users className="h-6 w-6 text-blue-600" />
                <span className="text-gray-700 font-medium">50k+ Students</span>
              </div>
              <div className="flex items-center space-x-2">
                <User className="h-6 w-6 text-blue-600" />
                <span className="text-gray-700 font-medium">AI-Tutor</span>
              </div>
            </div>
          </div>

          <div className="relative">
            <div className="relative z-10 bg-white rounded-2xl shadow-2xl p-8 transform rotate-3 hover:rotate-0 transition-transform duration-300">
              <div className="space-y-4">
                <div className="flex items-center space-x-3">
                  <div className="w-12 h-12 bg-blue-100 rounded-full flex items-center justify-center">
                    <BookOpen className="h-6 w-6 text-blue-600" />
                  </div>
                  <div>
                    <h3 className="font-semibold text-gray-900">React Development</h3>
                    <p className="text-sm text-gray-600">Progress: 75%</p>
                  </div>
                </div>
                <div className="w-full bg-gray-200 rounded-full h-2">
                  <div className="bg-blue-600 h-2 rounded-full" style={{ width: '75%' }}></div>
                </div>
                <div className="grid grid-cols-2 gap-4 pt-4">
                  <div className="bg-gray-50 rounded-lg p-3 text-center">
                    <div className="text-2xl font-bold text-blue-600">12</div>
                    <div className="text-sm text-gray-600">Completed</div>
                  </div>
                  <div className="bg-gray-50 rounded-lg p-3 text-center">
                    <div className="text-2xl font-bold text-green-600">4</div>
                    <div className="text-sm text-gray-600">Remaining</div>
                  </div>
                </div>
              </div>
            </div>
            <div className="absolute top-8 left-8 w-full h-full bg-blue-200 rounded-2xl -z-10"></div>
          </div>
        </div>
      </div>
    </div>
  );
};

export default Hero;
