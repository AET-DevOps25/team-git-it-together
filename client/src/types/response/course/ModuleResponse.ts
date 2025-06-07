import { LessonResponse } from '@/types/response/course/LessonResponse.ts';

export interface ModuleResponse {
  title: string;
  description?: string;
  courseId: string;
  lessons: LessonResponse[];
  order: number;
}
