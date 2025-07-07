import { UserAchievement } from '@/types';
import * as userService from '@/services/user.service';
import * as courseService from '@/services/course.service';
import * as achievementService from '@/services/achievement.service';

export function setAuthToken(token: string | null) {
  // set it in the user service
  userService.setAuthToken(token);
  // set it in the course service
  courseService.setAuthToken(token);
}

// ------------------------------------------------------------
// Helper interfaces for dashboard data

// For the stats section
export interface DashboardStats {
  totalCourses: number;
  completedCourses: number;
  skillsMastered: number;
  certificates: number;
}

export interface DashboardCourse {
  id: string;
  title: string;
  description: string;
  progress: number;
  totalLessons: number;
  completedLessons: number;
  category: string;
  difficulty: string;
  nextLesson: string;
  timeSpent: string;
  instructor: string;
  thumbnailUrl?: string;
}

export interface DashboardData {
  stats: DashboardStats;
  currentSkills: string[];
  skillsInProgress: string[];
  achievements: UserAchievement[];
  enrolledCourses: DashboardCourse[];
  completedCourses: DashboardCourse[];
  bookmarkedCourses: DashboardCourse[];
}
// ------------------------------------------------------------

function transformCourse(course: any, userId: string, isCompleted: boolean = false): DashboardCourse {
  const userEnrollment = course.enrolledUsers?.find((u: any) => u.userId === userId);
  const progress = userEnrollment?.progress || 0;
  const currentLesson = userEnrollment?.currentLesson || 0;
  const totalLessons = userEnrollment?.totalNumberOfLessons || 0;
  return {
    id: course.id,
    title: course.title,
    description: course.description,
    progress,
    totalLessons,
    completedLessons: currentLesson,
    category: course.categories?.[0] || 'General',
    difficulty: course.level || 'Beginner',
    nextLesson: isCompleted ? 'Course Completed!' : `Lesson ${currentLesson + 1}`,
    timeSpent: `${Math.round(progress * 0.3)} hours`, // Placeholder logic
    instructor: course.instructor,
    thumbnailUrl: course.thumbnailUrl,
  };
}

// Helper: Fetch multiple courses in parallel, filter nulls (for missing/deleted ones)
async function fetchCourses(ids: string[], getCourse: (id: string) => Promise<any>): Promise<any[]> {
  if (!ids.length) return [];
  const fetched = await Promise.all(ids.map(async (id) => {
    try {
      return await getCourse(id);
    } catch (e) {
      console.warn(`Course fetch failed for ${id}:`, e);
      return null;
    }
  }));
  return fetched.filter(c => c !== null);
}

export async function getDashboardData(userId: string): Promise<DashboardData> {
  try {
    // Parallel fetching of all IDs/skills/profile
    const [
      userSkills,
      userSkillsInProgress,
      enrolledCourseIds,
      completedCourseIds,
      bookmarkedCourseIds,
      userProfile,
    ] = await Promise.all([
      userService.getUserSkills(userId),
      userService.getUserSkillsInProgress(userId),
      userService.getUserEnrolledCourseIds(userId),
      userService.getUserCompletedCourseIds(userId),
      userService.getUserBookmarkedCourseIds(userId),
      userService.getUserProfile(userId),
    ]);

    // Parallel fetch of full course details
    const [
      enrolledCourses,
      completedCourses,
      bookmarkedCourses,
    ] = await Promise.all([
      fetchCourses(enrolledCourseIds, courseService.getCourse),
      fetchCourses(completedCourseIds, courseService.getCourse),
      fetchCourses(bookmarkedCourseIds, courseService.getCourse),
    ]);

    // Deduplicate for stats (Set of all course IDs)
    const totalCourseSet = new Set([
      ...enrolledCourseIds,
      ...completedCourseIds,
      ...bookmarkedCourseIds,
    ]);
    const completedCourseSet = new Set(completedCourseIds);

    // Dashboard course objects
    const dashboardEnrolledCourses = enrolledCourses
      .filter(course => {
        const userEnrollment = course.enrolledUsers?.find((u: any) => u.userId === userId);
        const progress = userEnrollment?.progress || 0;
        return progress < 100;
      })
      .map(course => transformCourse(course, userId));

    const dashboardCompletedCourses = completedCourses
      .map(course => transformCourse(course, userId, true));
    const dashboardBookmarkedCourses = bookmarkedCourses
      .map(course => transformCourse(course, userId));

    // Stats
    const stats: DashboardStats = {
      totalCourses: totalCourseSet.size,
      completedCourses: completedCourseSet.size,
      skillsMastered: Math.round(completedCourseSet.size * 3), // tweak as needed
      certificates: completedCourseSet.size,
    };

    // Achievements (keep this last)
    const achievements = achievementService.calculateUserAchievements(
      userProfile,
      completedCourses,
      dashboardEnrolledCourses,
      dashboardBookmarkedCourses
    );

    return {
      stats,
      currentSkills: userSkills,
      skillsInProgress: userSkillsInProgress,
      achievements,
      enrolledCourses: dashboardEnrolledCourses,
      completedCourses: dashboardCompletedCourses,
      bookmarkedCourses: dashboardBookmarkedCourses,
    };

  } catch (error: any) {
    console.error('Error fetching dashboard data:', {
      error: error.message || error,
      stack: error.stack,
    });
    throw error;
  }
}