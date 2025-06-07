import { LessonContent } from '@/types/utils/LessonContent.ts';

export interface LessonPayload {
  title: string;
  description: string;
  content: LessonContent;
  order?: number;
}