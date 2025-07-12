import { Level } from '@/types/utils/Level.ts';
import { Language } from '@/types/utils/Language.ts';
import { EnrolledUserInfo } from '@/types/response/course/CourseResponse.ts';

export interface CourseSummaryResponse {
  id: string;
  title: string;
  description: string;
  instructor: string;
  skills: string[];
  thumbnailUrl?: string;
  numberOfEnrolledUsers: number;
  categories: string[];
  level: Level;
  isPublic: boolean;
  published: boolean;
  language: Language;
  rating: number;
  enrolledUsers: EnrolledUserInfo[];
} 