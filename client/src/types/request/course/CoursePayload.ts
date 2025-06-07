import { SkillPayload } from '@/types/request/skill/SkillPayload.ts';
import { ModulePayload } from '@/types/request/course/ModulePayload.ts';
import { CategoryPayload } from '@/types/request/course/CategoryPayload.ts';
import { Level } from '@/types/utils/Level.ts';
import { Language } from '@/types/utils/Language.ts';

export interface CoursePayload {
  id: string;
  title: string;
  description: string;
  instructor: string;
  skills?: SkillPayload[];
  modules?: ModulePayload[];
  enrolledUserIds?: string[];
  numberOfEnrolledUsers?: number;
  categories?: CategoryPayload[];
  level?: Level;
  thumbnailUrl?: string;
  published?: boolean;
  language: Language;
  rating?: number;
}