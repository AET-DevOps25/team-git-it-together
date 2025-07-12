import { BookOpen, Users, Sparkles, Target } from 'lucide-react';
import { SiGithub } from 'react-icons/si';
import { APP_NAME } from '@/constants/app';
import { Link } from 'react-router-dom';
import React from 'react';

const Footer = () => {
  return (
    <footer className="bg-gray-900 py-12 text-white">
      <div className="mx-auto max-w-7xl px-4 sm:px-6 lg:px-8">
        <div className="grid gap-8 md:grid-cols-4">
          <div className="col-span-2">
            <div className="mb-4 flex items-center space-x-2">
              <div className="flex h-8 w-8 items-center justify-center rounded-lg bg-gradient-to-r from-blue-600 to-purple-600 shadow-md transition hover:from-blue-700 hover:to-purple-700">
                <BookOpen className="h-5 w-5 text-white" />
              </div>
              <span className="text-xl font-bold">{APP_NAME}</span>
            </div>
            <p className="mb-4 text-gray-400 leading-relaxed">
              Empowering learners worldwide with AI-curated courses and personalized learning experiences. 
              Join our community and master new skills with confidence.
            </p>
            <div className="flex space-x-4">
              <a
                href="https://github.com/AET-DevOps25/team-git-it-together"
                target="_blank"
                rel="noopener noreferrer"
                className="flex items-center space-x-2 text-gray-400 transition-colors hover:text-white"
              >
                <SiGithub className="h-5 w-5" />
                <span>GitHub</span>
              </a>
            </div>
          </div>

          <div>
            <h3 className="mb-4 font-semibold text-white">Quick Links</h3>
            <ul className="space-y-3 text-gray-400">
              <li>
                <Link to="/" className="transition-colors hover:text-white">
                  Home
                </Link>
              </li>
              <li>
                <Link to="/courses" className="transition-colors hover:text-white">
                  Courses
                </Link>
              </li>
              <li>
                <Link to="/about" className="transition-colors hover:text-white">
                  About
                </Link>
              </li>
            </ul>
          </div>

          <div>
            <h3 className="mb-4 font-semibold text-white">Features</h3>
            <ul className="space-y-3 text-gray-400">
              <li className="flex items-center space-x-2">
                <Sparkles className="h-4 w-4 text-blue-400" />
                <span>AI-Curated Courses</span>
              </li>
              <li className="flex items-center space-x-2">
                <Target className="h-4 w-4 text-green-400" />
                <span>Personalized Learning</span>
              </li>
              <li className="flex items-center space-x-2">
                <Users className="h-4 w-4 text-purple-400" />
                <span>Achievement System</span>
              </li>
              <li className="flex items-center space-x-2">
                <BookOpen className="h-4 w-4 text-orange-400" />
                <span>Progress Tracking</span>
              </li>
            </ul>
          </div>
        </div>

        <div className="mt-12 border-t border-gray-800 pt-8">
          <div className="flex flex-col md:flex-row justify-between items-center space-y-4 md:space-y-0">
            <div className="text-center md:text-left">
              <p className="text-gray-400">
                &copy; {new Date().getFullYear()} {APP_NAME}. All rights reserved.
              </p>
            </div>
            <div className="flex space-x-6 text-sm text-gray-400">
              <Link to="/privacy" className="transition-colors hover:text-white">
                Privacy Policy
              </Link>
              <Link to="/terms" className="transition-colors hover:text-white">
                Terms of Service
              </Link>
              <Link to="/cookies" className="transition-colors hover:text-white">
                Cookie Policy
              </Link>
            </div>
          </div>
        </div>
      </div>
    </footer>
  );
};

export default Footer;
