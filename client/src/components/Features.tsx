
import React from 'react';
import { BookOpen, LayoutDashboard, TrendingUp, Users } from 'lucide-react';

const Features = () => {
  const features = [
    {
      icon: BookOpen,
      title: "Ai-Curated Courses",
      description: "Access hundreds of courses designed by industry experts and experienced educators."
    },
    {
      icon: LayoutDashboard,
      title: "Personal Dashboard",
      description: "Track your learning journey with a comprehensive dashboard showing progress and achievements."
    },
    {
      icon: TrendingUp,
      title: "Progress Tracking",
      description: "Monitor your progress with detailed analytics and personalized learning recommendations."
    },
    {
      icon: Users,
      title: "Community Learning",
      description: "Connect with fellow learners, share knowledge, and collaborate on projects."
    }
  ];

  return (
    <div className="py-24 bg-white">
      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
        <div className="text-center space-y-4 mb-16">
          <h2 className="text-4xl font-bold text-gray-900">
            Everything You Need to <span className="text-blue-600">Succeed</span>
          </h2>
          <p className="text-xl text-gray-600 max-w-3xl mx-auto">
            Our platform provides all the tools and resources you need to learn effectively and achieve your goals.
          </p>
        </div>

        <div className="grid md:grid-cols-2 lg:grid-cols-4 gap-8">
          {features.map((feature, index) => {
            const Icon = feature.icon;
            return (
              <div 
                key={index} 
                className="group p-6 rounded-xl border border-gray-100 hover:border-blue-200 hover:shadow-lg transition-all duration-300 hover:-translate-y-1"
              >
                <div className="w-12 h-12 bg-blue-100 rounded-lg flex items-center justify-center mb-4 group-hover:bg-blue-600 transition-colors duration-300">
                  <Icon className="h-6 w-6 text-blue-600 group-hover:text-white transition-colors duration-300" />
                </div>
                <h3 className="text-xl font-semibold text-gray-900 mb-2">{feature.title}</h3>
                <p className="text-gray-600 leading-relaxed">{feature.description}</p>
              </div>
            );
          })}
        </div>
      </div>
    </div>
  );
};

export default Features;
