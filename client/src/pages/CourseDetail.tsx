import React, { useState, useEffect, useContext } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from '@/components/ui/card';
import { Button } from '@/components/ui/button';
import { Badge } from '@/components/ui/badge';
import { Accordion, AccordionContent, AccordionItem, AccordionTrigger } from '@/components/ui/accordion';
import { 
  BookOpen, 
  Clock, 
  Users, 
  Star, 
  Loader2, 
  ArrowLeft,
  Play,
  CheckCircle,
  User,
  Bookmark,
  BookmarkCheck
} from 'lucide-react';
import Navbar from '@/components/Navbar';
import CertificateGenerator from '@/components/CertificateGenerator';
import { AuthContext } from '@/contexts/AuthContext';
import * as courseService from '@/services/course.service';
import type { CourseResponse } from '@/types';
import { useToast } from '@/hooks/use-toast';


const CourseDetail = () => {
  const { courseId } = useParams<{ courseId: string }>();
  const navigate = useNavigate();
  const { user, loading: authLoading, updateUserBookmarks } = useContext(AuthContext);
  const [course, setCourse] = useState<CourseResponse | null>(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const { toast } = useToast();
  const [enrolling, setEnrolling] = useState(false);
  const [bookmarking, setBookmarking] = useState(false);

  useEffect(() => {
    const fetchCourse = async () => {
      if (!courseId) {
        setError('Course ID is required');
        setLoading(false);
        return;
      }

      try {
        setLoading(true);
        setError(null);
        
        const fetchedCourse = await courseService.getCourse(courseId);
        setCourse(fetchedCourse);
      } catch (err: any) {
        console.error('Error fetching course:', err);
        if (err.status === 401) {
          // User is not authenticated, redirect to login with return URL
          navigate(`/login?redirect=${encodeURIComponent(`/courses/${courseId}`)}`);
          return;
        }
        setError(err.message || 'Failed to fetch course details');
      } finally {
        setLoading(false);
      }
    };

    if (!authLoading) {
      fetchCourse();
    }
  }, [courseId, authLoading, navigate]);

  const handleEnroll = async () => {
    if (!courseId || !user?.id) return;
    setEnrolling(true);
    try {
      const updatedCourse = await courseService.enrollInCourse(courseId, user.id);
      setCourse(updatedCourse);
      toast({
        title: 'Enrolled!',
        description: 'You have been successfully enrolled in this course.',
        variant: 'success',
      });
    } catch (err: any) {
      toast({
        title: 'Enrollment Failed',
        description: err.message || 'Could not enroll in course.',
        variant: 'destructive',
      });
    } finally {
      setEnrolling(false);
    }
  };

  const isEnrolled = !!course?.enrolledUsers.find(u => u.userId === user?.id);
  const userEnrollment = course?.enrolledUsers.find(u => u.userId === user?.id);
  const isCourseCompleted = userEnrollment?.progress === 100;
  const isBookmarked = !!user?.bookmarkedCourseIds?.includes(courseId || '');

  const handleBookmark = async () => {
    if (!courseId || !user?.id) return;
    setBookmarking(true);
    try {
      if (isBookmarked) {
        await courseService.unbookmarkCourse(courseId, user.id);
        // Update user's bookmarked courses in context
        updateUserBookmarks(courseId, false);
        toast({
          title: 'Bookmark Removed',
          description: 'Course removed from your bookmarks.',
          variant: 'default',
        });
      } else {
        await courseService.bookmarkCourse(courseId, user.id);
        // Update user's bookmarked courses in context
        updateUserBookmarks(courseId, true);
        toast({
          title: 'Bookmarked!',
          description: 'Course added to your bookmarks.',
          variant: 'success',
        });
      }
    } catch (err: any) {
      toast({
        title: 'Bookmark Failed',
        description: err.message || 'Could not update bookmark.',
        variant: 'destructive',
      });
    } finally {
      setBookmarking(false);
    }
  };

  const handleUnenroll = async () => {
    if (!courseId || !user?.id) return;
    setEnrolling(true);
    try {
      await courseService.unenrollFromCourse(courseId, user.id);
      // Refetch course details to update enrolled users
      const updatedCourse = await courseService.getCourse(courseId);
      setCourse(updatedCourse);
      toast({
        title: 'Unenrolled',
        description: 'You have been unenrolled from this course.',
        variant: 'success',
      });
    } catch (err: any) {
      toast({
        title: 'Unenroll Failed',
        description: err.message || 'Could not unenroll from course.',
        variant: 'destructive',
      });
    } finally {
      setEnrolling(false);
    }
  };



  // Show loading state
  if (authLoading || loading) {
    return (
      <div className="min-h-screen bg-gray-50">
        <Navbar />
        <div className="flex h-96 items-center justify-center">
          <div className="flex items-center space-x-2">
            <Loader2 className="h-6 w-6 animate-spin" />
            <span>Loading course details...</span>
          </div>
        </div>
      </div>
    );
  }

  // Show error state
  if (error) {
    return (
      <div className="min-h-screen bg-gray-50">
        <Navbar />
        <div className="flex h-96 items-center justify-center">
          <div className="text-center">
            <p className="text-lg text-red-600 mb-4">{error}</p>
            <Button onClick={() => navigate('/courses')}>Back to Courses</Button>
          </div>
        </div>
      </div>
    );
  }

  if (!course) {
    return (
      <div className="min-h-screen bg-gray-50">
        <Navbar />
        <div className="flex h-96 items-center justify-center">
          <div className="text-center">
            <p className="text-lg text-gray-600 mb-4">Course not found</p>
            <Button onClick={() => navigate('/courses')}>Back to Courses</Button>
          </div>
        </div>
      </div>
    );
  }

  const totalLessons = course.modules.reduce((total, module) => total + module.lessons.length, 0);
  const estimatedDuration = totalLessons * 15; // Assuming 15 minutes per lesson

  return (
    <div className="min-h-screen bg-gray-50">
      <Navbar />

      <div className="mx-auto max-w-7xl px-4 py-8 sm:px-6 lg:px-8">
        {/* Breadcrumb */}
        <div className="mb-6">
          <Button
            variant="ghost"
            onClick={() => navigate('/courses')}
            className="mb-4 flex items-center gap-2 text-gray-600 hover:text-gray-900"
          >
            <ArrowLeft className="h-4 w-4" />
            Back to Courses
          </Button>
        </div>

        {/* Course Header */}
        <div className="mb-8">
          <div className="grid gap-8 lg:grid-cols-3">
            {/* Course Info */}
            <div className="lg:col-span-2">
              <div className="mb-6">
                <Badge variant="secondary" className="mb-2">
                  {course.level}
                </Badge>
                <h1 className="mb-4 text-4xl font-bold text-gray-900">{course.title}</h1>
                <p className="mb-6 text-xl text-gray-600">{course.description}</p>
                
                <div className="mb-6 flex flex-wrap items-center gap-4 text-sm text-gray-600">
                  <div className="flex items-center gap-1">
                    <User className="h-4 w-4" />
                    <span>by {course.instructor}</span>
                  </div>
                  <div className="flex items-center gap-1">
                    <Clock className="h-4 w-4" />
                    <span>{estimatedDuration} min</span>
                  </div>
                  <div className="flex items-center gap-1">
                    <BookOpen className="h-4 w-4" />
                    <span>{totalLessons} lessons</span>
                  </div>
                  <div className="flex items-center gap-1">
                    <Users className="h-4 w-4" />
                    <span>{course.numberOfEnrolledUsers.toLocaleString()} enrolled</span>
                  </div>
                  <div className="flex items-center gap-1">
                    <Star className="h-4 w-4 fill-yellow-400 text-yellow-400" />
                    <span>{course.rating}</span>
                  </div>
                </div>

                <div className="mb-6">
                  <h3 className="mb-3 text-lg font-semibold text-gray-900">Skills you'll learn</h3>
                  <div className="flex flex-wrap gap-2">
                    {course.skills.map((skill, index) => (
                      <Badge key={index} variant="outline" className="bg-blue-50 text-blue-700">
                        {skill}
                      </Badge>
                    ))}
                  </div>
                </div>

                <div className="mb-6">
                  <h3 className="mb-3 text-lg font-semibold text-gray-900">Categories</h3>
                  <div className="flex flex-wrap gap-2">
                    {course.categories.map((category, index) => (
                      <Badge key={index} variant="secondary">
                        {category}
                      </Badge>
                    ))}
                  </div>
                </div>
              </div>
            </div>

            {/* Course Card */}
            <div className="lg:col-span-1">
              <Card className="sticky top-24">
                <CardHeader>
                  <CardTitle className="text-2xl font-bold text-green-600">Free</CardTitle>
                  <CardDescription>Lifetime access to this course</CardDescription>
                </CardHeader>
                <CardContent className="space-y-4">
                  <div className="space-y-3">
                    <div className="flex items-center gap-2 text-sm text-gray-600">
                      <CheckCircle className="h-4 w-4 text-green-500" />
                      <span>{totalLessons} lessons</span>
                    </div>
                    <div className="flex items-center gap-2 text-sm text-gray-600">
                      <CheckCircle className="h-4 w-4 text-green-500" />
                      <span>Certificate of completion</span>
                    </div>
                    <div className="flex items-center gap-2 text-sm text-gray-600">
                      <CheckCircle className="h-4 w-4 text-green-500" />
                      <span>Lifetime access</span>
                    </div>
                    <div className="flex items-center gap-2 text-sm text-gray-600">
                      <CheckCircle className="h-4 w-4 text-green-500" />
                      <span>Mobile and desktop access</span>
                    </div>
                  </div>
                  
                  {isEnrolled ? (
                    <div className="space-y-3">
                      {isCourseCompleted && (
                        <CertificateGenerator
                          courseTitle={course.title}
                          userFirstName={user?.firstName || 'Student'}
                          userLastName={user?.lastName || ''}
                          completionDate={new Date().toLocaleDateString()}
                          instructor={course.instructor}
                        />
                      )}
                      <Button size="lg" variant="destructive" className="w-full" onClick={handleUnenroll} disabled={enrolling}>
                        {enrolling ? (
                          <>
                            <Loader2 className="mr-2 h-4 w-4 animate-spin" />
                            Unenrolling...
                          </>
                        ) : (
                          <>Unenroll</>
                        )}
                      </Button>
                    </div>
                  ) : (
                    <Button size="lg" className="w-full bg-blue-600 hover:bg-blue-700" onClick={handleEnroll} disabled={enrolling}>
                      {enrolling ? (
                        <>
                          <Loader2 className="mr-2 h-4 w-4 animate-spin" />
                          Enrolling...
                        </>
                      ) : (
                        <>
                          <Play className="mr-2 h-4 w-4" />
                          Enroll Now
                        </>
                      )}
                    </Button>
                  )}
                  
                  {/* Bookmark Button - Only show if user is logged in */}
                  {user && (
                    <Button 
                      variant={isBookmarked ? "default" : "outline"} 
                      size="sm" 
                      className="w-full mb-3" 
                      onClick={handleBookmark} 
                      disabled={bookmarking}
                    >
                      {bookmarking ? (
                        <>
                          <Loader2 className="mr-2 h-4 w-4 animate-spin" />
                          {isBookmarked ? 'Removing...' : 'Adding...'}
                        </>
                      ) : (
                        <>
                          {isBookmarked ? (
                            <BookmarkCheck className="mr-2 h-4 w-4" />
                          ) : (
                            <Bookmark className="mr-2 h-4 w-4" />
                          )}
                          {isBookmarked ? 'Bookmarked' : 'Bookmark'}
                        </>
                      )}
                    </Button>
                  )}
                </CardContent>
              </Card>
            </div>
          </div>
        </div>

        {/* Course Content */}
        <div className="mb-8">
          <h2 className="mb-6 text-2xl font-bold text-gray-900">Course Content</h2>
          
          <Accordion type="single" collapsible className="space-y-4">
            {course.modules.map((module, moduleIndex) => (
              <AccordionItem 
                key={moduleIndex} 
                value={`module-${moduleIndex}`}
                className="border rounded-lg"
              >
                <AccordionTrigger className="px-6 py-4 hover:no-underline">
                  <div className="flex items-center justify-between w-full">
                    <div className="flex items-center gap-3">
                      <div className="flex h-8 w-8 items-center justify-center rounded-full bg-blue-100 text-blue-600">
                        {module.order + 1}
                      </div>
                      <div className="text-left">
                        <h3 className="font-semibold text-gray-900">{module.title}</h3>
                        {module.description && (
                          <p className="text-sm text-gray-600">{module.description}</p>
                        )}
                      </div>
                    </div>
                    <div className="flex items-center gap-2 text-sm text-gray-500">
                      <span>{module.lessons.length} lessons</span>
                    </div>
                  </div>
                </AccordionTrigger>
                <AccordionContent className="px-6 pb-4">
                  <div className="space-y-3">
                    {module.lessons.map((lesson, lessonIndex) => (
                      <div
                        key={lessonIndex}
                        className={`flex items-center justify-between rounded-lg border p-3 ${isEnrolled ? 'hover:bg-gray-50 cursor-pointer' : 'opacity-60 cursor-not-allowed'}`}
                        onClick={() => isEnrolled && navigate(`/courses/${courseId}/lessons/${lesson.order}`)}
                      >
                        <div className="flex items-center gap-3">
                          <div className="flex h-6 w-6 items-center justify-center rounded-full bg-gray-100 text-gray-600">
                            {lesson.order + 1}
                          </div>
                          <div>
                            <h4 className="font-medium text-gray-900">{lesson.title}</h4>
                            <p className="text-sm text-gray-600">{lesson.description}</p>
                          </div>
                        </div>
                        <div className="flex items-center gap-2">
                          <Play className="h-4 w-4 text-gray-400" />
                          <span className="text-sm text-gray-500">15 min</span>
                        </div>
                      </div>
                    ))}
                  </div>
                </AccordionContent>
              </AccordionItem>
            ))}
          </Accordion>
        </div>

        {/* Course Stats */}
        <div className="mb-8">
          <h2 className="mb-6 text-2xl font-bold text-gray-900">Course Statistics</h2>
          <div className="grid gap-6 md:grid-cols-2 lg:grid-cols-4">
            <Card>
              <CardContent className="p-6">
                <div className="flex items-center gap-3">
                  <div className="flex h-10 w-10 items-center justify-center rounded-full bg-blue-100">
                    <Users className="h-5 w-5 text-blue-600" />
                  </div>
                  <div>
                    <p className="text-2xl font-bold text-gray-900">{course.numberOfEnrolledUsers.toLocaleString()}</p>
                    <p className="text-sm text-gray-600">Students enrolled</p>
                  </div>
                </div>
              </CardContent>
            </Card>
            
            <Card>
              <CardContent className="p-6">
                <div className="flex items-center gap-3">
                  <div className="flex h-10 w-10 items-center justify-center rounded-full bg-green-100">
                    <BookOpen className="h-5 w-5 text-green-600" />
                  </div>
                  <div>
                    <p className="text-2xl font-bold text-gray-900">{totalLessons}</p>
                    <p className="text-sm text-gray-600">Total lessons</p>
                  </div>
                </div>
              </CardContent>
            </Card>
            
            <Card>
              <CardContent className="p-6">
                <div className="flex items-center gap-3">
                  <div className="flex h-10 w-10 items-center justify-center rounded-full bg-yellow-100">
                    <Clock className="h-5 w-5 text-yellow-600" />
                  </div>
                  <div>
                    <p className="text-2xl font-bold text-gray-900">{estimatedDuration}</p>
                    <p className="text-sm text-gray-600">Minutes of content</p>
                  </div>
                </div>
              </CardContent>
            </Card>
            
            <Card>
              <CardContent className="p-6">
                <div className="flex items-center gap-3">
                  <div className="flex h-10 w-10 items-center justify-center rounded-full bg-purple-100">
                    <Star className="h-5 w-5 text-purple-600" />
                  </div>
                  <div>
                    <p className="text-2xl font-bold text-gray-900">{course.rating}</p>
                    <p className="text-sm text-gray-600">Course rating</p>
                  </div>
                </div>
              </CardContent>
            </Card>
          </div>
        </div>
      </div>


    </div>
  );
};

export default CourseDetail; 