import { LessonContentType } from '@/types/utils/LessonContentType.ts';

export interface LessonContent {
  type: LessonContentType;
  content: string;
}
