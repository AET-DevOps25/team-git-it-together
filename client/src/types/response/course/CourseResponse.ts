import { SkillResponse } from '@/types/response/skill/SkillResponse.ts';
import { CategoryResponse } from '@/types/response/course/CategoryResponse.ts';
import { Level } from '@/types/utils/Level.ts';
import { ModuleResponse } from '@/types/response/course/ModuleResponse.ts';
import { Language } from '@/types/utils/Language.ts';

export interface CourseResponse {
  id: string;
  title: string;
  description: string;
  instructor: string;
  skills: SkillResponse[];
  modules: ModuleResponse[];
  numberOfEnrolledUsers: number;
  categories: CategoryResponse[];
  level: Level;
  thumbnailUrl?: string;
  published: boolean;
  language: Language;
  rating: number;
}
