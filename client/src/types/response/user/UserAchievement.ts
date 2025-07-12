export interface UserAchievement {
  id: string;
  title: string;
  description: string;
  date: string;
  icon: string;
  category: AchievementCategory;
  progress?: number;
  maxProgress?: number;
}

export enum AchievementCategory {
  COURSE_COMPLETION = 'COURSE COMPLETION',
  LEARNING_STREAK = 'LEARNING STREAK',
  SKILL_MASTERY = 'SKILL MASTERY',
  ENGAGEMENT = 'ENGAGEMENT',
  MILESTONE = 'MILESTONE'
}

export interface AchievementCriteria {
  type: string;
  value: number;
  description: string;
}

export interface AchievementDefinition {
  id: string;
  title: string;
  description: string;
  icon: string;
  category: AchievementCategory;
  criteria: AchievementCriteria;
  rewards?: string[];
} 