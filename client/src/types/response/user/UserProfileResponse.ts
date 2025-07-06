import { CourseResponse } from '@/types/response/course/CourseResponse.ts';

export interface UserProfileResponse {
  id: string;
  firstName: string;
  lastName: string;
  username: string;
  email: string;
  profilePictureUrl?: string;
  bio: string;

  interests: string[];
  skills: string[];
  skillsInProgress: string[];

  enrolledCourses: CourseResponse[];
  bookmarkedCourses: CourseResponse[];
  completedCourses?: CourseResponse[];
}
