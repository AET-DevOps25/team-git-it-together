import type {
  UserAchievement,
  AchievementDefinition,
  UserProfileResponse,
} from '@/types';
import { AchievementCategory } from '@/types';

// Achievement definitions
const ACHIEVEMENT_DEFINITIONS: AchievementDefinition[] = [
  {
    id: 'first-course-completed',
    title: 'First Course Completed',
    description: 'Completed your first course',
    icon: 'Trophy',
    category: AchievementCategory.COURSE_COMPLETION,
    criteria: { type: 'completed_courses', value: 1, description: 'Complete 1 course' }
  },
  {
    id: 'course-completion-5',
    title: 'Course Connoisseur',
    description: 'Completed 5 courses',
    icon: 'Trophy',
    category: AchievementCategory.COURSE_COMPLETION,
    criteria: { type: 'completed_courses', value: 5, description: 'Complete 5 courses' }
  },
  {
    id: 'course-completion-10',
    title: 'Learning Champion',
    description: 'Completed 10 courses',
    icon: 'Trophy',
    category: AchievementCategory.COURSE_COMPLETION,
    criteria: { type: 'completed_courses', value: 10, description: 'Complete 10 courses' }
  },
  {
    id: 'active-learner',
    title: 'Active Learner',
    description: 'Enrolled in 3 or more courses',
    icon: 'Target',
    category: AchievementCategory.ENGAGEMENT,
    criteria: { type: 'enrolled_courses', value: 3, description: 'Enroll in 3 courses' }
  },
  {
    id: 'skill-master-2',
    title: 'Skill Master',
    description: 'Mastered 2 skills',
    icon: 'Award',
    category: AchievementCategory.SKILL_MASTERY,
    criteria: { type: 'mastered_skills', value: 2, description: 'Master 2 skills' }
  },
  {
    id: 'skill-master-5',
    title: 'Skill Expert',
    description: 'Mastered 5 skills',
    icon: 'Award',
    category: AchievementCategory.SKILL_MASTERY,
    criteria: { type: 'mastered_skills', value: 5, description: 'Master 5 skills' }
  },
  {
    id: 'skill-master-10',
    title: 'Skill Legend',
    description: 'Mastered 10 skills',
    icon: 'Award',
    category: AchievementCategory.SKILL_MASTERY,
    criteria: { type: 'mastered_skills', value: 10, description: 'Master 10 skills' }
  },
  {
    id: 'learning-streak-7',
    title: 'Week Warrior',
    description: 'Maintained a 7-day learning streak',
    icon: 'Target',
    category: AchievementCategory.LEARNING_STREAK,
    criteria: { type: 'learning_streak', value: 7, description: 'Maintain 7-day streak' }
  },
  {
    id: 'learning-streak-30',
    title: 'Monthly Master',
    description: 'Maintained a 30-day learning streak',
    icon: 'Target',
    category: AchievementCategory.LEARNING_STREAK,
    criteria: { type: 'learning_streak', value: 30, description: 'Maintain 30-day streak' }
  },
  {
    id: 'certificate-collector',
    title: 'Certificate Collector',
    description: 'Earned 3 certificates',
    icon: 'Award',
    category: AchievementCategory.MILESTONE,
    criteria: { type: 'certificates', value: 3, description: 'Earn 3 certificates' }
  },
  {
    id: 'bookmarker',
    title: 'Bookmarker',
    description: 'Bookmarked 5 courses',
    icon: 'Bookmark',
    category: AchievementCategory.ENGAGEMENT,
    criteria: { type: 'bookmarked_courses', value: 5, description: 'Bookmark 5 courses' }
  },
  {
    id: 'diverse-learner',
    title: 'Diverse Learner',
    description: 'Completed courses in 3 different categories',
    icon: 'Target',
    category: AchievementCategory.ENGAGEMENT,
    criteria: { type: 'course_categories', value: 3, description: 'Complete courses in 3 categories' }
  },
  {
    id: 'speed-learner',
    title: 'Speed Learner',
    description: 'Completed 3 courses in one month',
    icon: 'Target',
    category: AchievementCategory.MILESTONE,
    criteria: { type: 'courses_in_month', value: 3, description: 'Complete 3 courses in a month' }
  },
  {
    id: 'perfect-score',
    title: 'Perfect Score',
    description: 'Achieved 100% progress in a course',
    icon: 'Star',
    category: AchievementCategory.MILESTONE,
    criteria: { type: 'perfect_course', value: 1, description: 'Get 100% in a course' }
  }
];

/**
 * Calculate user achievements based on their profile and course data
 * @param userProfile User profile data
 * @param completedCourses List of completed courses
 * @param enrolledCourses List of enrolled courses
 * @param bookmarkedCourses List of bookmarked courses
 * @returns Array of earned achievements
 */
