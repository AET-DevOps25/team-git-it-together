import React, { useState, useEffect, useContext } from 'react';
import { useNavigate } from 'react-router-dom';
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from '@/components/ui/card';
import { Button } from '@/components/ui/button';
import { Badge } from '@/components/ui/badge';
import { Input } from '@/components/ui/input';
import { Select, SelectContent, SelectItem, SelectTrigger, SelectValue } from '@/components/ui/select';
import { BookOpen, Users, Star, Loader2, Search, X, Bookmark, BookmarkCheck, Brain } from 'lucide-react';
import Navbar from '@/components/Navbar';
import { AuthContext } from '@/contexts/AuthContext';
import * as courseService from '@/services/course.service';
import type { CourseSummaryResponse } from '@/types';
import { Level, Language } from '@/types';
import { useDebounce } from '../hooks/use-debounce';
import { useToast } from '@/hooks/use-toast';

const Courses = () => {
  const navigate = useNavigate();
  const { user, loading: authLoading, updateUserBookmarks } = useContext(AuthContext);
  const { toast } = useToast();
  const [activeCategory, setActiveCategory] = useState('All');
  const [courses, setCourses] = useState<CourseSummaryResponse[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [bookmarking, setBookmarking] = useState<string | null>(null); // Track which course is being bookmarked
  
  // Search state
  const [searchTitle, setSearchTitle] = useState('');
  const [searchInstructor, setSearchInstructor] = useState('');
  const [searchLevel, setSearchLevel] = useState<Level | 'all'>('all');
  const [searchLanguage, setSearchLanguage] = useState<Language | 'all'>('all');
  const [searchSkill, setSearchSkill] = useState('');
  const [searchCategory, setSearchCategory] = useState('');
  const [isSearching, setIsSearching] = useState(false);

  // Debounce search inputs
  const debouncedSearchTitle = useDebounce(searchTitle, 300);
  const debouncedSearchInstructor = useDebounce(searchInstructor, 300);
  const debouncedSearchSkill = useDebounce(searchSkill, 300);
  const debouncedSearchCategory = useDebounce(searchCategory, 300);

  // Real-time search effect
  const fetchOriginalCourses = React.useCallback(async () => {
    try {
      setLoading(true);
      setError(null);
  
      let fetchedCourses: CourseSummaryResponse[];
      if (user) {
        // User is logged in, fetch all courses
        fetchedCourses = await courseService.getAllCourses();
      } else {
        // User is not logged in, fetch public courses only
        fetchedCourses = await courseService.getPublicCourses();
      }
  
      setCourses(fetchedCourses);
    } catch (err: any) {
      console.error('Error fetching courses:', err);
      setError(err.message || 'Failed to fetch courses');
    } finally {
      setLoading(false);
    }
  }, [user]);
  
  // 2. Real-time search effect, now with fetchOriginalCourses as a dependency
  useEffect(() => {
    const performSearch = async () => {
      // Only search if any search criteria are provided
      const hasSearchCriteria = debouncedSearchTitle || 
                               debouncedSearchInstructor || 
                               searchLevel !== 'all' || 
                               searchLanguage !== 'all' || 
                               debouncedSearchSkill || 
                               debouncedSearchCategory;
  
      if (!hasSearchCriteria) {
        // If no search criteria, fetch original courses
        await fetchOriginalCourses();
        return;
      }
  
      try {
        setIsSearching(true);
        setError(null);
  
        const searchResults = await courseService.searchCourses({
          instructor: debouncedSearchInstructor || undefined,
          level: searchLevel === 'all' ? undefined : searchLevel,
          language: searchLanguage === 'all' ? undefined : searchLanguage,
          skill: debouncedSearchSkill || undefined,
          category: debouncedSearchCategory || undefined,
          title: debouncedSearchTitle || undefined,
        });
  
        setCourses(searchResults);
      } catch (err: any) {
        console.error('Error searching courses:', err);
        setError(err.message || 'Failed to search courses');
      } finally {
        setIsSearching(false);
      }
    };
  
    performSearch();
  }, [
    debouncedSearchTitle, debouncedSearchInstructor, searchLevel, searchLanguage, 
    debouncedSearchSkill, debouncedSearchCategory, user, fetchOriginalCourses
  ]);
  
  // 3. Initial fetch effect (no change needed, as fetchOriginalCourses is stable)
  useEffect(() => {
    if (!authLoading) {
      fetchOriginalCourses();
    }
  }, [user, authLoading, fetchOriginalCourses]);

  // Extract unique categories from courses
  const allCategories = courses.flatMap(course => course.categories);
  const uniqueCategories = Array.from(new Set(allCategories));
  const categories = ['All', ...uniqueCategories];

  const filteredCourses =
    activeCategory === 'All'
      ? courses
      : courses.filter((course) => 
          course.categories.includes(activeCategory)
        );

  const handleCategoryClick = (category: string) => {
    setActiveCategory(category);
  };

  const handleClearSearch = () => {
    // Reset search fields
    setSearchTitle('');
    setSearchInstructor('');
    setSearchLevel('all');
    setSearchLanguage('all');
    setSearchSkill('');
    setSearchCategory('');
  };

  const handleBookmark = async (courseId: string, event: React.MouseEvent) => {
    event.stopPropagation(); // Prevent card click
    if (!user?.id) return;
    
    setBookmarking(courseId);
    try {
      // Check if course is already bookmarked
      const isBookmarked = user.bookmarkedCourseIds?.includes(courseId);
      
      if (isBookmarked) {
        await courseService.unbookmarkCourse(courseId, user.id);
        updateUserBookmarks(courseId, false);
        toast({
          title: 'Bookmark Removed',
          description: 'Course removed from your bookmarks.',
          variant: 'default',
        });
      } else {
        await courseService.bookmarkCourse(courseId, user.id);
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
        description: err.message || 'Could not bookmark course.',
        variant: 'destructive',
      });
    } finally {
      setBookmarking(null);
    }
  };

  const isCourseBookmarked = (courseId: string) => {
    return user?.bookmarkedCourseIds?.includes(courseId) || false;
  };

  // Show loading state
  if (authLoading || loading) {
    return (
      <div className="min-h-screen bg-gray-50">
        <Navbar />
        <div className="flex h-96 items-center justify-center">
          <div className="flex items-center space-x-2">
            <Loader2 className="h-6 w-6 animate-spin" />
            <span>Loading courses...</span>
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
            <Button onClick={() => window.location.reload()}>Try Again</Button>
          </div>
        </div>
      </div>
    );
  }

  return (
    <div className="min-h-screen bg-gray-50">
      <Navbar />

      <div className="mx-auto max-w-7xl px-4 py-8 sm:px-6 lg:px-8">
        {/* Header Section */}
        <div className="mb-12 text-center">
          <h1 className="mb-4 text-4xl font-bold text-gray-900">
            Explore Our <span className="text-blue-600">Courses</span>
          </h1>
          <p className="mx-auto max-w-3xl text-xl text-gray-600">
            {user 
              ? "Discover all available courses designed to help you master new skills and advance your career."
              : "Discover public courses designed to help you master new skills. Sign in to access all courses."
            }
          </p>
        </div>

        {/* Search Section */}
        <div className="mb-8 bg-white rounded-lg shadow-sm border p-6">
          <h3 className="text-lg font-semibold text-gray-900 mb-4 flex items-center">
            <Search className="mr-2 h-5 w-5" />
            Search Courses
          </h3>
          <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-4 mb-4">
            <div>
              <label className="block text-sm font-medium text-gray-700 mb-1">Title</label>
              <Input
                placeholder="Search by title..."
                value={searchTitle}
                onChange={(e) => setSearchTitle(e.target.value)}
              />
            </div>
            <div>
              <label className="block text-sm font-medium text-gray-700 mb-1">Instructor</label>
              <Input
                placeholder="Search by instructor..."
                value={searchInstructor}
                onChange={(e) => setSearchInstructor(e.target.value)}
              />
            </div>
            <div>
              <label className="block text-sm font-medium text-gray-700 mb-1">Level</label>
              <Select value={searchLevel} onValueChange={(value) => setSearchLevel(value as Level | 'all')}>
                <SelectTrigger>
                  <SelectValue placeholder="Select level" />
                </SelectTrigger>
                <SelectContent>
                  <SelectItem value="all">All Levels</SelectItem>
                  <SelectItem value="BEGINNER">Beginner</SelectItem>
                  <SelectItem value="INTERMEDIATE">Intermediate</SelectItem>
                  <SelectItem value="ADVANCED">Advanced</SelectItem>
                </SelectContent>
              </Select>
            </div>
            <div>
              <label className="block text-sm font-medium text-gray-700 mb-1">Language</label>
              <Select value={searchLanguage} onValueChange={(value) => setSearchLanguage(value as Language | 'all')}>
                <SelectTrigger>
                  <SelectValue placeholder="Select language" />
                </SelectTrigger>
                <SelectContent>
                  <SelectItem value="all">All Languages</SelectItem>
                  <SelectItem value="EN">English</SelectItem>
                  <SelectItem value="ES">Spanish</SelectItem>
                  <SelectItem value="FR">French</SelectItem>
                  <SelectItem value="DE">German</SelectItem>
                  <SelectItem value="AR">Arabic</SelectItem>
                  <SelectItem value="ZH">Chinese</SelectItem>
                  <SelectItem value="JA">Japanese</SelectItem>
                </SelectContent>
              </Select>
            </div>
            <div>
              <label className="block text-sm font-medium text-gray-700 mb-1">Skill</label>
              <Input
                placeholder="Search by skill..."
                value={searchSkill}
                onChange={(e) => setSearchSkill(e.target.value)}
              />
            </div>
            <div>
              <label className="block text-sm font-medium text-gray-700 mb-1">Category</label>
              <Input
                placeholder="Search by category..."
                value={searchCategory}
                onChange={(e) => setSearchCategory(e.target.value)}
              />
            </div>
          </div>
          <div className="flex gap-3">
            <Button 
              variant="outline" 
              onClick={handleClearSearch}
              disabled={isSearching}
            >
              <X className="mr-2 h-4 w-4" />
              Clear
            </Button>
            {isSearching && (
              <div className="flex items-center text-sm text-gray-600">
                <Loader2 className="mr-2 h-4 w-4 animate-spin" />
                Searching...
              </div>
            )}
          </div>
        </div>

        {/* Filter Categories */}
        {categories.length > 1 && (
          <div className="mb-8 flex flex-wrap justify-center gap-4">
            {categories.map((category) => (
              <Button
                key={category}
                variant={category === activeCategory ? 'default' : 'outline'}
                size="sm"
                className="rounded-full"
                onClick={() => handleCategoryClick(category)}
              >
                {category}
              </Button>
            ))}
          </div>
        )}

        {/* Courses Grid */}
        <div className="grid gap-8 md:grid-cols-2 lg:grid-cols-3">
          {filteredCourses.map((course) => (
            <Card
            key={course.id}
            className="group cursor-pointer transition-all duration-300 hover:-translate-y-1 hover:shadow-lg h-full flex flex-col"
            onClick={() => navigate(`/courses/${course.id}`)}
          >
            <div className="relative aspect-video overflow-hidden rounded-t-lg bg-gradient-to-br from-blue-100 to-purple-100 shrink-0">
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
          
              {/* Bookmark Icon Overlay */}
              {user && (
                <button
                  onClick={(event) => handleBookmark(course.id, event)}
                  disabled={bookmarking === course.id}
                  aria-label={
                    bookmarking === course.id
                      ? 'Bookmarking course'
                      : isCourseBookmarked(course.id)
                      ? 'Remove bookmark from course'
                      : 'Bookmark course'
                  }
                  className={`absolute right-3 top-3 z-10 flex h-8 w-8 items-center justify-center rounded-full bg-white/90 backdrop-blur-sm transition-all duration-200 hover:bg-white hover:shadow-md ${
                    isCourseBookmarked(course.id)
                      ? 'text-blue-600 shadow-md'
                      : 'text-gray-600 hover:text-blue-600'
                  }`}
                >
                  {bookmarking === course.id ? (
                    <Loader2 className="h-4 w-4 animate-spin" />
                  ) : isCourseBookmarked(course.id) ? (
                    <BookmarkCheck className="h-4 w-4 fill-current" />
                  ) : (
                    <Bookmark className="h-4 w-4" />
                  )}
                </button>
              )}
          
              <div className="absolute left-4 top-4">
                <Badge className="bg-white/90 text-gray-800 hover:bg-white">
                  {course.categories[0] || 'General'}
                </Badge>
              </div>
              <div className="absolute left-4 bottom-4">
                <Badge variant="secondary" className="bg-white/90 text-gray-800">
                  {course.level}
                </Badge>
              </div>
            </div>
          
            <div className="flex flex-col flex-1 h-full">
              <CardHeader className="pb-0">
                <CardTitle className="text-lg transition-colors group-hover:text-blue-600 cursor-pointer">
                  {course.title}
                </CardTitle>
                <CardDescription className="text-sm line-clamp-2 min-h-[44px]">
                  {course.description}
                </CardDescription>
              </CardHeader>
              <CardContent className="flex flex-col flex-1 justify-between h-full pt-0">
                {/* Instructor always at the same spot */}
                <div className="mb-1 text-sm text-gray-600 flex items-center gap-1 min-h-[24px]">
                  <span>by</span>
                  <span className="font-semibold text-gray-800">{course.instructor}</span>
                </div>
          
                {/* Stats row */}
                <div className="flex items-center justify-between text-sm text-gray-600 mb-2">
                <div className="flex items-center space-x-1">
                  <Brain className="h-4 w-4" />
                  <span>{course.skills.length} skills</span>
                </div>
                <div className="flex items-center space-x-1">
                  <Users className="h-4 w-4" />
                  <span>{course.numberOfEnrolledUsers.toLocaleString()}</span>
                </div>
                <div className="flex items-center space-x-1">
                  <Star className="h-4 w-4 fill-yellow-400 text-yellow-400" />
                  <span>{course.rating}</span>
                </div>
              </div>
          
                {/* Spacer for flex alignment */}
                <div className="flex-1" />
          
                {/* Bottom Action Row */}
                <div className="flex items-center justify-between pt-4 mt-auto">
                  <span className="text-2xl font-bold text-green-600">Free</span>
                  <Button
                    size="sm"
                    className="bg-blue-600 hover:bg-blue-700"
                    onClick={(e) => {
                      e.stopPropagation();
                      navigate(`/courses/${course.id}`);
                    }}
                  >
                    <BookOpen className="mr-2 h-4 w-4" />
                    View Course
                  </Button>
                </div>
              </CardContent>
            </div>
          </Card>
          
          ))}
        </div>

        {/* Empty state */}
        {filteredCourses.length === 0 && (
          <div className="text-center py-12">
            <BookOpen className="mx-auto h-12 w-12 text-gray-400 mb-4" />
            <h3 className="text-lg font-medium text-gray-900 mb-2">No courses found</h3>
            <p className="text-gray-600">
              {activeCategory === 'All' 
                ? 'No courses are available at the moment.'
                : `No courses found in the "${activeCategory}" category.`
              }
            </p>
          </div>
        )}
      </div>
    </div>
  );
};

export default Courses;
