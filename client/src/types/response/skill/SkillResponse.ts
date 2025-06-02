import { CategoryResponse } from '@/types/response/course/CategoryResponse.ts';
import { Level } from '@/types/utils/Level.ts';

export interface SkillResponse {
  id: string;
  name: string;
  description: string;
  category: CategoryResponse;
  difficultyLevel: Level;
}