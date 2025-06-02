import { CoursePayload } from '@/types/request/course/CoursePayload';
import { CourseProgressPayload } from '@/types/request/course/CourseProgressPayload';

export interface EnrolledCoursePayload {
  course: CoursePayload;
  progress: CourseProgressPayload;
}