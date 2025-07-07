
import { SkillPayload } from '@/types/request/skill/SkillPayload.ts';
import { EnrolledCoursePayload } from '@/types/request/course/EnrolledCoursePayload.ts';
import { CoursePayload } from '@/types/request/course/CoursePayload.ts';

export interface UpdatePayload {
  bio?: string;
  profilePictureUrl?: string;
  password?: string;

  interests?: string[];
  skills?: SkillPayload[];
  skillsInProgress?: SkillPayload[];

  enrolledCourses?: EnrolledCoursePayload[];
  bookmarkedCourses?: CoursePayload[];

  completedCourses?: CoursePayload[];
}
