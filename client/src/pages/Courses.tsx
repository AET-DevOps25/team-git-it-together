import React, { useState } from 'react';
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from '@/components/ui/card';
import { Button } from '@/components/ui/button';
import { Badge } from '@/components/ui/badge';
import { BookOpen, Clock, Users, Star } from 'lucide-react';
import Navbar from '@/components/Navbar';

const Courses = () => {
  const [activeCategory, setActiveCategory] = useState("All");

  const courses = [
    {
      id: 1,
      title: "React Development Fundamentals",
      description: "Learn modern React development with hooks, context, and best practices for building dynamic web applications.",
      instructor: "Sarah Johnson",
      duration: "8 hours",
      students: 1250,
      rating: 4.8,
      price: "Free",
      category: "Frontend",
      level: "Beginner",
      image: "photo-1649972904349-6e44c42644a7"
    },
    {
      id: 2,
      title: "JavaScript ES6+ Masterclass",
      description: "Master modern JavaScript features including async/await, destructuring, modules, and advanced concepts.",
      instructor: "Mike Chen",
      duration: "12 hours",
      students: 2100,
      rating: 4.9,
      price: "Free",
      category: "Programming",
      level: "Intermediate",
      image: "photo-1488590528505-98d2b5aba04b"
    },
    {
      id: 3,
      title: "Full Stack Web Development",
      description: "Complete guide to building full-stack applications with Node.js, Express, MongoDB, and React.",
      instructor: "Alex Rodriguez",
      duration: "20 hours",
      students: 850,
      rating: 4.7,
      price: "Free",
      category: "Full Stack",
      level: "Advanced",
      image: "photo-1461749280684-dccba630e2f6"
    },
    {
      id: 4,
      title: "UI/UX Design Principles",
      description: "Learn design thinking, user research, prototyping, and creating beautiful user interfaces.",
      instructor: "Emma Davis",
      duration: "10 hours",
      students: 980,
      rating: 4.6,
      price: "Free",
      category: "Design",
      level: "Beginner",
      image: "photo-1486312338219-ce68d2c6f44d"
    },
    {
      id: 5,
      title: "Python for Data Science",
      description: "Master Python programming for data analysis, visualization, and machine learning applications.",
      instructor: "Dr. James Wilson",
      duration: "15 hours",
      students: 1650,
      rating: 4.8,
      price: "Free",
      category: "Data Science",
      level: "Intermediate",
      image: "photo-1581091226825-a6a2a5aee158"
    },
    {
      id: 6,
      title: "Mobile App Development",
      description: "Build cross-platform mobile applications using React Native and modern development tools.",
      instructor: "Lisa Park",
      duration: "18 hours",
      students: 720,
      rating: 4.5,
      price: "Free",
      category: "Mobile",
      level: "Advanced",
      image: "photo-1649972904349-6e44c42644a7"
    }
  ];

  const categories = ["All", "Frontend", "Programming", "Full Stack", "Design", "Data Science", "Mobile"];

  const filteredCourses = activeCategory === "All"
    ? courses
    : courses.filter(course => course.category === activeCategory);

  const handleCategoryClick = (category: string) => {
    setActiveCategory(category);
  };

  return (
    <div className="min-h-screen bg-gray-50">
      <Navbar />

      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
        {/* Header Section */}
        <div className="text-center mb-12">
          <h1 className="text-4xl font-bold text-gray-900 mb-4">
            Explore Our <span className="text-blue-600">Courses</span>
          </h1>
          <p className="text-xl text-gray-600 max-w-3xl mx-auto">
            Discover Ai-led courses designed to help you master new skills and advance your career.
          </p>
        </div>

        {/* Filter Categories */}
        <div className="flex flex-wrap justify-center gap-4 mb-8">
          {categories.map((category) => (
            <Button
              key={category}
              variant={category === activeCategory ? "default" : "outline"}
              size="sm"
              className="rounded-full"
              onClick={() => handleCategoryClick(category)}
            >
              {category}
            </Button>
          ))}
        </div>

        {/* Courses Grid */}
        <div className="grid md:grid-cols-2 lg:grid-cols-3 gap-8">
          {filteredCourses.map((course) => (
            <Card key={course.id} className="group hover:shadow-lg transition-all duration-300 hover:-translate-y-1">
              <div className="aspect-video bg-gradient-to-br from-blue-100 to-purple-100 rounded-t-lg relative overflow-hidden">
                <img
                  src={`https://images.unsplash.com/${course.image}?w=400&h=225&fit=crop`}
                  alt={course.title}
                  className="w-full h-full object-cover group-hover:scale-105 transition-transform duration-300"
                />
                <div className="absolute top-4 left-4">
                  <Badge className="bg-white/90 text-gray-800 hover:bg-white">
                    {course.category}
                  </Badge>
                </div>
                <div className="absolute top-4 right-4">
                  <Badge variant="secondary" className="bg-white/90 text-gray-800">
                    {course.level}
                  </Badge>
                </div>
              </div>

              <CardHeader>
                <CardTitle className="text-lg group-hover:text-blue-600 transition-colors">
                  {course.title}
                </CardTitle>
                <CardDescription className="text-sm">
                  {course.description}
                </CardDescription>
              </CardHeader>

              <CardContent className="space-y-4">
                <div className="text-sm text-gray-600">
                  by {course.instructor}
                </div>

                <div className="flex items-center justify-between text-sm text-gray-600">
                  <div className="flex items-center space-x-1">
                    <Clock className="h-4 w-4" />
                    <span>{course.duration}</span>
                  </div>
                  <div className="flex items-center space-x-1">
                    <Users className="h-4 w-4" />
                    <span>{course.students.toLocaleString()}</span>
                  </div>
                  <div className="flex items-center space-x-1">
                    <Star className="h-4 w-4 fill-yellow-400 text-yellow-400" />
                    <span>{course.rating}</span>
                  </div>
                </div>

                <div className="flex items-center justify-between pt-4">
                  <span className="text-2xl font-bold text-green-600">{course.price}</span>
                  <Button size="sm" className="bg-blue-600 hover:bg-blue-700">
                    <BookOpen className="h-4 w-4 mr-2" />
                    Enroll Now
                  </Button>
                </div>
              </CardContent>
            </Card>
          ))}
        </div>

        {/* Load More Section */}
        <div className="text-center mt-12">
          <Button variant="outline" size="lg">
            Load More Courses
          </Button>
        </div>
      </div>
    </div>
  );
};

export default Courses;