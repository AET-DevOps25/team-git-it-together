import { LessonContent } from '@/types/utils/LessonContent.ts';

export interface LessonResponse {
  title: string;
  description: string;
  content: LessonContent;
  order: number;
}
