import React, { useState, useEffect } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from '@/components/ui/card';
import { Button } from '@/components/ui/button';
import { Badge } from '@/components/ui/badge';
import { Progress } from '@/components/ui/progress';
import { 
  BookOpen, 
  Clock, 
  ArrowLeft,
  ArrowRight,
  CheckCircle,
  Trophy,
  Target
} from 'lucide-react';
import Navbar from '@/components/Navbar';
import CourseCompletionCelebration from '@/components/CourseCompletionCelebration';
import { useAuth } from '@/hooks/useAuth';
import * as courseService from '@/services/course.service';
import type { CourseResponse } from '@/types';
import { useToast } from '@/hooks/use-toast';
import ReactMarkdown from 'react-markdown';
import remarkGfm from 'remark-gfm';
import { LessonContentType } from '@/types/utils/LessonContentType';
import type { LessonContent } from '@/types/utils/LessonContent';

const LessonPage = () => {
  const { courseId, lessonId } = useParams<{ courseId: string; lessonId: string }>();
  const navigate = useNavigate();
  const { user, loading: authLoading } = useAuth();
  const { toast } = useToast();
  
  const [course, setCourse] = useState<CourseResponse | null>(null);
  const [currentLesson, setCurrentLesson] = useState<{ title: string; content: LessonContent; order: number } | null>(null);
  const [currentModule, setCurrentModule] = useState<{ title: string; order: number } | null>(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [completing, setCompleting] = useState(false);
  const [completingCourse, setCompletingCourse] = useState(false);
  const [showCelebration, setShowCelebration] = useState(false);

  useEffect(() => {
    const fetchCourse = async () => {
      if (!courseId || !lessonId) {
        setError('Course ID and Lesson ID are required');
        setLoading(false);
        return;
      }

      try {
        setLoading(true);
        setError(null);
        
        const fetchedCourse = await courseService.getCourse(courseId);
        setCourse(fetchedCourse);
        
        // Find the current lesson
        let foundLesson: { title: string; content: LessonContent; order: number } | null = null;
        let foundModule: { title: string; order: number } | null = null;
        
        for (const module of fetchedCourse.modules) {
          const lesson = module.lessons.find(l => l.order.toString() === lessonId);
          if (lesson) {
            foundLesson = lesson;
            foundModule = { title: module.title, order: module.order };
            break;
          }
        }
        
        if (!foundLesson) {
          setError('Lesson not found');
          setLoading(false);
          return;
        }
        
        setCurrentLesson(foundLesson);
        setCurrentModule(foundModule);
      } catch (err: any) {
        console.error('Error fetching course:', err);
        if (err.status === 401) {
          navigate(`/login?redirect=${encodeURIComponent(`/courses/${courseId}/lessons/${lessonId}`)}`);
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
  }, [courseId, lessonId, authLoading, navigate]);

  const isEnrolled = !!course?.enrolledUsers.find(u => u.userId === user?.id);
  const userEnrollment = course?.enrolledUsers.find(u => u.userId === user?.id);
  const userCurrentLesson = userEnrollment?.currentLesson || 0;
  const totalNumberOfLessons = userEnrollment?.totalNumberOfLessons || 0;
  const completedLessons = userCurrentLesson; // Use currentLesson as completed lessons count
  const isCourseCompleted = userCurrentLesson >= totalNumberOfLessons;
  

  

  
  // Check if current lesson is already completed
  const isCurrentLessonCompleted = () => {
    if (!course || !currentLesson) return false;
    const lessonIndex = currentLesson.order;
    // Lesson is completed if user's currentLesson is greater than this lesson's order
    return userCurrentLesson > lessonIndex;
  };

  // Check if this is the next lesson to be completed
  const isNextLessonToComplete = () => {
    if (!course || !currentLesson) return false;
    const lessonIndex = currentLesson.order;
    // This is the next lesson if user's currentLesson equals this lesson's order
    return userCurrentLesson === lessonIndex;
  };



  const handleCompleteLesson = async () => {
    if (!courseId || !lessonId || !user?.id || !course) return;
    setCompleting(true);
    try {
      const updatedCourse = await courseService.completeLesson(courseId, lessonId, user.id, course);
      setCourse(updatedCourse);
      
      // Force refresh the course data to ensure we have the latest state
      // Add a small delay to ensure server has processed the update
      setTimeout(async () => {
        const refreshedCourse = await courseService.getCourse(courseId);
        setCourse(refreshedCourse);
      }, 500);
      
      toast({
        title: 'Lesson Completed!',
        description: 'Great job! You\'ve completed this lesson.',
        variant: 'success',
      });
    } catch (err: any) {
      console.error('Error completing lesson:', err);
      toast({
        title: 'Failed to Complete Lesson',
        description: err.message || 'Could not complete lesson.',
        variant: 'destructive',
      });
    } finally {
      setCompleting(false);
    }
  };

  const handleCompleteCourse = async () => {
    if (!courseId || !user?.id || !course) return;
    setCompletingCourse(true);
    try {
      const updatedCourse = await courseService.completeCourse(courseId, user.id, course);
      setCourse(updatedCourse);
      
      // Force refresh the course data to ensure we have the latest state
      // Add a small delay to ensure server has processed the update
      setTimeout(async () => {
        const refreshedCourse = await courseService.getCourse(courseId);
        setCourse(refreshedCourse);
      }, 500);
      
      // Show celebration animation
      setShowCelebration(true);
      
      toast({
        title: 'Course Completed!',
        description: 'Congratulations! You\'ve completed the entire course.',
        variant: 'success',
      });
    } catch (err: any) {
      toast({
        title: 'Failed to Complete Course',
        description: err.message || 'Could not complete course.',
        variant: 'destructive',
      });
    } finally {
      setCompletingCourse(false);
    }
  };

  const handleCelebrationClose = () => {
    setShowCelebration(false);
    navigate('/dashboard');
  };

  const getNextLesson = () => {
    if (!course || !currentLesson) return null;
    
    for (const module of course.modules) {
      for (let i = 0; i < module.lessons.length; i++) {
        if (module.lessons[i].order === currentLesson.order) {
          if (i + 1 < module.lessons.length) {
            return { module, lesson: module.lessons[i + 1] };
          }
          // Check next module
          const moduleIndex = course.modules.findIndex(m => m.order === module.order);
          if (moduleIndex + 1 < course.modules.length) {
            const nextModule = course.modules[moduleIndex + 1];
            if (nextModule.lessons.length > 0) {
              return { module: nextModule, lesson: nextModule.lessons[0] };
            }
          }
        }
      }
    }
    return null;
  };

  const getPrevLesson = () => {
    if (!course || !currentLesson) return null;
    
    for (const module of course.modules) {
      for (let i = 0; i < module.lessons.length; i++) {
        if (module.lessons[i].order === currentLesson.order) {
          if (i > 0) {
            return { module, lesson: module.lessons[i - 1] };
          }
          // Check previous module
          const moduleIndex = course.modules.findIndex(m => m.order === module.order);
          if (moduleIndex > 0) {
            const prevModule = course.modules[moduleIndex - 1];
            if (prevModule.lessons.length > 0) {
              return { module: prevModule, lesson: prevModule.lessons[prevModule.lessons.length - 1] };
            }
          }
        }
      }
    }
    return null;
  };

  const navigateToLesson = (lessonOrder: number) => {
    navigate(`/courses/${courseId}/lessons/${lessonOrder}`);
  };

  const truncateText = (text: string, maxLength: number = 30) => {
    if (text.length <= maxLength) return text;
    return text.substring(0, maxLength) + '...';
  };

  // Show loading state
  if (authLoading || loading) {
    return (
      <div className="min-h-screen bg-gray-50">
        <Navbar />
        <div className="flex h-96 items-center justify-center">
          <div className="flex items-center space-x-2">
            <div className="h-6 w-6 animate-spin rounded-full border-2 border-blue-600 border-t-transparent"></div>
            <span>Loading lesson...</span>
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
            <Button onClick={() => navigate(`/courses/${courseId}`)}>Back to Course</Button>
          </div>
        </div>
      </div>
    );
  }

  if (!course || !currentLesson || !currentModule) {
    return (
      <div className="min-h-screen bg-gray-50">
        <Navbar />
        <div className="flex h-96 items-center justify-center">
          <div className="text-center">
            <p className="text-lg text-gray-600 mb-4">Lesson not found</p>
            <Button onClick={() => navigate(`/courses/${courseId}`)}>Back to Course</Button>
          </div>
        </div>
      </div>
    );
  }

  if (!isEnrolled) {
    return (
      <div className="min-h-screen bg-gray-50">
        <Navbar />
        <div className="flex h-96 items-center justify-center">
          <div className="text-center">
            <p className="text-lg text-gray-600 mb-4">You need to enroll in this course to access lessons</p>
            <Button onClick={() => navigate(`/courses/${courseId}`)}>Back to Course</Button>
          </div>
        </div>
      </div>
    );
  }

  const nextLesson = getNextLesson();
  const prevLesson = getPrevLesson();

  return (
    <div className="min-h-screen bg-gray-50">
      <Navbar />

      {/* Course Completion Celebration */}
      <CourseCompletionCelebration
        courseTitle={course?.title || ''}
        isVisible={showCelebration}
        onClose={handleCelebrationClose}
      />

      <div className="mx-auto max-w-7xl px-4 py-8 sm:px-6 lg:px-8">
        {/* Breadcrumb */}
        <div className="mb-6">
          <Button
            variant="ghost"
            onClick={() => navigate(`/courses/${courseId}`)}
            className="mb-4 flex items-center gap-2 text-gray-600 hover:text-gray-900"
          >
            <ArrowLeft className="h-4 w-4" />
            Back to Course
          </Button>
        </div>

        {/* Progress Bar */}
        <div className="mb-8">
          <div className="flex items-center justify-between mb-2">
            <h2 className="text-lg font-semibold text-gray-900">Course Progress</h2>
            <span className="text-sm text-gray-600">{completedLessons} of {totalNumberOfLessons} lessons completed</span>
          </div>
          <Progress value={totalNumberOfLessons > 0 ? (completedLessons / totalNumberOfLessons) * 100 : 0} className="h-3" />
          <div className="mt-2 text-sm text-gray-600">
            {totalNumberOfLessons > 0 ? ((completedLessons / totalNumberOfLessons) * 100).toFixed(1) : 0}% complete
          </div>
        </div>

        {/* Lesson Header */}
        <div className="mb-8">
          <div className="flex items-center gap-2 mb-4">
            <Badge variant="secondary">{currentModule.title}</Badge>
            <span className="text-gray-400">•</span>
            <span className="text-sm text-gray-600">Lesson {currentLesson.order + 1}</span>
          </div>
          <h1 className="text-3xl font-bold text-gray-900 mb-4">{currentLesson.title}</h1>
        </div>

        {/* Lesson Content */}
        <div className="grid gap-8 lg:grid-cols-3">
          {/* Main Content */}
          <div className="lg:col-span-2">
            <Card className="mb-8">
              <CardContent className="p-8">
                {(() => {
                  const { type, content } = currentLesson.content;
                  switch (type) {
                    case LessonContentType.TEXT:
                      return (
                        <div className="prose max-w-none prose-table:border prose-table:border-gray-300 prose-th:border prose-th:border-gray-300 prose-th:bg-gray-50 prose-th:p-2 prose-td:border prose-td:border-gray-300 prose-td:p-2">
                          <ReactMarkdown remarkPlugins={[remarkGfm]}>{content}</ReactMarkdown>
                        </div>
                      );
                    case LessonContentType.HTML:
                      return (
                        <div 
                          className="prose prose-lg max-w-none prose-headings:text-gray-900 prose-p:text-gray-700 prose-code:bg-blue-50 prose-code:text-blue-800 prose-code:px-2 prose-code:py-1 prose-code:rounded prose-code:font-mono prose-pre:bg-gray-50 prose-pre:text-gray-800 prose-pre:border prose-pre:border-gray-200 prose-ul:text-gray-700 prose-ol:text-gray-700 prose-li:text-gray-700 prose-strong:text-gray-900 prose-em:text-gray-700" 
                          dangerouslySetInnerHTML={{ __html: content }} 
                        />
                      );
                    case LessonContentType.VIDEO: {
                      const getEmbedUrl = (url: string) => {
                        // Handle different YouTube URL formats
                        const videoId = url.match(/(?:youtube\.com\/watch\?v=|youtu\.be\/|youtube\.com\/embed\/)([^&\n?#]+)/)?.[1];
                        if (videoId) {
                          return `https://www.youtube.com/embed/${videoId}`;
                        }
                        return url; // Fallback to original URL
                      };
                      
                      return (
                        <div className="aspect-video w-full mb-6">
                          <iframe 
                            src={getEmbedUrl(content)} 
                            title="Video" 
                            allowFullScreen 
                            className="w-full h-full rounded-lg border" 
                          />
                        </div>
                      );
                    }
                    case LessonContentType.URL:
                      return (
                        <div className="aspect-video w-full mb-6">
                          <iframe 
                            src={content} 
                            title="External Resource" 
                            allowFullScreen 
                            className="w-full h-full rounded-lg border" 
                          />
                        </div>
                      );
                    case LessonContentType.AUDIO:
                      return (
                        <div className="mb-6">
                          <audio controls className="w-full">
                            <source src={content} />
                          </audio>
                        </div>
                      );
                    case LessonContentType.IMAGE:
                      return (
                        <div className="mb-6">
                          <img src={content} alt="Lesson visual" className="w-full rounded-lg border" />
                        </div>
                      );
                    default:
                      return <div>Unsupported lesson content type.</div>;
                  }
                })()}
              </CardContent>
            </Card>
          </div>

          {/* Sidebar */}
          <div className="lg:col-span-1">
            <div className="space-y-6">
              {/* Course Info */}
              <Card>
                <CardHeader>
                  <CardTitle className="text-lg">{course.title}</CardTitle>
                  <CardDescription>by {course.instructor}</CardDescription>
                </CardHeader>
                <CardContent className="space-y-4">
                  <div className="flex items-center gap-2 text-sm text-gray-600">
                    <BookOpen className="h-4 w-4" />
                    <span>{totalNumberOfLessons} total lessons</span>
                  </div>
                  <div className="flex items-center gap-2 text-sm text-gray-600">
                    <Target className="h-4 w-4" />
                    <span>{completedLessons} completed</span>
                  </div>
                  <div className="flex items-center gap-2 text-sm text-gray-600">
                    <Clock className="h-4 w-4" />
                    <span>~15 min per lesson</span>
                  </div>
                </CardContent>
              </Card>

              {/* Action Buttons */}
              <Card>
                <CardHeader>
                  <CardTitle className="text-lg">Actions</CardTitle>
                </CardHeader>
                <CardContent className="space-y-4">
                  <Button 
                    onClick={handleCompleteLesson}
                    disabled={completing || isCurrentLessonCompleted() || !isNextLessonToComplete()}
                    className={`w-full ${
                      isCurrentLessonCompleted() 
                        ? 'bg-gray-400 cursor-not-allowed' 
                        : !isNextLessonToComplete()
                        ? 'bg-gray-400 cursor-not-allowed'
                        : 'bg-green-600 hover:bg-green-700'
                    }`}
                  >
                    {completing ? (
                      <>
                        <div className="mr-2 h-4 w-4 animate-spin rounded-full border-2 border-white border-t-transparent"></div>
                        Completing...
                      </>
                    ) : isCurrentLessonCompleted() ? (
                      <>
                        <CheckCircle className="mr-2 h-4 w-4" />
                        Lesson Completed
                      </>
                    ) : !isNextLessonToComplete() ? (
                      <>
                        <Clock className="mr-2 h-4 w-4" />
                        Complete Previous Lessons First
                      </>
                    ) : (
                      <>
                        <CheckCircle className="mr-2 h-4 w-4" />
                        Complete Lesson
                      </>
                    )}
                  </Button>

                  {isCourseCompleted && (
                    <Button 
                      onClick={handleCompleteCourse}
                      disabled={completingCourse}
                      className="w-full bg-yellow-600 hover:bg-yellow-700"
                    >
                      {completingCourse ? (
                        <>
                          <div className="mr-2 h-4 w-4 animate-spin rounded-full border-2 border-white border-t-transparent"></div>
                          Completing Course...
                        </>
                      ) : (
                        <>
                          <Trophy className="mr-2 h-4 w-4" />
                          Complete Course
                        </>
                      )}
                    </Button>
                  )}
                </CardContent>
              </Card>

              {/* Navigation */}
              <Card>
                <CardHeader>
                  <CardTitle className="text-lg">Navigation</CardTitle>
                </CardHeader>
                <CardContent className="space-y-3">
                  {prevLesson && (
                    <Button 
                      variant="outline" 
                      className="w-full justify-start"
                      onClick={() => navigateToLesson(prevLesson.lesson.order)}
                    >
                      <ArrowLeft className="mr-2 h-4 w-4 flex-shrink-0" />
                      <span className="truncate">Previous: {truncateText(prevLesson.lesson.title)}</span>
                    </Button>
                  )}
                  
                  {nextLesson && (
                    <Button 
                      variant="outline" 
                      className="w-full justify-start"
                      onClick={() => navigateToLesson(nextLesson.lesson.order)}
                    >
                      <span className="truncate">Next: {truncateText(nextLesson.lesson.title)}</span>
                      <ArrowRight className="ml-2 h-4 w-4 flex-shrink-0" />
                    </Button>
                  )}
                  
                  {!nextLesson && (
                    <div className="text-center text-sm text-gray-600">
                      🎉 This is the last lesson!
                    </div>
                  )}
                </CardContent>
              </Card>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
};

export default LessonPage; 