import React from 'react';
import { LayoutDashboard, Sparkles, Globe, Target } from 'lucide-react';

const Features = () => {
  const features = [
    {
      icon: Sparkles,
      title: 'AI-Curated Courses',
      description:
        'Discover expertly crafted courses powered by AI that adapt to your learning style and pace.',
      available: true,
    },
    {
      icon: Target,
      title: 'Personalized Learning',
      description:
        'Experience learning tailored to your goals, skills, and preferences with intelligent recommendations.',
      available: true,
    },
    {
      icon: LayoutDashboard,
      title: 'Smart Dashboard',
      description:
        'Track your learning journey with AI-powered insights, progress analytics, and achievement tracking.',
      available: false,
      comingSoon: true,
    },
    {
      icon: Globe,
      title: 'Global Community',
      description: 'Connect with learners worldwide, share knowledge, and collaborate in a diverse learning environment.',
      available: false,
      comingSoon: true,
    }
  ];

  return (
    <div className="bg-white py-24">
      <div className="mx-auto max-w-7xl px-4 sm:px-6 lg:px-8">
        <div className="mb-16 space-y-4 text-center">
          <h2 className="text-4xl font-bold text-gray-900">
            The Future of <span className="text-blue-600">Learning</span> is Here
          </h2>
          <p className="mx-auto max-w-3xl text-xl text-gray-600">
            Experience the power of AI-driven education with personalized learning paths, 
            intelligent course curation, and a global community of learners.
          </p>
        </div>

        <div className="grid gap-8 md:grid-cols-2 lg:grid-cols-4">
          {features.map((feature, index) => {
            const Icon = feature.icon;
            return (
              <div
                key={index}
                className={`group rounded-xl border border-gray-100 p-6 transition-all duration-300 ${
                  feature.available 
                    ? 'hover:-translate-y-1 hover:border-blue-200 hover:shadow-lg' 
                    : 'opacity-60 cursor-not-allowed'
                }`}
              >
                <div className={`mb-4 flex h-12 w-12 items-center justify-center rounded-lg transition-colors duration-300 ${
                  feature.available 
                    ? 'bg-gradient-to-r from-blue-100 to-purple-100 group-hover:from-blue-600 group-hover:to-purple-600' 
                    : 'bg-gray-100'
                }`}>
                  <Icon className={`h-6 w-6 transition-colors duration-300 ${
                    feature.available 
                      ? 'text-blue-600 group-hover:text-white' 
                      : 'text-gray-400'
                  }`} />
                </div>
                <h3 className={`mb-2 text-xl font-semibold ${
                  feature.available ? 'text-gray-900' : 'text-gray-500'
                }`}>
                  {feature.title}
                  {feature.comingSoon && (
                    <span className="ml-2 inline-flex items-center rounded-full bg-gradient-to-r from-purple-100 to-indigo-100 px-2 py-1 text-xs font-medium text-purple-700 border border-purple-200">
                      Coming Soon
                    </span>
                  )}
                </h3>
                <p className={`leading-relaxed ${
                  feature.available ? 'text-gray-600' : 'text-gray-400'
                }`}>
                  {feature.description}
                </p>
              </div>
            );
          })}
        </div>
      </div>
    </div>
  );
};

export default Features;
