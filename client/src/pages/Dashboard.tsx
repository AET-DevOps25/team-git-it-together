import React, { useEffect, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from '@/components/ui/card';
import { Button } from '@/components/ui/button';
import { Progress } from '@/components/ui/progress';
import { Badge } from '@/components/ui/badge';
import { Tabs, TabsContent, TabsList, TabsTrigger } from '@/components/ui/tabs';
import { BookOpen, LayoutDashboard, User, Users, Trophy, Calendar, Clock, Star, TrendingUp, Award, Target } from 'lucide-react';
import Navbar from '@/components/Navbar';

const Dashboard = () => {
  const [user, setUser] = useState<any>(null);
  const navigate = useNavigate();

  useEffect(() => {
    const isLoggedIn = localStorage.getItem('isLoggedIn');
    const userData = localStorage.getItem('user');

    if (!isLoggedIn || !userData) {
      navigate('/login');
      return;
    }

    setUser(JSON.parse(userData));
  }, [navigate]);

  if (!user) {
    return <div>Loading...</div>;
  }

  const courses = [
    {
      id: 1,
      title: "React Development",
      description: "Learn modern React development with hooks and context",
      progress: 75,
      totalLessons: 16,
      completedLessons: 12,
      category: "Frontend",
      difficulty: "Intermediate",
      nextLesson: "State Management with Context",
      timeSpent: "24 hours"
    },
    {
      id: 2,
      title: "JavaScript Fundamentals",
      description: "Master the basics of JavaScript programming",
      progress: 100,
      totalLessons: 20,
      completedLessons: 20,
      category: "Programming",
      difficulty: "Beginner",
      nextLesson: "Course Completed!",
      timeSpent: "30 hours"
    },
    {
      id: 3,
      title: "UI/UX Design Principles",
      description: "Learn design thinking and user experience principles",
      progress: 30,
      totalLessons: 12,
      completedLessons: 4,
      category: "Design",
      difficulty: "Beginner",
      nextLesson: "User Research Methods",
      timeSpent: "8 hours"
    }
  ];

  const skills = [
    { name: "React", level: 75, color: "bg-blue-500", trend: "+5%" },
    { name: "JavaScript", level: 90, color: "bg-yellow-500", trend: "+2%" },
    { name: "CSS", level: 65, color: "bg-purple-500", trend: "+8%" },
    { name: "Node.js", level: 45, color: "bg-green-500", trend: "+12%" }
  ];

  const achievements = [
    { title: "First Course Completed", description: "Completed JavaScript Fundamentals", date: "2024-01-20", icon: Trophy },
    { title: "Week Streak", description: "7 days learning streak", date: "2024-01-18", icon: Target },
    { title: "React Expert", description: "Mastered React Hooks", date: "2024-01-15", icon: Award },
  ];

  const upcomingDeadlines = [
    { course: "React Development", task: "Project Submission", date: "2024-02-05", daysLeft: 3 },
    { course: "UI/UX Design", task: "Design Challenge", date: "2024-02-10", daysLeft: 8 },
  ];

  return (
    <div className="min-h-screen bg-gray-50">
      <Navbar />

      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
        {/* Welcome Section */}
        <div className="mb-8">
          <h1 className="text-3xl font-bold text-gray-900 mb-2">Welcome back, {user.name}!</h1>
          <p className="text-gray-600">Continue your learning journey and track your progress.</p>
        </div>

        {/* Stats Cards */}
        <div className="grid grid-cols-1 md:grid-cols-4 gap-6 mb-8">
          <Card className="bg-gradient-to-r from-blue-500 to-blue-600 text-white">
            <CardContent className="p-6">
              <div className="flex items-center justify-between">
                <div>
                  <p className="text-blue-100">Total Courses</p>
                  <p className="text-3xl font-bold">3</p>
                  <p className="text-sm text-blue-100">+1 this month</p>
                </div>
                <BookOpen className="h-12 w-12 text-blue-200" />
              </div>
            </CardContent>
          </Card>

          <Card className="bg-gradient-to-r from-green-500 to-green-600 text-white">
            <CardContent className="p-6">
              <div className="flex items-center justify-between">
                <div>
                  <p className="text-green-100">Completed</p>
                  <p className="text-3xl font-bold">1</p>
                  <p className="text-sm text-green-100">33% completion rate</p>
                </div>
                <Trophy className="h-12 w-12 text-green-200" />
              </div>
            </CardContent>
          </Card>

          <Card className="bg-gradient-to-r from-purple-500 to-purple-600 text-white">
            <CardContent className="p-6">
              <div className="flex items-center justify-between">
                <div>
                  <p className="text-purple-100">Hours Learned</p>
                  <p className="text-3xl font-bold">62</p>
                  <p className="text-sm text-purple-100">+8 this week</p>
                </div>
                <Clock className="h-12 w-12 text-purple-200" />
              </div>
            </CardContent>
          </Card>

          <Card className="bg-gradient-to-r from-yellow-500 to-yellow-600 text-white">
            <CardContent className="p-6">
              <div className="flex items-center justify-between">
                <div>
                  <p className="text-yellow-100">Certificates</p>
                  <p className="text-3xl font-bold">1</p>
                  <p className="text-sm text-yellow-100">JavaScript Expert</p>
                </div>
                <Award className="h-12 w-12 text-yellow-200" />
              </div>
            </CardContent>
          </Card>
        </div>

        <Tabs defaultValue="overview" className="space-y-6">
          <TabsList className="grid w-full grid-cols-4">
            <TabsTrigger value="overview">Overview</TabsTrigger>
            <TabsTrigger value="courses">Courses</TabsTrigger>
            <TabsTrigger value="progress">Progress</TabsTrigger>
            <TabsTrigger value="achievements">Achievements</TabsTrigger>
          </TabsList>

          <TabsContent value="overview" className="space-y-6">
            <div className="grid lg:grid-cols-3 gap-6">
              {/* Current Courses */}
              <div className="lg:col-span-2">
                <Card>
                  <CardHeader>
                    <CardTitle>Continue Learning</CardTitle>
                    <CardDescription>Pick up where you left off</CardDescription>
                  </CardHeader>
                  <CardContent className="space-y-4">
                    {courses.slice(0, 2).map((course) => (
                      <div key={course.id} className="border rounded-lg p-4 hover:shadow-md transition-shadow">
                        <div className="flex justify-between items-start mb-3">
                          <div>
                            <h3 className="font-semibold text-gray-900">{course.title}</h3>
                            <p className="text-sm text-gray-600 mb-2">Next: {course.nextLesson}</p>
                            <div className="flex items-center space-x-2">
                              <Badge variant="secondary">{course.category}</Badge>
                              <Badge variant="outline">{course.difficulty}</Badge>
                            </div>
                          </div>
                          <Button size="sm">Continue</Button>
                        </div>
                        <Progress value={course.progress} className="h-2" />
                        <div className="flex justify-between text-sm text-gray-600 mt-2">
                          <span>{course.completedLessons}/{course.totalLessons} lessons</span>
                          <span>{course.progress}% complete</span>
                        </div>
                      </div>
                    ))}
                  </CardContent>
                </Card>
              </div>

              {/* Quick Stats */}
              <div className="space-y-6">
                <Card>
                  <CardHeader>
                    <CardTitle>Learning Streak</CardTitle>
                  </CardHeader>
                  <CardContent>
                    <div className="text-center">
                      <div className="text-4xl font-bold text-blue-600 mb-2">7</div>
                      <p className="text-gray-600">Days in a row</p>
                      <div className="mt-4 flex justify-center space-x-1">
                        {[1,2,3,4,5,6,7].map((day) => (
                          <div key={day} className="w-6 h-6 bg-blue-500 rounded-full"></div>
                        ))}
                      </div>
                    </div>
                  </CardContent>
                </Card>

                <Card>
                  <CardHeader>
                    <CardTitle>Upcoming Deadlines</CardTitle>
                  </CardHeader>
                  <CardContent className="space-y-3">
                    {upcomingDeadlines.map((deadline, index) => (
                      <div key={index} className="flex items-center justify-between p-3 bg-orange-50 rounded-lg">
                        <div>
                          <p className="font-medium text-sm">{deadline.task}</p>
                          <p className="text-xs text-gray-600">{deadline.course}</p>
                        </div>
                        <Badge variant={deadline.daysLeft <= 3 ? "destructive" : "secondary"}>
                          {deadline.daysLeft}d left
                        </Badge>
                      </div>
                    ))}
                  </CardContent>
                </Card>
              </div>
            </div>
          </TabsContent>

          <TabsContent value="courses" className="space-y-6">
            <div className="grid gap-6">
              {courses.map((course) => (
                <Card key={course.id}>
                  <CardContent className="p-6">
                    <div className="flex justify-between items-start mb-4">
                      <div>
                        <h3 className="text-xl font-semibold text-gray-900">{course.title}</h3>
                        <p className="text-gray-600 mb-2">{course.description}</p>
                        <div className="flex items-center space-x-4 text-sm text-gray-500">
                          <span>{course.timeSpent}</span>
                          <span>{course.completedLessons}/{course.totalLessons} lessons</span>
                          <Badge variant="secondary">{course.category}</Badge>
                        </div>
                      </div>
                      <Button>{course.progress === 100 ? 'Review' : 'Continue'}</Button>
                    </div>
                    <div className="space-y-2">
                      <div className="flex justify-between text-sm">
                        <span>Progress</span>
                        <span className="font-medium">{course.progress}%</span>
                      </div>
                      <Progress value={course.progress} className="h-3" />
                    </div>
                  </CardContent>
                </Card>
              ))}
            </div>
          </TabsContent>

          <TabsContent value="progress" className="space-y-6">
            <Card>
              <CardHeader>
                <CardTitle>Skills Progress</CardTitle>
                <CardDescription>Your current skill levels and recent improvements</CardDescription>
              </CardHeader>
              <CardContent className="space-y-6">
                {skills.map((skill) => (
                  <div key={skill.name}>
                    <div className="flex justify-between items-center mb-2">
                      <span className="text-sm font-medium text-gray-900">{skill.name}</span>
                      <div className="flex items-center space-x-2">
                        <span className="text-sm text-green-600 font-medium">{skill.trend}</span>
                        <span className="text-sm text-gray-600">{skill.level}%</span>
                      </div>
                    </div>
                    <div className="w-full bg-gray-200 rounded-full h-3">
                      <div
                        className={`h-3 rounded-full ${skill.color} transition-all duration-500`}
                        style={{ width: `${skill.level}%` }}
                      ></div>
                    </div>
                  </div>
                ))}
              </CardContent>
            </Card>
          </TabsContent>

          <TabsContent value="achievements" className="space-y-6">
            <div className="grid md:grid-cols-2 lg:grid-cols-3 gap-6">
              {achievements.map((achievement, index) => {
                const Icon = achievement.icon;
                return (
                  <Card key={index} className="text-center">
                    <CardContent className="p-6">
                      <div className="w-16 h-16 bg-yellow-100 rounded-full flex items-center justify-center mx-auto mb-4">
                        <Icon className="h-8 w-8 text-yellow-600" />
                      </div>
                      <h3 className="font-semibold text-gray-900 mb-2">{achievement.title}</h3>
                      <p className="text-sm text-gray-600 mb-2">{achievement.description}</p>
                      <p className="text-xs text-gray-500">{achievement.date}</p>
                    </CardContent>
                  </Card>
                );
              })}
            </div>
          </TabsContent>
        </Tabs>
      </div>
    </div>
  );
};

export default Dashboard;