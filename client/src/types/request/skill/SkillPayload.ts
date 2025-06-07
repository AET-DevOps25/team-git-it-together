import type { CategoryPayload } from '@/types/request/course/CategoryPayload.ts';
import type { Level } from '@/types/utils/Level.ts';

export interface SkillPayload {
  id: string;
  name: string;
  description: string;
  category: CategoryPayload;
  iconUrl?: string;
  level: Level;
}
