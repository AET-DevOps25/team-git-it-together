import { CategoryResponse } from '@/types/response/course/CategoryResponse.ts';
import { SkillResponse } from '@/types/response/skill/SkillResponse.ts';
import { EnrolledCourseResponse } from '@/types/response/course/EnrolledCourseResponse.ts';
import { CourseResponse } from '@/types/response/course/CourseResponse.ts';

export interface UserProfileResponse {
  id: string;
  firstName: string;
  lastName: string;
  username: string;
  email: string;
  profilePictureUrl?: string;
  bio: string;

  interests: CategoryResponse[];
  skills: SkillResponse[];
  skillsInProgress: SkillResponse[];

  enrolledCourses: EnrolledCourseResponse[];
  bookmarkedCourses: CourseResponse[];
  completedCourses?: CourseResponse[];
}