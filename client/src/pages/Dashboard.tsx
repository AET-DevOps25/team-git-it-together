import React, { useState, useEffect } from 'react';
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from '@/components/ui/card';
import { Button } from '@/components/ui/button';
import { Progress } from '@/components/ui/progress';
import { Badge } from '@/components/ui/badge';
import { Tabs, TabsContent, TabsList, TabsTrigger } from '@/components/ui/tabs';
import { Award, BookOpen, Target, Trophy, Star, Bookmark, BookmarkCheck, Clock, Users } from 'lucide-react';
import Navbar from '@/components/Navbar';
import AIChatAssistant from '@/components/AIChatAssistant';
import CertificateGenerator from '@/components/CertificateGenerator';
import { useAuth } from '@/hooks/useAuth';
import { getDashboardData, type DashboardData, type DashboardCourse } from '@/services/dashboard.service';
import { useToast } from '@/hooks/use-toast';
import { useNavigate } from 'react-router-dom';
import * as courseService from '@/services/course.service';

const Dashboard = () => {
  const { user } = useAuth();
  const { toast } = useToast();
  const navigate = useNavigate();
  
  const [dashboardData, setDashboardData] = useState<DashboardData | null>(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [localBookmarkedCourses, setLocalBookmarkedCourses] = useState<DashboardCourse[]>([]);
  


  useEffect(() => {
    const fetchDashboardData = async () => {
      if (!user?.id) return;
      try {
        setLoading(true);
        setError(null);
        const data = await getDashboardData(user.id);
        setDashboardData(data);
        setLocalBookmarkedCourses(data.bookmarkedCourses);
      } catch (err: any) {
        console.error('Error fetching dashboard data:', err);
        setError(err.message || 'Failed to load dashboard data');
        toast({
          title: 'Error',
          description: 'Failed to load dashboard data',
          variant: 'destructive',
        });
      } finally {
        setLoading(false);
      }
    };
    fetchDashboardData();
  }, [user?.id, toast]);



  if (!user) {
    return <div>Loading...</div>;
  }

  if (loading) {
    return (
      <div className="min-h-screen bg-gray-50">
        <Navbar />
        <div className="flex h-96 items-center justify-center">
          <div className="flex items-center space-x-2">
            <div className="h-6 w-6 animate-spin rounded-full border-2 border-blue-600 border-t-transparent"></div>
            <span>Loading dashboard...</span>
          </div>
        </div>
      </div>
    );
  }

  if (error) {
    return (
      <div className="min-h-screen bg-gray-50">
        <Navbar />
        <div className="flex h-96 items-center justify-center">
          <div className="text-center">
            <p className="text-lg text-red-600 mb-4">{error}</p>
            <Button onClick={() => window.location.reload()}>Retry</Button>
          </div>
        </div>
      </div>
    );
  }

  if (!dashboardData) {
    return (
      <div className="min-h-screen bg-gray-50">
        <Navbar />
        <div className="flex h-96 items-center justify-center">
          <div className="text-center">
            <p className="text-lg text-gray-600 mb-4">No dashboard data available</p>
          </div>
        </div>
      </div>
    );
  }

  const { stats, achievements, enrolledCourses, completedCourses } = dashboardData;

  const handleContinueCourse = (course: DashboardCourse) => {
    navigate(`/courses/${course.id}`);
  };

  const handleReviewCourse = (course: DashboardCourse) => {
    navigate(`/courses/${course.id}`);
  };





  return (
    <div className="min-h-screen bg-gray-50">
      <Navbar />

      <div className="mx-auto max-w-7xl px-4 py-8 sm:px-6 lg:px-8">
        {/* Welcome Section */}
        <div className="mb-8">
          <h1 className="mb-2 text-3xl font-bold text-gray-900">
            Welcome back, {user.firstName || user.username || user.email}!
          </h1>
          <p className="text-gray-600">Continue your learning journey and track your progress.</p>
        </div>

        {/* Stats Cards */}
        <div className="mb-8 grid grid-cols-1 gap-6 md:grid-cols-4">
          <Card className="bg-gradient-to-r from-blue-500 to-blue-600 text-white">
            <CardContent className="p-6">
              <div className="flex items-center justify-between">
                <div>
                  <p className="text-blue-100">Total Courses</p>
                  <p className="text-3xl font-bold">{stats.totalCourses}</p>
                  <p className="text-sm text-blue-100">+{enrolledCourses.length} this month</p>
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
                  <p className="text-3xl font-bold">{stats.completedCourses}</p>
                  <p className="text-sm text-green-100">
                    {stats.totalCourses > 0 ? Math.round((stats.completedCourses / stats.totalCourses) * 100) : 0}% completion rate
                  </p>
                </div>
                <Trophy className="h-12 w-12 text-green-200" />
              </div>
            </CardContent>
          </Card>

          {/* Skills Mastered Card */}
          <Card className="bg-gradient-to-r from-purple-500 to-purple-600 text-white">
            <CardContent className="p-6">
              <div className="flex items-center justify-between">
                <div>
                  <p className="text-purple-100">Skills Mastered</p>
                  <p className="text-3xl font-bold">{dashboardData.currentSkills ? dashboardData.currentSkills.length : 0}</p>
                  <p className="text-sm text-purple-100">Total mastered skills</p>
                </div>
                <Target className="h-12 w-12 text-purple-200" />
              </div>
            </CardContent>
          </Card>

          <Card className="bg-gradient-to-r from-yellow-500 to-yellow-600 text-white">
            <CardContent className="p-6">
              <div className="flex items-center justify-between">
                <div>
                  <p className="text-yellow-100">Certificates</p>
                  <p className="text-3xl font-bold">{stats.certificates}</p>
                  <p className="text-sm text-yellow-100">
                    {stats.certificates > 0 ? 'Course Certificates' : 'No certificates yet'}
                  </p>
                </div>
                <Award className="h-12 w-12 text-yellow-200" />
              </div>
            </CardContent>
          </Card>
        </div>

        <Tabs defaultValue="overview" className="space-y-6">
          <TabsList className="grid w-full grid-cols-5">
            <TabsTrigger value="overview">Overview</TabsTrigger>
            <TabsTrigger value="courses">Courses</TabsTrigger>
            <TabsTrigger value="bookmarks">Bookmarks</TabsTrigger>
            <TabsTrigger value="skills">Skills</TabsTrigger>
            <TabsTrigger value="achievements">Achievements</TabsTrigger>
          </TabsList>

          <TabsContent value="overview" className="space-y-6">
            <div className="grid gap-6 lg:grid-cols-3">
              {/* Current Courses */}
              <div className="lg:col-span-2">
                <Card>
                  <CardHeader>
                    <CardTitle>Continue Learning</CardTitle>
                    <CardDescription>Pick up where you left off</CardDescription>
                  </CardHeader>
                  <CardContent className="space-y-4">
                    {(() => {
                      const coursesWithProgress = enrolledCourses.filter(course => course.progress > 0);
                      const coursesToShow = coursesWithProgress.length > 0 
                        ? coursesWithProgress 
                        : enrolledCourses; // Show all enrolled courses if none have progress
                      
                      if (coursesToShow.length === 0) {
                        return (
                          <div className="text-center py-8">
                            <p className="text-gray-500 mb-4">No courses in progress</p>
                            <Button className="mt-2" onClick={() => navigate('/courses')}>
                              Start a New Course
                            </Button>
                          </div>
                        );
                      }
                      
                      return coursesToShow.map((course) => (
                        <div
                          key={course.id}
                          className="rounded-lg border p-4 transition-shadow hover:shadow-md"
                        >
                          <div className="mb-3 flex items-start justify-between">
                            <div>
                              <h3 className="font-semibold text-gray-900">{course.title}</h3>
                              <p className="mb-2 text-sm text-gray-600">
                                {course.progress > 0 ? `Next: ${course.nextLesson}` : 'Ready to start'}
                              </p>
                              <div className="flex items-center space-x-2">
                                <Badge variant="secondary">{course.category}</Badge>
                                <Badge variant="outline">{course.difficulty}</Badge>
                              </div>
                            </div>
                            <Button size="sm" onClick={() => handleContinueCourse(course)}>
                              {course.progress > 0 ? 'Continue' : 'Start'}
                            </Button>
                          </div>
                          <Progress value={course.progress} className="h-2" />
                          <div className="mt-2 flex justify-between text-sm text-gray-600">
                            <span>
                              {course.completedLessons}/{course.totalLessons} lessons
                            </span>
                            <span>{course.progress.toFixed(2)}% complete</span>
                          </div>
                        </div>
                      ));
                    })()}
                  </CardContent>
                </Card>
              </div>

              {/* AI Chat Assistant */}
              <AIChatAssistant 
                userSkills={[
                  ...(dashboardData.skillsInProgress || []),
                  ...(dashboardData.currentSkills || [])
                ]} 
                disableCourseGeneration={true}
              />
            </div>
          </TabsContent>

          <TabsContent value="courses" className="space-y-6">
            <div className="grid gap-6">
              {/* Enrolled Courses */}
              {enrolledCourses.length > 0 && (
                <div>
                  <h3 className="mb-4 text-lg font-semibold">Courses in Progress</h3>
                  {enrolledCourses.sort((a, b) => b.progress - a.progress).map((course) => (
                    <Card key={course.id} className="mb-4">
                      <CardContent className="p-6">
                        <div className="mb-4 flex items-start justify-between">
                          <div>
                            <h3 className="text-xl font-semibold text-gray-900">{course.title}</h3>
                            <p className="mb-2 text-gray-600">{course.description}</p>
                            <div className="flex items-center space-x-4 text-sm text-gray-500">
                              <span>{course.timeSpent}</span>
                              <span>
                                {course.completedLessons}/{course.totalLessons} lessons
                              </span>
                              <Badge variant="secondary">{course.category}</Badge>
                            </div>
                          </div>
                          <Button onClick={() => handleContinueCourse(course)}>
                            {course.progress > 0 ? 'Continue' : 'Start'}
                          </Button>
                        </div>
                        <div className="space-y-2">
                          <div className="flex justify-between text-sm">
                            <span>Progress</span>
                            <span className="font-medium">{course.progress.toFixed(2)}%</span>
                          </div>
                          <Progress value={course.progress} className="h-3" />
                        </div>
                      </CardContent>
                    </Card>
                  ))}
                </div>
              )}

              {/* Completed Courses */}
              {completedCourses.length > 0 && (
                <div>
                  <h3 className="mb-4 text-lg font-semibold">Completed Courses</h3>
                  {completedCourses.map((course) => (
                    <Card key={course.id} className="mb-4">
                      <CardContent className="p-6">
                        <div className="mb-4 flex items-start justify-between">
                          <div>
                            <h3 className="text-xl font-semibold text-gray-900">{course.title}</h3>
                            <p className="mb-2 text-gray-600">{course.description}</p>
                            <div className="flex items-center space-x-4 text-sm text-gray-500">
                              <span>{course.timeSpent}</span>
                              <span>
                                {course.completedLessons}/{course.totalLessons} lessons
                              </span>
                              <Badge variant="secondary">{course.category}</Badge>
                            </div>
                          </div>
                          <div className="flex flex-col space-y-2">
                            <Button onClick={() => handleReviewCourse(course)}>
                              Review
                            </Button>
                            <CertificateGenerator
                              courseTitle={course.title}
                              userFirstName={user?.firstName || 'Student'}
                              userLastName={user?.lastName || ''}
                              completionDate={new Date().toLocaleDateString()}
                              instructor={course.instructor}
                            />
                          </div>
                        </div>
                        <div className="space-y-2">
                          <div className="flex justify-between text-sm">
                            <span>Progress</span>
                            <span className="font-medium">{course.progress.toFixed(2)}%</span>
                          </div>
                          <Progress value={course.progress} className="h-3" />
                        </div>
                      </CardContent>
                    </Card>
                  ))}
                </div>
              )}              

              {enrolledCourses.length === 0 && completedCourses.length === 0 && localBookmarkedCourses.length === 0 && (
                <div className="text-center py-8">
                  <p className="text-gray-500 mb-4">No courses found</p>
                  <Button onClick={() => navigate('/courses')}>
                    Browse Courses
                  </Button>
                </div>
              )}
            </div>
          </TabsContent>

          <TabsContent value="bookmarks" className="space-y-6">
            <div className="grid gap-6">
              {localBookmarkedCourses.length > 0 ? (
                <div>
                  <h3 className="mb-4 text-lg font-semibold">Bookmarked Courses</h3>
                  <div className="grid gap-8 md:grid-cols-2 lg:grid-cols-3">
                    {localBookmarkedCourses.map((course) => (
                      <Card
                        key={course.id}
                        className="group cursor-pointer transition-all duration-300 hover:-translate-y-1 hover:shadow-lg"
                        onClick={() => navigate(`/courses/${course.id}`)}
                      >
                        <div className="relative aspect-video overflow-hidden rounded-t-lg bg-gradient-to-br from-blue-100 to-purple-100">
                          {course.thumbnailUrl ? (
                            <img
                              src={course.thumbnailUrl}
                              alt={course.title}
                              className="h-full w-full object-cover transition-transform duration-300 group-hover:scale-105"
                            />
                          ) : (
                            <div className="flex h-full items-center justify-center bg-gradient-to-br from-blue-200 to-purple-200">
                              <BookOpen className="h-12 w-12 text-gray-400" />
                            </div>
                          )}
                          
                          {/* Bookmark Icon Overlay - Only for unbookmarking in bookmarks tab */}
                          {user && (
                            <button
                              onClick={(event) => {
                                event.stopPropagation();
                                // In bookmarks tab, we only unbookmark courses
                                courseService.unbookmarkCourse(course.id, user.id).then(() => {
                                  // Update local state only - no need to call updateUserBookmarks since we're already updating local state
                                  setLocalBookmarkedCourses(prev => prev.filter(c => c.id !== course.id));
                                  toast({
                                    title: 'Bookmark Removed',
                                    description: 'Course removed from your bookmarks.',
                                    variant: 'default',
                                  });
                                }).catch((err: any) => {
                                  toast({
                                    title: 'Bookmark Failed',
                                    description: err.message || 'Could not remove bookmark.',
                                    variant: 'destructive',
                                  });
                                });
                              }}
                              className="absolute right-3 top-3 z-10 flex h-8 w-8 items-center justify-center rounded-full bg-white/90 backdrop-blur-sm text-blue-600 shadow-md hover:bg-white hover:shadow-lg transition-all duration-200"
                            >
                              <BookmarkCheck className="h-4 w-4 fill-current" />
                            </button>
                          )}
                          
                          <div className="absolute left-4 top-4">
                            <Badge className="bg-white/90 text-gray-800 hover:bg-white">
                              {course.category}
                            </Badge>
                          </div>
                          <div className="absolute left-4 bottom-4">
                            <Badge variant="secondary" className="bg-white/90 text-gray-800">
                              {course.difficulty}
                            </Badge>
                          </div>
                        </div>

                        <CardHeader>
                          <CardTitle className="text-lg transition-colors group-hover:text-blue-600 cursor-pointer">
                            {course.title}
                          </CardTitle>
                          <CardDescription className="text-sm">{course.description}</CardDescription>
                        </CardHeader>

                        <CardContent className="space-y-4">
                          <div className="text-sm text-gray-600">by {course.instructor}</div>

                          <div className="flex items-center justify-between text-sm text-gray-600">
                            <div className="flex items-center space-x-1">
                              <Clock className="h-4 w-4" />
                              <span>{course.totalLessons} lessons</span>
                            </div>
                            <div className="flex items-center space-x-1">
                              <Users className="h-4 w-4" />
                              <span>Bookmarked</span>
                            </div>
                            <div className="flex items-center space-x-1">
                              <Star className="h-4 w-4 fill-yellow-400 text-yellow-400" />
                              <span>Saved</span>
                            </div>
                          </div>

                          <div className="flex items-center justify-between pt-4">
                            <span className="text-2xl font-bold text-green-600">Free</span>
                            <Button 
                              size="sm" 
                              className="bg-blue-600 hover:bg-blue-700"
                              onClick={() => navigate(`/courses/${course.id}`)}
                            >
                              <BookOpen className="mr-2 h-4 w-4" />
                              View Course
                            </Button>
                          </div>
                        </CardContent>
                      </Card>
                    ))}
                  </div>
                </div>
              ) : (
                <div className="text-center py-12">
                  <Bookmark className="mx-auto h-12 w-12 text-gray-400 mb-4" />
                  <h3 className="text-lg font-semibold text-gray-900 mb-2">No bookmarked courses</h3>
                  <p className="text-gray-500 mb-4">Bookmark courses you're interested in to find them easily later.</p>
                  <Button onClick={() => navigate('/courses')}>
                    Browse Courses
                  </Button>
                </div>
              )}
            </div>
          </TabsContent>

          <TabsContent value="skills" className="space-y-6">
            <div className="grid gap-6 md:grid-cols-2">
              {/* Skills in Progress */}
              <Card>
                <CardHeader>
                  <CardTitle>Skills in Progress</CardTitle>
                  <CardDescription>Skills you're currently learning</CardDescription>
                </CardHeader>
                <CardContent className="space-y-3">
                  {dashboardData.skillsInProgress && dashboardData.skillsInProgress.length > 0 ? (
                    dashboardData.skillsInProgress.map((skill, index) => (
                      <div
                        key={index}
                        className="flex items-center justify-between rounded-lg bg-blue-50 p-4 transition-colors hover:bg-blue-100"
                      >
                        <div className="flex items-center space-x-3">
                          <div className="flex h-8 w-8 items-center justify-center rounded-full bg-blue-100">
                            <Target className="h-4 w-4 text-blue-600" />
                          </div>
                          <span className="font-medium text-blue-900">{skill}</span>
                        </div>
                        <Badge variant="secondary" className="bg-blue-200 text-blue-800">
                          Learning
                        </Badge>
                      </div>
                    ))
                  ) : (
                    <div className="text-center py-8">
                      <p className="text-gray-500">No skills in progress</p>
                      <p className="text-sm text-gray-400 mt-2">Enroll in courses to start learning new skills</p>
                    </div>
                  )}
                </CardContent>
              </Card>

              {/* Current Skills */}
              <Card>
                <CardHeader>
                  <CardTitle>Current Skills</CardTitle>
                  <CardDescription>Skills you've mastered</CardDescription>
                </CardHeader>
                <CardContent className="space-y-3">
                  {dashboardData.currentSkills && dashboardData.currentSkills.length > 0 ? (
                    dashboardData.currentSkills.map((skill, index) => (
                      <div
                        key={index}
                        className="flex items-center justify-between rounded-lg bg-green-50 p-4 transition-colors hover:bg-green-100"
                      >
                        <div className="flex items-center space-x-3">
                          <div className="flex h-8 w-8 items-center justify-center rounded-full bg-green-100">
                            <Trophy className="h-4 w-4 text-green-600" />
                          </div>
                          <span className="font-medium text-green-900">{skill}</span>
                        </div>
                        <Badge variant="secondary" className="bg-green-200 text-green-800">
                          Mastered
                        </Badge>
                      </div>
                    ))
                  ) : (
                    <div className="text-center py-8">
                      <p className="text-gray-500">No mastered skills yet</p>
                      <p className="text-sm text-gray-400 mt-2">Complete courses to master new skills</p>
                    </div>
                  )}
                </CardContent>
              </Card>
            </div>
          </TabsContent>

          <TabsContent value="achievements" className="space-y-6">
            <div className="grid gap-6 md:grid-cols-2 lg:grid-cols-3">
              {achievements.length > 0 ? (
                achievements.map((achievement, index) => {
                  const Icon = achievement.icon === 'Trophy' ? Trophy : 
                              achievement.icon === 'Target' ? Target : 
                              achievement.icon === 'Award' ? Award :
                              achievement.icon === 'Star' ? Star :
                              achievement.icon === 'Bookmark' ? Bookmark : Award;
                  return (
                    <Card key={index} className="text-center">
                      <CardContent className="p-6">
                        <div className="mx-auto mb-4 flex h-16 w-16 items-center justify-center rounded-full bg-yellow-100">
                          <Icon className="h-8 w-8 text-yellow-600" />
                        </div>
                        <h3 className="mb-2 font-semibold text-gray-900">{achievement.title}</h3>
                        <p className="mb-2 text-sm text-gray-600">{achievement.description}</p>
                        <p className="text-xs text-gray-500">{achievement.date}</p>
                        <Badge variant="secondary" className="mt-2">
                          {achievement.category}
                        </Badge>
                      </CardContent>
                    </Card>
                  );
                })
              ) : (
                <div className="col-span-full text-center py-8">
                  <p className="text-gray-500 mb-4">No achievements yet</p>
                  <p className="text-sm text-gray-400">Complete courses to earn achievements!</p>
                </div>
              )}
            </div>
          </TabsContent>
        </Tabs>
      </div>
    </div>
  );
};

export default Dashboard;
