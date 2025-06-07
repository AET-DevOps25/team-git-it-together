import { LessonPayload } from '@/types/request/course/LessonPayload.ts';

export interface ModulePayload {
  title: string;
  description?: string;
  courseId: string;
  lessons?: LessonPayload[];
  order?: number;
}
