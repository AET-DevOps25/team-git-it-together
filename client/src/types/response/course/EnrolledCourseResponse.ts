import { CourseResponse } from '@/types/response/course/CourseResponse.ts';
import { CourseProgressResponse } from '@/types/response/course/CourseProgressResponse.ts';

export interface EnrolledCourseResponse {
  course: CourseResponse,
  progress: CourseProgressResponse
}