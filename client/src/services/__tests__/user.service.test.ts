import { describe, it, expect, beforeEach, vi } from 'vitest';
import {
  setAuthToken,
  register,
  login,
  getUserProfile,
  updateUserProfile,
  deleteUserAccount,
  getUserSkillsInProgress,
  getUserEnrolledCourseIds,
  getUserCompletedCourseIds,
  getUserSkills,
  getUserBookmarkedCourseIds,
} from '../user.service';
import {
  mockUserLoginResponse,
  mockUserRegisterResponse,
  mockUserProfileResponse,
  mockApiError,
  mockUnauthorizedError,
} from '@/test/mocks/api';

// Tests for User Service
describe('User Service', () => {
  beforeEach(() => {
    vi.clearAllMocks();
    setAuthToken(null);
  });

  describe('setAuthToken', () => {
    it('should set auth token', () => {
      setAuthToken('test-token');
      // Note: We can't directly test the internal state, but we can test it indirectly
      // through functions that use it
    });

    it('should clear auth token when null is passed', () => {
      setAuthToken('test-token');
      setAuthToken(null);
      // The token should be cleared
    });
  });

  describe('register', () => {
    it('should register a user successfully', async () => {
      const mockFetch = vi.mocked(fetch);
      mockFetch.mockResolvedValueOnce({
        ok: true,
        json: async () => mockUserRegisterResponse,
      } as Response);

      const payload = {
        username: 'newuser',
        email: 'new@example.com',
        password: 'password123',
        firstName: 'New',
        lastName: 'User',
      };

      const result = await register(payload);

      expect(mockFetch).toHaveBeenCalledWith(
        expect.stringContaining('/users/register'),
        expect.objectContaining({
          method: 'POST',
          headers: { 'Content-Type': 'application/json' },
          body: JSON.stringify(payload),
        })
      );
      expect(result).toEqual(mockUserRegisterResponse);
    });

    it('should throw error on registration failure', async () => {
      const mockFetch = vi.mocked(fetch);
      mockFetch.mockResolvedValueOnce({
        ok: false,
        status: 400,
        json: async () => mockApiError,
      } as Response);

      const payload = {
        username: 'newuser',
        email: 'new@example.com',
        password: 'password123',
        firstName: 'New',
        lastName: 'User',
      };

      await expect(register(payload)).rejects.toThrow();
    });
  });

  describe('login', () => {
    it('should login a user successfully', async () => {
      const mockFetch = vi.mocked(fetch);
      mockFetch.mockResolvedValueOnce({
        ok: true,
        json: async () => mockUserLoginResponse,
      } as Response);

      const payload = {
        username: 'testuser',
        email: 'test@example.com',
        password: 'password123',
      };

      const result = await login(payload);

      expect(mockFetch).toHaveBeenCalledWith(
        expect.stringContaining('/users/login'),
        expect.objectContaining({
          method: 'POST',
          headers: { 'Content-Type': 'application/json' },
          body: JSON.stringify(payload),
        })
      );
      expect(result).toEqual(mockUserLoginResponse);
    });

    it('should throw error on login failure', async () => {
      const mockFetch = vi.mocked(fetch);
      mockFetch.mockResolvedValueOnce({
        ok: false,
        status: 401,
        json: async () => mockUnauthorizedError,
      } as Response);

      const payload = {
        username: 'testuser',
        email: 'test@example.com',
        password: 'wrongpassword',
      };

      await expect(login(payload)).rejects.toThrow();
    });
  });

  describe('getUserProfile', () => {
    it('should get user profile successfully', async () => {
      setAuthToken('test-token');
      const mockFetch = vi.mocked(fetch);
      mockFetch.mockResolvedValueOnce({
        ok: true,
        json: async () => mockUserProfileResponse,
      } as Response);

      const result = await getUserProfile('user-123');

      expect(mockFetch).toHaveBeenCalledWith(
        expect.stringContaining('/users/user-123/profile'),
        expect.objectContaining({
          method: 'GET',
          headers: {
            Authorization: 'Bearer test-token',
          },
        })
      );
      expect(result).toEqual(mockUserProfileResponse);
    });

    it('should throw error when no auth token is set', async () => {
      await expect(getUserProfile('user-123')).rejects.toEqual({
        status: 401,
        message: 'No authentication token provided',
      });
    });

    it('should throw error on API failure', async () => {
      setAuthToken('test-token');
      const mockFetch = vi.mocked(fetch);
      mockFetch.mockResolvedValueOnce({
        ok: false,
        status: 404,
        json: async () => mockApiError,
      } as Response);

      await expect(getUserProfile('user-123')).rejects.toThrow();
    });
  });

  describe('updateUserProfile', () => {
    it('should update user profile successfully', async () => {
      setAuthToken('test-token');
      const mockFetch = vi.mocked(fetch);
      mockFetch.mockResolvedValueOnce({
        ok: true,
        json: async () => mockUserProfileResponse,
      } as Response);

      const payload = {
        bio: 'Updated bio',
        interests: ['Updated Interest'],
      };

      const result = await updateUserProfile('user-123', payload);

      expect(mockFetch).toHaveBeenCalledWith(
        expect.stringContaining('/users/user-123/profile'),
        expect.objectContaining({
          method: 'PUT',
          headers: {
            'Content-Type': 'application/json',
            Authorization: 'Bearer test-token',
          },
          body: JSON.stringify(payload),
        })
      );
      expect(result).toEqual(mockUserProfileResponse);
    });

    it('should throw error when no auth token is set', async () => {
      const payload = { bio: 'Updated bio' };
      await expect(updateUserProfile('user-123', payload)).rejects.toEqual({
        status: 401,
        message: 'No authentication token provided',
      });
    });
  });

  describe('deleteUserAccount', () => {
    it('should delete user account successfully', async () => {
      setAuthToken('test-token');
      const mockFetch = vi.mocked(fetch);
      mockFetch.mockResolvedValueOnce({
        ok: true,
        status: 204,
      } as Response);

      await deleteUserAccount('user-123');

      expect(mockFetch).toHaveBeenCalledWith(
        expect.stringContaining('/users/user-123/profile'),
        expect.objectContaining({
          method: 'DELETE',
          headers: {
            Authorization: 'Bearer test-token',
          },
        })
      );
    });

    it('should throw error when no auth token is set', async () => {
      await expect(deleteUserAccount('user-123')).rejects.toEqual({
        status: 401,
        message: 'No authentication token provided',
      });
    });

    it('should throw error on unexpected response status', async () => {
      setAuthToken('test-token');
      const mockFetch = vi.mocked(fetch);
      mockFetch.mockResolvedValueOnce({
        ok: true,
        status: 200, // Should be 204
      } as Response);

      await expect(deleteUserAccount('user-123')).rejects.toEqual({
        status: 200,
        message: 'Unexpected response from server',
      });
    });
  });

  describe('getUserSkillsInProgress', () => {
    it('should get user skills in progress successfully', async () => {
      setAuthToken('test-token');
      const mockFetch = vi.mocked(fetch);
      const mockSkills = ['JavaScript', 'React'];
      mockFetch.mockResolvedValueOnce({
        ok: true,
        json: async () => mockSkills,
      } as Response);

      const result = await getUserSkillsInProgress('user-123');

      expect(mockFetch).toHaveBeenCalledWith(
        expect.stringContaining('/users/user-123/skills-in-progress'),
        expect.objectContaining({
          method: 'GET',
          headers: {
            'Content-Type': 'application/json',
            Authorization: 'Bearer test-token',
          },
        })
      );
      expect(result).toEqual(mockSkills);
    });

    it('should throw error when no auth token is set', async () => {
      await expect(getUserSkillsInProgress('user-123')).rejects.toEqual({
        status: 401,
        message: 'No authentication token provided',
      });
    });
  });

  describe('getUserEnrolledCourseIds', () => {
    it('should get user enrolled course IDs successfully', async () => {
      setAuthToken('test-token');
      const mockFetch = vi.mocked(fetch);
      const mockCourseIds = ['course-1', 'course-2'];
      mockFetch.mockResolvedValueOnce({
        ok: true,
        json: async () => mockCourseIds,
      } as Response);

      const result = await getUserEnrolledCourseIds('user-123');

      expect(mockFetch).toHaveBeenCalledWith(
        expect.stringContaining('/users/user-123/courses/enrolled'),
        expect.objectContaining({
          method: 'GET',
          headers: {
            'Content-Type': 'application/json',
            Authorization: 'Bearer test-token',
          },
        })
      );
      expect(result).toEqual(mockCourseIds);
    });

    it('should throw error when no auth token is set', async () => {
      await expect(getUserEnrolledCourseIds('user-123')).rejects.toEqual({
        status: 401,
        message: 'No authentication token provided',
      });
    });
  });

  describe('getUserCompletedCourseIds', () => {
    it('should get user completed course IDs successfully', async () => {
      setAuthToken('test-token');
      const mockFetch = vi.mocked(fetch);
      const mockCourseIds = ['course-1', 'course-2'];
      mockFetch.mockResolvedValueOnce({
        ok: true,
        json: async () => mockCourseIds,
      } as Response);

      const result = await getUserCompletedCourseIds('user-123');

      expect(mockFetch).toHaveBeenCalledWith(
        expect.stringContaining('/users/user-123/courses/completed'),
        expect.objectContaining({
          method: 'GET',
          headers: {
            'Content-Type': 'application/json',
            Authorization: 'Bearer test-token',
          },
        })
      );
      expect(result).toEqual(mockCourseIds);
    });

    it('should throw error when no auth token is set', async () => {
      await expect(getUserCompletedCourseIds('user-123')).rejects.toEqual({
        status: 401,
        message: 'No authentication token provided',
      });
    });
  });

  describe('getUserSkills', () => {
    it('should get user skills successfully', async () => {
      setAuthToken('test-token');
      const mockFetch = vi.mocked(fetch);
      const mockSkills = ['JavaScript', 'React', 'TypeScript'];
      mockFetch.mockResolvedValueOnce({
        ok: true,
        json: async () => mockSkills,
      } as Response);

      const result = await getUserSkills('user-123');

      expect(mockFetch).toHaveBeenCalledWith(
        expect.stringContaining('/users/user-123/skills'),
        expect.objectContaining({
          method: 'GET',
          headers: {
            'Content-Type': 'application/json',
            Authorization: 'Bearer test-token',
          },
        })
      );
      expect(result).toEqual(mockSkills);
    });

    it('should throw error when no auth token is set', async () => {
      await expect(getUserSkills('user-123')).rejects.toEqual({
        status: 401,
        message: 'No authentication token provided',
      });
    });
  });

  describe('getUserBookmarkedCourseIds', () => {
    it('should get user bookmarked course IDs successfully', async () => {
      setAuthToken('test-token');
      const mockFetch = vi.mocked(fetch);
      const mockCourseIds = ['course-1', 'course-2'];
      mockFetch.mockResolvedValueOnce({
        ok: true,
        json: async () => mockCourseIds,
      } as Response);

      const result = await getUserBookmarkedCourseIds('user-123');

      expect(mockFetch).toHaveBeenCalledWith(
        expect.stringContaining('/users/user-123/courses/bookmarked'),
        expect.objectContaining({
          method: 'GET',
          headers: {
            'Content-Type': 'application/json',
            Authorization: 'Bearer test-token',
          },
        })
      );
      expect(result).toEqual(mockCourseIds);
    });

    it('should throw error when no auth token is set', async () => {
      await expect(getUserBookmarkedCourseIds('user-123')).rejects.toEqual({
        status: 401,
        message: 'No authentication token provided',
      });
    });
  });
}); 