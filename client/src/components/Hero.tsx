import React from 'react';
import { Link } from 'react-router-dom';
import { Button } from '@/components/ui/button';
import { BookOpen, Users, User } from 'lucide-react';

const Hero = () => {
  return (
    <div className="relative overflow-hidden bg-gradient-to-br from-blue-50 to-indigo-100">
      <div className="mx-auto max-w-7xl px-4 py-24 sm:px-6 lg:px-8">
        <div className="grid items-center gap-12 lg:grid-cols-2">
          <div className="space-y-8">
            <div className="space-y-4">
              <h1 className="text-5xl font-bold leading-tight text-gray-900 lg:text-6xl">
                Learn <span className="text-blue-600">Anything</span>
                <br />
                at Your Own Pace
              </h1>
              <p className="text-xl leading-relaxed text-gray-600">
                Join thousands of learners on our platform. Access Ai-curated courses, track your
                progress, and build the skills you need to succeed.
              </p>
            </div>

            <div className="flex flex-col gap-4 sm:flex-row">
              <Link to="/signup">
                <Button size="lg" className="w-full px-8 py-3 text-lg sm:w-auto">
                  Start Learning Today
                </Button>
              </Link>
              <Link to="/courses">
                <Button variant="outline" size="lg" className="w-full px-8 py-3 text-lg sm:w-auto">
                  Browse Courses
                </Button>
              </Link>
            </div>

            <div className="flex items-center space-x-8 pt-8">
              <div className="flex items-center space-x-2">
                <BookOpen className="h-6 w-6 text-blue-600" />
                <span className="font-medium text-gray-700">500+ Courses</span>
              </div>
              <div className="flex items-center space-x-2">
                <Users className="h-6 w-6 text-blue-600" />
                <span className="font-medium text-gray-700">50k+ Students</span>
              </div>
              <div className="flex items-center space-x-2">
                <User className="h-6 w-6 text-blue-600" />
                <span className="font-medium text-gray-700">AI-Tutor</span>
              </div>
            </div>
          </div>

          <div className="relative">
            <div className="relative z-10 rotate-3 transform rounded-2xl bg-white p-8 shadow-2xl transition-transform duration-300 hover:rotate-0">
              <div className="space-y-4">
                <div className="flex items-center space-x-3">
                  <div className="flex h-12 w-12 items-center justify-center rounded-full bg-blue-100">
                    <BookOpen className="h-6 w-6 text-blue-600" />
                  </div>
                  <div>
                    <h3 className="font-semibold text-gray-900">React Development</h3>
                    <p className="text-sm text-gray-600">Progress: 75%</p>
                  </div>
                </div>
                <div className="h-2 w-full rounded-full bg-gray-200">
                  <div className="h-2 rounded-full bg-blue-600" style={{ width: '75%' }}></div>
                </div>
                <div className="grid grid-cols-2 gap-4 pt-4">
                  <div className="rounded-lg bg-gray-50 p-3 text-center">
                    <div className="text-2xl font-bold text-blue-600">12</div>
                    <div className="text-sm text-gray-600">Completed</div>
                  </div>
                  <div className="rounded-lg bg-gray-50 p-3 text-center">
                    <div className="text-2xl font-bold text-green-600">4</div>
                    <div className="text-sm text-gray-600">Remaining</div>
                  </div>
                </div>
              </div>
            </div>
            <div className="absolute left-8 top-8 -z-10 h-full w-full rounded-2xl bg-blue-200"></div>
          </div>
        </div>
      </div>
    </div>
  );
};

export default Hero;
