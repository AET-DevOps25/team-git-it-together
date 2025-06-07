import React from 'react';
import { BookOpen, LayoutDashboard, TrendingUp, Users } from 'lucide-react';

const Features = () => {
  const features = [
    {
      icon: BookOpen,
      title: 'Ai-Curated Courses',
      description:
        'Access hundreds of courses designed by industry experts and experienced educators.',
    },
    {
      icon: LayoutDashboard,
      title: 'Personal Dashboard',
      description:
        'Track your learning journey with a comprehensive dashboard showing progress and achievements.',
    },
    {
      icon: TrendingUp,
      title: 'Progress Tracking',
      description:
        'Monitor your progress with detailed analytics and personalized learning recommendations.',
    },
    {
      icon: Users,
      title: 'Community Learning',
      description: 'Connect with fellow learners, share knowledge, and collaborate on projects.',
    },
  ];

  return (
    <div className="bg-white py-24">
      <div className="mx-auto max-w-7xl px-4 sm:px-6 lg:px-8">
        <div className="mb-16 space-y-4 text-center">
          <h2 className="text-4xl font-bold text-gray-900">
            Everything You Need to <span className="text-blue-600">Succeed</span>
          </h2>
          <p className="mx-auto max-w-3xl text-xl text-gray-600">
            Our platform provides all the tools and resources you need to learn effectively and
            achieve your goals.
          </p>
        </div>

        <div className="grid gap-8 md:grid-cols-2 lg:grid-cols-4">
          {features.map((feature, index) => {
            const Icon = feature.icon;
            return (
              <div
                key={index}
                className="group rounded-xl border border-gray-100 p-6 transition-all duration-300 hover:-translate-y-1 hover:border-blue-200 hover:shadow-lg"
              >
                <div className="mb-4 flex h-12 w-12 items-center justify-center rounded-lg bg-blue-100 transition-colors duration-300 group-hover:bg-blue-600">
                  <Icon className="h-6 w-6 text-blue-600 transition-colors duration-300 group-hover:text-white" />
                </div>
                <h3 className="mb-2 text-xl font-semibold text-gray-900">{feature.title}</h3>
                <p className="leading-relaxed text-gray-600">{feature.description}</p>
              </div>
            );
          })}
        </div>
      </div>
    </div>
  );
};

export default Features;
