// Tests for Course Service

import { describe, it, expect, beforeEach, vi } from 'vitest';
import {
  getAllCourses,
  getCourse,
  getPublicCourses,
  searchCourses,
  enrollInCourse,
  unenrollFromCourse,
  bookmarkCourse,
  unbookmarkCourse,
  getUserEnrolledCourses,
  generateResponseFromPrompt,
  setAuthToken,
} from '../course.service';
import {
  mockCourseResponse,
  mockCourseSummaryResponse,
  mockApiError,
} from '@/test/mocks/api';

// Tests for Course Service
describe('Course Service', () => {
  beforeEach(() => {
    vi.clearAllMocks();
  });

  describe('getAllCourses', () => {
    it('should get all courses successfully', async () => {
      setAuthToken('test-token');
      const mockFetch = vi.mocked(fetch);
      const mockCourses = [mockCourseSummaryResponse];
      mockFetch.mockResolvedValueOnce({
        ok: true,
        json: async () => mockCourses,
      } as Response);

      const result = await getAllCourses();

      expect(mockFetch).toHaveBeenCalledWith(
        expect.stringContaining('/courses'),
        expect.objectContaining({
          method: 'GET',
          headers: {
            'Content-Type': 'application/json',
            Authorization: 'Bearer test-token',
          },
        })
      );
      expect(result).toEqual(mockCourses);
    });

    it('should throw error on API failure', async () => {
      const mockFetch = vi.mocked(fetch);
      mockFetch.mockResolvedValueOnce({
        ok: false,
        status: 500,
        json: async () => mockApiError,
      } as Response);

      await expect(getAllCourses()).rejects.toThrow();
    });
  });

  describe('getCourse', () => {
    it('should get course by ID successfully', async () => {
      setAuthToken('test-token');
      const mockFetch = vi.mocked(fetch);
      mockFetch.mockResolvedValueOnce({
        ok: true,
        json: async () => mockCourseResponse,
      } as Response);

      const result = await getCourse('course-123');

      expect(mockFetch).toHaveBeenCalledWith(
        expect.stringContaining('/courses/course-123'),
        expect.objectContaining({
          method: 'GET',
          headers: {
            'Content-Type': 'application/json',
            Authorization: 'Bearer test-token',
          },
        })
      );
      expect(result).toEqual(mockCourseResponse);
    });

    it('should throw error on API failure', async () => {
      const mockFetch = vi.mocked(fetch);
      mockFetch.mockResolvedValueOnce({
        ok: false,
        status: 404,
        json: async () => mockApiError,
      } as Response);

      await expect(getCourse('course-123')).rejects.toThrow();
    });
  });

  describe('getPublicCourses', () => {
    it('should get public courses successfully', async () => {
      const mockFetch = vi.mocked(fetch);
      const mockCourses = [mockCourseSummaryResponse];
      mockFetch.mockResolvedValueOnce({
        ok: true,
        json: async () => mockCourses,
      } as Response);

      const result = await getPublicCourses();

      expect(mockFetch).toHaveBeenCalledWith(
        expect.stringContaining('/courses/public'),
        expect.objectContaining({
          method: 'GET',
        })
      );
      expect(result).toEqual(mockCourses);
    });
  });

  describe('searchCourses', () => {
    it('should search courses successfully', async () => {
      setAuthToken('test-token');
      const mockFetch = vi.mocked(fetch);
      const mockCourses = [mockCourseSummaryResponse];
      mockFetch.mockResolvedValueOnce({
        ok: true,
        json: async () => mockCourses,
      } as Response);

      const searchParams = {
        title: 'JavaScript',
        category: 'Programming',
      };

      const result = await searchCourses(searchParams);

      expect(mockFetch).toHaveBeenCalledWith(
        expect.stringContaining('/courses/search'),
        expect.objectContaining({
          method: 'GET',
          headers: {
            'Content-Type': 'application/json',
            Authorization: 'Bearer test-token',
          },
        })
      );
      expect(result).toEqual(mockCourses);
    });
  });

  describe('enrollInCourse', () => {
    it('should enroll in course successfully', async () => {
      setAuthToken('test-token');
      const mockFetch = vi.mocked(fetch);
      mockFetch.mockResolvedValueOnce({
        ok: true,
        json: async () => mockCourseResponse,
      } as Response);

      const result = await enrollInCourse('course-123', 'user-123');

      expect(mockFetch).toHaveBeenCalledWith(
        expect.stringContaining('/courses/course-123/enroll/user-123'),
        expect.objectContaining({
          method: 'POST',
          headers: { 
            'Content-Type': 'application/json',
            Authorization: 'Bearer test-token',
          },
        })
      );
      expect(result).toEqual(mockCourseResponse);
    });

    it('should throw error on enrollment failure', async () => {
      const mockFetch = vi.mocked(fetch);
      mockFetch.mockResolvedValueOnce({
        ok: false,
        status: 400,
        json: async () => mockApiError,
      } as Response);

      await expect(enrollInCourse('course-123', 'user-123')).rejects.toThrow();
    });
  });

  describe('unenrollFromCourse', () => {
    it('should unenroll from course successfully', async () => {
      setAuthToken('test-token');
      const mockFetch = vi.mocked(fetch);
      mockFetch.mockResolvedValueOnce({
        ok: true,
        status: 204,
      } as Response);

      await unenrollFromCourse('course-123', 'user-123');

      expect(mockFetch).toHaveBeenCalledWith(
        expect.stringContaining('/courses/course-123/enroll/user-123'),
        expect.objectContaining({
          method: 'DELETE',
          headers: { 
            'Content-Type': 'application/json',
            Authorization: 'Bearer test-token',
          },
        })
      );
    });
  });

  describe('bookmarkCourse', () => {
    it('should bookmark course successfully', async () => {
      setAuthToken('test-token');
      const mockFetch = vi.mocked(fetch);
      mockFetch.mockResolvedValueOnce({
        ok: true,
        json: async () => ({ message: 'Bookmarked successfully' }),
      } as Response);

      const result = await bookmarkCourse('course-123', 'user-123');

      expect(mockFetch).toHaveBeenCalledWith(
        expect.stringContaining('/courses/course-123/bookmark/user-123'),
        expect.objectContaining({
          method: 'POST',
          headers: { 
            'Content-Type': 'application/json',
            Authorization: 'Bearer test-token',
          },
        })
      );
      expect(result).toBeUndefined(); // bookmarkCourse returns void
    });
  });

  describe('unbookmarkCourse', () => {
    it('should unbookmark course successfully', async () => {
      setAuthToken('test-token');
      const mockFetch = vi.mocked(fetch);
      mockFetch.mockResolvedValueOnce({
        ok: true,
        status: 204,
      } as Response);

      await unbookmarkCourse('course-123', 'user-123');

      expect(mockFetch).toHaveBeenCalledWith(
        expect.stringContaining('/courses/course-123/bookmark/user-123'),
        expect.objectContaining({
          method: 'DELETE',
          headers: { 
            'Content-Type': 'application/json',
            Authorization: 'Bearer test-token',
          },
        })
      );
    });
  });

  describe('getUserEnrolledCourses', () => {
    it('should get user enrolled courses successfully', async () => {
      setAuthToken('test-token');
      const mockFetch = vi.mocked(fetch);
      const mockCourses = [mockCourseSummaryResponse];
      mockFetch.mockResolvedValueOnce({
        ok: true,
        json: async () => mockCourses,
      } as Response);

      const result = await getUserEnrolledCourses('user-123');

      expect(mockFetch).toHaveBeenCalledWith(
        expect.stringContaining('/courses/user/user-123/enrolled'),
        expect.objectContaining({
          method: 'GET',
          headers: {
            'Content-Type': 'application/json',
            Authorization: 'Bearer test-token',
          },
        })
      );
      expect(result).toEqual(mockCourses);
    });
  });

  describe('generateResponseFromPrompt', () => {
    it('should generate response from prompt successfully', async () => {
      setAuthToken('test-token');
      const mockFetch = vi.mocked(fetch);
      const mockResponse = 'Generated response';
      mockFetch.mockResolvedValueOnce({
        ok: true,
        text: async () => JSON.stringify({ response: mockResponse }),
      } as Response);

      const result = await generateResponseFromPrompt('Test prompt');

      expect(mockFetch).toHaveBeenCalledWith(
        expect.stringContaining('/api/v1/courses/generate/prompt'),
        expect.objectContaining({
          method: 'POST',
          headers: { 
            'Content-Type': 'application/json',
            Authorization: 'Bearer test-token',
          },
          body: JSON.stringify({ prompt: 'Test prompt' }),
        })
      );
      expect(result).toBe(mockResponse);
    });

    it('should throw error on generation failure', async () => {
      const mockFetch = vi.mocked(fetch);
      mockFetch.mockResolvedValueOnce({
        ok: false,
        status: 500,
        json: async () => mockApiError,
      } as Response);

      await expect(generateResponseFromPrompt('Test prompt')).rejects.toThrow();
    });
  });
}); 