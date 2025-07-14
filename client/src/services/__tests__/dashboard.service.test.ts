import { describe, it, expect, beforeEach, vi } from 'vitest';
import { getDashboardData, setAuthToken } from '../dashboard.service';
import * as userService from '../user.service';
import * as courseService from '../course.service';
import * as achievementService from '../achievement.service';
import { mockUserProfileResponse, mockCourseResponse } from '@/test/mocks/api';
import { AchievementCategory } from '@/types';

// Mock the dependencies
vi.mock('../user.service');
vi.mock('../course.service');
vi.mock('../achievement.service');

// Tests for Dashboard Service
describe('Dashboard Service', () => {
  beforeEach(() => {
    vi.clearAllMocks();
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

  describe('getDashboardData', () => {
    it('should return dashboard data successfully', async () => {
      const mockUserSkills = ['JavaScript', 'React'];
      const mockUserSkillsInProgress = ['Python'];
      const mockEnrolledCourseIds = ['course-1', 'course-2'];
      const mockCompletedCourseIds = ['course-3'];
      const mockBookmarkedCourseIds = ['course-4'];
      const mockEnrolledCourses = [
        {
          ...mockCourseResponse,
          id: 'course-1',
          enrolledUsers: [{ userId: 'user-123', progress: 50, currentLesson: 2, totalNumberOfLessons: 5, skills: ['JavaScript'] }],
        },
        {
          ...mockCourseResponse,
          id: 'course-2',
          enrolledUsers: [{ userId: 'user-123', progress: 30, currentLesson: 1, totalNumberOfLessons: 4, skills: ['React'] }],
        },
      ];
      const mockCompletedCourses = [
        {
          ...mockCourseResponse,
          id: 'course-3',
          enrolledUsers: [{ userId: 'user-123', progress: 100, currentLesson: 5, totalNumberOfLessons: 5, skills: ['TypeScript'] }],
        },
      ];
      const mockBookmarkedCourses = [
        {
          ...mockCourseResponse,
          id: 'course-4',
          enrolledUsers: [],
        },
      ];
      const mockAchievements = [
        {
          id: 'first-course-completed',
          title: 'First Course Completed',
          description: 'Completed your first course',
          date: '2024-01-01',
          icon: 'Trophy',
          category: AchievementCategory.COURSE_COMPLETION,
        },
      ];

      // Mock user service calls
      vi.mocked(userService.getUserSkills).mockResolvedValue(mockUserSkills);
      vi.mocked(userService.getUserSkillsInProgress).mockResolvedValue(mockUserSkillsInProgress);
      vi.mocked(userService.getUserEnrolledCourseIds).mockResolvedValue(mockEnrolledCourseIds);
      vi.mocked(userService.getUserCompletedCourseIds).mockResolvedValue(mockCompletedCourseIds);
      vi.mocked(userService.getUserBookmarkedCourseIds).mockResolvedValue(mockBookmarkedCourseIds);
      vi.mocked(userService.getUserProfile).mockResolvedValue(mockUserProfileResponse);

      // Mock course service calls
      vi.mocked(courseService.getCourse)
        .mockResolvedValueOnce(mockEnrolledCourses[0])
        .mockResolvedValueOnce(mockEnrolledCourses[1])
        .mockResolvedValueOnce(mockCompletedCourses[0])
        .mockResolvedValueOnce(mockBookmarkedCourses[0]);

      // Mock achievement service call
      vi.mocked(achievementService.calculateUserAchievements).mockReturnValue(mockAchievements);

      const result = await getDashboardData('user-123');

      // Verify user service calls
      expect(userService.getUserSkills).toHaveBeenCalledWith('user-123');
      expect(userService.getUserSkillsInProgress).toHaveBeenCalledWith('user-123');
      expect(userService.getUserEnrolledCourseIds).toHaveBeenCalledWith('user-123');
      expect(userService.getUserCompletedCourseIds).toHaveBeenCalledWith('user-123');
      expect(userService.getUserBookmarkedCourseIds).toHaveBeenCalledWith('user-123');
      expect(userService.getUserProfile).toHaveBeenCalledWith('user-123');

      // Verify course service calls
      expect(courseService.getCourse).toHaveBeenCalledTimes(4);
      expect(courseService.getCourse).toHaveBeenCalledWith('course-1');
      expect(courseService.getCourse).toHaveBeenCalledWith('course-2');
      expect(courseService.getCourse).toHaveBeenCalledWith('course-3');
      expect(courseService.getCourse).toHaveBeenCalledWith('course-4');

      // Verify achievement service call
      expect(achievementService.calculateUserAchievements).toHaveBeenCalledWith(
        mockUserProfileResponse,
        mockCompletedCourses,
        expect.any(Array), // dashboardEnrolledCourses
        expect.any(Array), // dashboardBookmarkedCourses
      );

      // Verify result structure
      expect(result).toHaveProperty('stats');
      expect(result).toHaveProperty('currentSkills');
      expect(result).toHaveProperty('skillsInProgress');
      expect(result).toHaveProperty('achievements');
      expect(result).toHaveProperty('enrolledCourses');
      expect(result).toHaveProperty('completedCourses');
      expect(result).toHaveProperty('bookmarkedCourses');

      // Verify stats
      expect(result.stats.totalCourses).toBe(4);
      expect(result.stats.completedCourses).toBe(1);
      expect(result.stats.skillsMastered).toBe(3); // 1 completed course * 3
      expect(result.stats.certificates).toBe(1);

      // Verify skills
      expect(result.currentSkills).toEqual(mockUserSkills);
      expect(result.skillsInProgress).toEqual(mockUserSkillsInProgress);

      // Verify achievements
      expect(result.achievements).toEqual(mockAchievements);

      // Verify courses
      expect(result.enrolledCourses).toHaveLength(2);
      expect(result.completedCourses).toHaveLength(1);
      expect(result.bookmarkedCourses).toHaveLength(1);

      // Verify course transformation
      expect(result.enrolledCourses[0]).toHaveProperty('id', 'course-1');
      expect(result.enrolledCourses[0]).toHaveProperty('progress', 50);
      expect(result.enrolledCourses[0]).toHaveProperty('completedLessons', 2);
      expect(result.enrolledCourses[0]).toHaveProperty('totalLessons', 5);
      expect(result.enrolledCourses[0]).toHaveProperty('nextLesson', 'Lesson 3');
    });

    it('should handle empty course lists', async () => {
      // Mock user service calls with empty data
      vi.mocked(userService.getUserSkills).mockResolvedValue([]);
      vi.mocked(userService.getUserSkillsInProgress).mockResolvedValue([]);
      vi.mocked(userService.getUserEnrolledCourseIds).mockResolvedValue([]);
      vi.mocked(userService.getUserCompletedCourseIds).mockResolvedValue([]);
      vi.mocked(userService.getUserBookmarkedCourseIds).mockResolvedValue([]);
      vi.mocked(userService.getUserProfile).mockResolvedValue(mockUserProfileResponse);

      // Mock achievement service call
      vi.mocked(achievementService.calculateUserAchievements).mockReturnValue([]);

      const result = await getDashboardData('user-123');

      // Verify stats are zero
      expect(result.stats.totalCourses).toBe(0);
      expect(result.stats.completedCourses).toBe(0);
      expect(result.stats.skillsMastered).toBe(0);
      expect(result.stats.certificates).toBe(0);

      // Verify empty arrays
      expect(result.currentSkills).toEqual([]);
      expect(result.skillsInProgress).toEqual([]);
      expect(result.achievements).toEqual([]);
      expect(result.enrolledCourses).toEqual([]);
      expect(result.completedCourses).toEqual([]);
      expect(result.bookmarkedCourses).toEqual([]);
    });

    it('should handle course fetch failures gracefully', async () => {
      const mockUserSkills = ['JavaScript'];
      const mockEnrolledCourseIds = ['course-1', 'course-2'];
      const mockCompletedCourseIds = ['course-3'];
      const mockBookmarkedCourseIds = ['course-4'];

      // Mock user service calls
      vi.mocked(userService.getUserSkills).mockResolvedValue(mockUserSkills);
      vi.mocked(userService.getUserSkillsInProgress).mockResolvedValue([]);
      vi.mocked(userService.getUserEnrolledCourseIds).mockResolvedValue(mockEnrolledCourseIds);
      vi.mocked(userService.getUserCompletedCourseIds).mockResolvedValue(mockCompletedCourseIds);
      vi.mocked(userService.getUserBookmarkedCourseIds).mockResolvedValue(mockBookmarkedCourseIds);
      vi.mocked(userService.getUserProfile).mockResolvedValue(mockUserProfileResponse);

      // Mock course service calls - one success, one failure
      vi.mocked(courseService.getCourse)
        .mockResolvedValueOnce(mockCourseResponse) // course-1 success
        .mockRejectedValueOnce(new Error('Course not found')) // course-2 failure
        .mockResolvedValueOnce(mockCourseResponse) // course-3 success
        .mockResolvedValueOnce(mockCourseResponse); // course-4 success

      // Mock achievement service call
      vi.mocked(achievementService.calculateUserAchievements).mockReturnValue([]);

      const result = await getDashboardData('user-123');

      // Should still return data with successful fetches
      expect(result.enrolledCourses).toHaveLength(1); // Only course-1
      expect(result.completedCourses).toHaveLength(1); // course-3
      expect(result.bookmarkedCourses).toHaveLength(1); // course-4
    });

    it('should filter out completed courses from enrolled courses', async () => {
      const mockEnrolledCourseIds = ['course-1'];
      const mockCompletedCourseIds = ['course-2'];
      const mockBookmarkedCourseIds = [];

      const mockEnrolledCourse = {
        ...mockCourseResponse,
        id: 'course-1',
        enrolledUsers: [{ userId: 'user-123', progress: 100, currentLesson: 5, totalNumberOfLessons: 5, skills: ['JavaScript'] }], // 100% progress
      };

      const mockCompletedCourse = {
        ...mockCourseResponse,
        id: 'course-2',
        enrolledUsers: [{ userId: 'user-123', progress: 100, currentLesson: 5, totalNumberOfLessons: 5, skills: ['React'] }],
      };

      // Mock user service calls
      vi.mocked(userService.getUserSkills).mockResolvedValue([]);
      vi.mocked(userService.getUserSkillsInProgress).mockResolvedValue([]);
      vi.mocked(userService.getUserEnrolledCourseIds).mockResolvedValue(mockEnrolledCourseIds);
      vi.mocked(userService.getUserCompletedCourseIds).mockResolvedValue(mockCompletedCourseIds);
      vi.mocked(userService.getUserBookmarkedCourseIds).mockResolvedValue(mockBookmarkedCourseIds);
      vi.mocked(userService.getUserProfile).mockResolvedValue(mockUserProfileResponse);

      // Mock course service calls
      vi.mocked(courseService.getCourse)
        .mockResolvedValueOnce(mockEnrolledCourse)
        .mockResolvedValueOnce(mockCompletedCourse);

      // Mock achievement service call
      vi.mocked(achievementService.calculateUserAchievements).mockReturnValue([]);

      const result = await getDashboardData('user-123');

      // The enrolled course with 100% progress should be filtered out
      expect(result.enrolledCourses).toHaveLength(0);
      expect(result.completedCourses).toHaveLength(1);
    });

    it('should throw error when user service fails', async () => {
      vi.mocked(userService.getUserSkills).mockRejectedValue(new Error('User service error'));

      await expect(getDashboardData('user-123')).rejects.toThrow('User service error');
    });
  });
}); 