export function calculateUserAchievements(
  userProfile: UserProfileResponse,
  completedCourses: any[] = [],
  enrolledCourses: any[] = [],
  bookmarkedCourses: any[] = []
): UserAchievement[] {
  const achievements: UserAchievement[] = [];
  const today = new Date().toISOString().split('T')[0];

  // Helper function to check if achievement is earned
  const isAchievementEarned = (definition: AchievementDefinition): boolean => {
    const { type, value } = definition.criteria;
    
    switch (type) {
      case 'completed_courses':
        return completedCourses.length >= value;
      case 'enrolled_courses':
        return enrolledCourses.length >= value;
      case 'mastered_skills':
        return (userProfile.skills?.length || 0) >= value;
      case 'bookmarked_courses':
        return bookmarkedCourses.length >= value;
      case 'certificates':
        return completedCourses.length >= value;
      case 'course_categories': {
        const categories = new Set(completedCourses.map(course => course.categories).flat());
        return categories.size >= value;
      }
      case 'courses_in_month': {
        const thisMonth = new Date().getMonth();
        const thisYear = new Date().getFullYear();
        const coursesThisMonth = completedCourses.filter(course => {
          const courseDate = new Date(course.completedAt || course.updatedAt);
          return courseDate.getMonth() === thisMonth && courseDate.getFullYear() === thisYear;
        });
        return coursesThisMonth.length >= value;
      }
      case 'perfect_course':
        return completedCourses.some(course => course.progress === 100);
      default:
        return false;
    }
  };

  // Check each achievement definition
  ACHIEVEMENT_DEFINITIONS.forEach(definition => {
    if (isAchievementEarned(definition)) {
      achievements.push({
        id: definition.id,
        title: definition.title,
        description: definition.description,
        date: today,
        icon: definition.icon,
        category: definition.category
      });
    }
  });

  return achievements;
}

/**
 * Get achievement progress for a specific achievement
 * @param definition Achievement definition
 * @param userProfile User profile data
 * @param completedCourses List of completed courses
 * @param enrolledCourses List of enrolled courses
 * @param bookmarkedCourses List of bookmarked courses
 * @returns Progress object with current and max values
 */
export function getAchievementProgress(
  definition: AchievementDefinition,
  userProfile: UserProfileResponse,
  completedCourses: any[] = [],
  enrolledCourses: any[] = [],
  bookmarkedCourses: any[] = []
): { progress: number; maxProgress: number; isCompleted: boolean } {
  const { type, value } = definition.criteria;
  let currentProgress = 0;

  switch (type) {
    case 'completed_courses':
      currentProgress = completedCourses.length;
      break;
    case 'enrolled_courses':
      currentProgress = enrolledCourses.length;
      break;
    case 'mastered_skills':
      currentProgress = userProfile.skills?.length || 0;
      break;
    case 'bookmarked_courses':
      currentProgress = bookmarkedCourses.length;
      break;
    case 'certificates':
      currentProgress = completedCourses.length;
      break;
    case 'course_categories': {
      const categories = new Set(completedCourses.map(course => course.categories).flat());
      currentProgress = categories.size;
      break;
    }
    case 'courses_in_month': {
      const thisMonth = new Date().getMonth();
      const thisYear = new Date().getFullYear();
      const coursesThisMonth = completedCourses.filter(course => {
        const courseDate = new Date(course.completedAt || course.updatedAt);
        return courseDate.getMonth() === thisMonth && courseDate.getFullYear() === thisYear;
      });
      currentProgress = coursesThisMonth.length;
      break;
    }
    case 'perfect_course':
      currentProgress = completedCourses.some(course => course.progress === 100) ? 1 : 0;
      break;
    default:
      currentProgress = 0;
  }

  return {
    progress: Math.min(currentProgress, value),
    maxProgress: value,
    isCompleted: currentProgress >= value
  };
}

/**
 * Get all achievement definitions
 * @returns Array of all achievement definitions
 */
export function getAllAchievementDefinitions(): AchievementDefinition[] {
  return ACHIEVEMENT_DEFINITIONS;
}

/**
 * Get achievement definition by ID
 * @param id Achievement ID
 * @returns Achievement definition or undefined if not found
 */
export function getAchievementDefinition(id: string): AchievementDefinition | undefined {
  return ACHIEVEMENT_DEFINITIONS.find(def => def.id === id);
}

/**
 * Get achievements by category
 * @param category Achievement category
 * @returns Array of achievement definitions for the given category
 */
export function getAchievementsByCategory(category: AchievementCategory): AchievementDefinition[] {
  return ACHIEVEMENT_DEFINITIONS.filter(def => def.category === category);
}

/**
 * Get recently earned achievements (within last 30 days)
 * @param achievements Array of user achievements
 * @returns Array of recently earned achievements
 */
export function getRecentAchievements(achievements: UserAchievement[]): UserAchievement[] {
  const thirtyDaysAgo = new Date();
  thirtyDaysAgo.setDate(thirtyDaysAgo.getDate() - 30);
  
  return achievements.filter(achievement => {
    const achievementDate = new Date(achievement.date);
    return achievementDate >= thirtyDaysAgo;
  });
}

/**
 * Get achievement statistics
 * @param achievements Array of user achievements
 * @returns Object with achievement statistics
 */
export function getAchievementStats(achievements: UserAchievement[]) {
  const totalAchievements = ACHIEVEMENT_DEFINITIONS.length;
  const earnedAchievements = achievements.length;
  const completionRate = totalAchievements > 0 ? (earnedAchievements / totalAchievements) * 100 : 0;
  
  const categoryStats = Object.values(AchievementCategory).reduce((stats, category) => {
    const categoryAchievements = achievements.filter(a => a.category === category);
    stats[category] = {
      total: ACHIEVEMENT_DEFINITIONS.filter(d => d.category === category).length,
      earned: categoryAchievements.length
    };
    return stats;
  }, {} as Record<AchievementCategory, { total: number; earned: number }>);

  return {
    totalAchievements,
    earnedAchievements,
    completionRate,
    categoryStats
  };
} 