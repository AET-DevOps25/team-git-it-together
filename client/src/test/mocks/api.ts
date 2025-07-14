import type {
  UserLoginResponse,
  UserProfileResponse,
  UserRegisterResponse,
  CourseResponse,
  CourseSummaryResponse,
  CourseProgressResponse,
  UserAchievement,
} from '@/types';
import { Level, Language, AchievementCategory } from '@/types';

// Mock user responses
export const mockUserLoginResponse: UserLoginResponse = {
  id: 'user-123',
  username: 'testuser',
  email: 'test@example.com',
  firstName: 'Test',
  lastName: 'User',
  profilePictureUrl: 'https://example.com/avatar.jpg',
  jwtToken: 'mock-jwt-token',
};

export const mockUserRegisterResponse: UserRegisterResponse = {
  id: 'user-123',
  username: 'newuser',
  email: 'new@example.com',
  firstName: 'New',
  lastName: 'User',
};

export const mockUserProfileResponse: UserProfileResponse = {
  id: 'user-123',
  username: 'testuser',
  email: 'test@example.com',
  firstName: 'Test',
  lastName: 'User',
  profilePictureUrl: 'https://example.com/avatar.jpg',
  bio: 'Test user bio',
  interests: ['Programming', 'Technology'],
  skills: ['JavaScript', 'React', 'TypeScript'],
  skillsInProgress: ['Python', 'Node.js'],
  enrolledCourses: [],
  bookmarkedCourses: [],
  completedCourses: [],
};

// Mock course responses
export const mockCourseResponse: CourseResponse = {
  id: 'course-123',
  title: 'Test Course',
  description: 'A test course for testing',
  instructor: 'instructor-123',
  skills: ['JavaScript', 'React'],
  modules: [],
  numberOfEnrolledUsers: 10,
  categories: ['Programming'],
  level: Level.BEGINNER,
  thumbnailUrl: 'https://example.com/thumbnail.jpg',
  published: true,
  isPublic: true,
  language: Language.EN,
  rating: 4.5,
  enrolledUsers: [],
};

export const mockCourseSummaryResponse: CourseSummaryResponse = {
  id: 'course-123',
  title: 'Test Course',
  description: 'A test course for testing',
  instructor: 'instructor-123',
  skills: ['JavaScript', 'React'],
  thumbnailUrl: 'https://example.com/thumbnail.jpg',
  numberOfEnrolledUsers: 10,
  categories: ['Programming'],
  level: Level.BEGINNER,
  isPublic: true,
  published: true,
  language: Language.EN,
  rating: 4.5,
  enrolledUsers: [],
};

export const mockCourseProgressResponse: CourseProgressResponse = {
  courseId: 'course-123',
  userId: 'user-123',
  progress: 75,
  enrolledAt: '2024-01-01T00:00:00Z',
  lastAccessedAt: '2024-01-01T00:00:00Z',
  completed: false,
  completedAt: undefined,
};

// Mock achievement responses
export const mockUserAchievement: UserAchievement = {
  id: 'first-course-completed',
  title: 'First Course Completed',
  description: 'Completed your first course',
  date: '2024-01-01',
  icon: 'Trophy',
  category: AchievementCategory.COURSE_COMPLETION,
};

// Mock error responses
export const mockApiError = {
  status: 400,
  message: 'Bad Request',
  timestamp: '2024-01-01T00:00:00Z',
  path: '/api/test',
};

export const mockUnauthorizedError = {
  status: 401,
  message: 'Unauthorized',
  timestamp: '2024-01-01T00:00:00Z',
  path: '/api/test',
};

export const mockNotFoundError = {
  status: 404,
  message: 'Not Found',
  timestamp: '2024-01-01T00:00:00Z',
  path: '/api/test',
};

export const mockServerError = {
  status: 500,
  message: 'Internal Server Error',
  timestamp: '2024-01-01T00:00:00Z',
  path: '/api/test',
}; 