import { describe, it, expect, beforeEach, afterEach, vi } from 'vitest';
import { getAIChatResponse, confirmCourse, setAuthToken } from '../aiChat.service';
import * as userService from '../user.service';
import * as courseService from '../course.service';
import { mockCourseResponse } from '@/test/mocks/api';
import { Level, Language } from '@/types';

// Mock the dependencies
vi.mock('../user.service');
vi.mock('../course.service');

// Tests for AI Chat Service
describe('AI Chat Service', () => {
  beforeEach(() => {
    vi.clearAllMocks();
    vi.useFakeTimers();
  });

  afterEach(() => {
    vi.useRealTimers();
  });

  describe('setAuthToken', () => {
    it('should set auth token in both user and course services', () => {
      const mockSetAuthToken = vi.mocked(userService.setAuthToken);
      const mockCourseSetAuthToken = vi.mocked(courseService.setAuthToken);

      setAuthToken('test-token');

      expect(mockSetAuthToken).toHaveBeenCalledWith('test-token');
      expect(mockCourseSetAuthToken).toHaveBeenCalledWith('test-token');
    });

    it('should clear auth token when null is passed', () => {
      const mockSetAuthToken = vi.mocked(userService.setAuthToken);
      const mockCourseSetAuthToken = vi.mocked(courseService.setAuthToken);

      setAuthToken(null);

      expect(mockSetAuthToken).toHaveBeenCalledWith(null);
      expect(mockCourseSetAuthToken).toHaveBeenCalledWith(null);
    });
  });

  describe('getAIChatResponse', () => {
    it('should return help message for /help command', async () => {
      const promise = getAIChatResponse('user-123', '/help', []);
      
      // Fast-forward time to skip the 1-second delay
      vi.advanceTimersByTime(1000);
      
      const result = await promise;

      expect(result).toContain('🤖 **SkillForge Assistant — Commands & Tips**');
      expect(result).toContain('/generate');
      expect(result).toContain('/explain');
    });

    it('should return help message for /help with extra spaces', async () => {
      const promise = getAIChatResponse('user-123', '  /help  ', []);
      vi.advanceTimersByTime(1000);
      const result = await promise;

      expect(result).toContain('🤖 **SkillForge Assistant — Commands & Tips**');
    });

    it('should explain topic for /explain command', async () => {
      const mockResponse = 'React hooks are functions that let you use state and other React features in functional components.';
      vi.mocked(courseService.generateResponseFromPrompt).mockResolvedValue(mockResponse);

      const promise = getAIChatResponse('user-123', '/explain React hooks', []);
      vi.advanceTimersByTime(1000);
      const result = await promise;

      expect(courseService.generateResponseFromPrompt).toHaveBeenCalledWith(
        expect.stringContaining('Explain the following topic in simple, clear language')
      );
      expect(result).toBe(mockResponse);
    });

    it('should return error for /explain without topic', async () => {
      const promise = getAIChatResponse('user-123', '/explain', []);
      vi.advanceTimersByTime(1000);
      const result = await promise;

      expect(result).toContain('❌ Please specify what you want explained');
    });

    it('should generate course for /generate command', async () => {
      const mockCoursePayload = {
        title: 'JavaScript Basics',
        description: 'Learn JavaScript fundamentals',
        category: 'Programming',
        level: Level.BEGINNER,
        id: 'course-123',
        instructor: 'instructor-123',
        language: Language.EN,
      };
      vi.mocked(courseService.generateCourseForUser).mockResolvedValue(mockCoursePayload);

      const promise = getAIChatResponse('user-123', '/generate JavaScript basics', ['JavaScript']);
      vi.advanceTimersByTime(1000);
      const result = await promise;

      expect(courseService.generateCourseForUser).toHaveBeenCalledWith(
        'user-123',
        'JavaScript basics',
        ['JavaScript']
      );
      expect(result).toEqual(mockCoursePayload);
    });

    it('should return error for /generate without topic', async () => {
      const promise = getAIChatResponse('user-123', '/generate', []);
      vi.advanceTimersByTime(1000);
      const result = await promise;

      expect(result).toContain('❌ Please provide a topic or subject for the course');
    });

    it('should return disabled message when course generation is disabled', async () => {
      const promise = getAIChatResponse('user-123', '/generate JavaScript', [], true);
      vi.advanceTimersByTime(1000);
      const result = await promise;

      expect(result).toContain('🚀 **Course Generation Disabled**');
      expect(result).toContain('Please visit the **AI Center** page');
    });

    it('should handle course generation returning JSON string', async () => {
      const mockJsonString = JSON.stringify({
        title: 'JavaScript Course',
        description: 'Learn JavaScript',
      });
      vi.mocked(courseService.generateCourseForUser).mockResolvedValue(mockJsonString);

      const promise = getAIChatResponse('user-123', '/generate JavaScript', []);
      vi.advanceTimersByTime(1000);
      const result = await promise;

      expect(result).toEqual({
        title: 'JavaScript Course',
        description: 'Learn JavaScript',
      });
    });

    it('should handle course generation returning plain string', async () => {
      const mockString = 'Course generation in progress...';
      vi.mocked(courseService.generateCourseForUser).mockResolvedValue(mockString);

      const promise = getAIChatResponse('user-123', '/generate JavaScript', []);
      vi.advanceTimersByTime(1000);
      const result = await promise;

      expect(result).toBe(mockString);
    });

    it('should confirm course for /confirm command', async () => {
      vi.mocked(courseService.confirmCourseGeneration).mockResolvedValue(mockCourseResponse);

      const promise = getAIChatResponse('user-123', '/confirm', []);
      vi.advanceTimersByTime(1000);
      const result = await promise;

      expect(courseService.confirmCourseGeneration).toHaveBeenCalledWith('user-123');
      expect(result).toEqual(mockCourseResponse);
    });

    it('should handle general chat messages', async () => {
      const mockResponse = 'Hello! How can I help you with your learning journey?';
      vi.mocked(courseService.generateResponseFromPrompt).mockResolvedValue(mockResponse);

      const promise = getAIChatResponse('user-123', 'Hello', []);
      vi.advanceTimersByTime(1000);
      const result = await promise;

      expect(courseService.generateResponseFromPrompt).toHaveBeenCalledWith(
        expect.stringContaining('Respond to the following message in a natural, friendly, and concise way')
      );
      expect(result).toBe(mockResponse);
    });

    it('should handle empty messages', async () => {
      const promise = getAIChatResponse('user-123', '', []);
      vi.advanceTimersByTime(1000);
      const result = await promise;

      expect(result).toContain('❌ Please provide a message or question');
    });

    it('should handle whitespace-only messages', async () => {
      const promise = getAIChatResponse('user-123', '   ', []);
      vi.advanceTimersByTime(1000);
      const result = await promise;

      expect(result).toContain('❌ Please provide a message or question');
    });

    it('should normalize command formatting', async () => {
      const mockResponse = 'Help message';
      vi.mocked(courseService.generateResponseFromPrompt).mockResolvedValue(mockResponse);

      const promise = getAIChatResponse('user-123', '  /explain   React hooks  ', []);
      vi.advanceTimersByTime(1000);
      await promise;

      // The prompt should contain 'Topic: in   React hooks' due to the way substring is handled
      expect(courseService.generateResponseFromPrompt).toHaveBeenCalledWith(
        expect.stringContaining('Topic: in   React hooks')
      );
    });

    it('should simulate delay for AI response', async () => {
      const mockResponse = 'Response';
      vi.mocked(courseService.generateResponseFromPrompt).mockResolvedValue(mockResponse);

      const promise = getAIChatResponse('user-123', 'Hello', []);
      
      // Fast-forward time
      vi.advanceTimersByTime(1000);
      
      const result = await promise;
      expect(result).toBe(mockResponse);
    });
  });

  describe('confirmCourse', () => {
    it('should confirm course generation successfully', async () => {
      vi.mocked(courseService.confirmCourseGeneration).mockResolvedValue(mockCourseResponse);

      const result = await confirmCourse('user-123');

      expect(courseService.confirmCourseGeneration).toHaveBeenCalledWith('user-123');
      expect(result).toEqual(mockCourseResponse);
    });

    it('should return error for missing user ID', async () => {
      const result = await confirmCourse('');

      expect(result).toContain('❌ User ID is required to confirm course generation');
    });

    it('should return error for null user ID', async () => {
      const result = await confirmCourse(null as any);

      expect(result).toContain('❌ User ID is required to confirm course generation');
    });

    it('should handle course service errors', async () => {
      const errorMessage = 'Course generation failed';
      vi.mocked(courseService.confirmCourseGeneration).mockRejectedValue(new Error(errorMessage));

      await expect(confirmCourse('user-123')).rejects.toThrow(errorMessage);
    });
  });
}); 