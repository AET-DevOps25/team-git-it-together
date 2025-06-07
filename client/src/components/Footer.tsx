import { BookOpen } from 'lucide-react';
import { APP_NAME } from '@/constants/app.ts';
import React from 'react';

const Footer = () => {
  return (
    <footer className="bg-gray-900 py-12 text-white">
      <div className="mx-auto max-w-7xl px-4 sm:px-6 lg:px-8">
        <div className="grid gap-8 md:grid-cols-4">
          <div className="col-span-2">
            <div className="mb-4 flex items-center space-x-2">
              <div className="flex h-8 w-8 items-center justify-center rounded-lg bg-blue-600 shadow-md transition hover:bg-blue-700">
                <BookOpen className="h-5 w-5 text-white" />
              </div>
              <span className="text-xl font-bold">{APP_NAME}</span>
            </div>
            <p className="mb-4 text-gray-400">
              Empowering learners worldwide with Ai-curated courses and personalized learning
              experiences.
            </p>
            <div className="flex space-x-4">
              <a
                href="https://github.com/AET-DevOps25/team-git-it-together"
                className="text-gray-400 transition-colors hover:text-white"
              >
                GitHub
              </a>
            </div>
          </div>

          <div>
            <h3 className="mb-4 font-semibold">Platform</h3>
            <ul className="space-y-2 text-gray-400">
              <li>
                <a href="#" className="transition-colors hover:text-white">
                  Courses
                </a>
              </li>
              <li>
                <a href="#" className="transition-colors hover:text-white">
                  Instructors
                </a>
              </li>
              <li>
                <a href="#" className="transition-colors hover:text-white">
                  Pricing
                </a>
              </li>
              <li>
                <a href="#" className="transition-colors hover:text-white">
                  About
                </a>
              </li>
            </ul>
          </div>

          <div>
            <h3 className="mb-4 font-semibold">Support</h3>
            <ul className="space-y-2 text-gray-400">
              <li>
                <a href="#" className="transition-colors hover:text-white">
                  Help Center
                </a>
              </li>
              <li>
                <a href="#" className="transition-colors hover:text-white">
                  Contact
                </a>
              </li>
              <li>
                <a href="#" className="transition-colors hover:text-white">
                  Privacy
                </a>
              </li>
              <li>
                <a href="#" className="transition-colors hover:text-white">
                  Terms
                </a>
              </li>
            </ul>
          </div>
        </div>

        <div className="mt-8 border-t border-gray-800 pt-8 text-center text-gray-400">
          <p>&copy; 2025 {APP_NAME}. All rights reserved.</p>
        </div>
      </div>
    </footer>
  );
};

export default Footer;
