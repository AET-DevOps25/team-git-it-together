import { Level } from '@/types/utils/Level.ts';
import { Language } from '@/types/utils/Language.ts';
import { ModuleResponse } from '@/types/response/course/ModuleResponse.ts';

export interface EnrolledUserInfo {
  userId: string;
  progress: number;
  skills: string[];
  currentLesson: number;
  totalNumberOfLessons: number;
}

export interface CourseResponse {
  id: string;
  title: string;
  description: string;
  instructor: string;
  skills: string[];
  modules: ModuleResponse[];
  numberOfEnrolledUsers: number;
  categories: string[];
  level: Level;
  thumbnailUrl?: string;
  published: boolean;
  isPublic: boolean;
  language: Language;
  rating: number;
  enrolledUsers: EnrolledUserInfo[];
}
