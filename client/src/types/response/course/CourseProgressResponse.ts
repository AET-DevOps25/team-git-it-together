export interface CourseProgressResponse {
  courseId: string;
  userId: string;
  progress: number;
  enrolledAt: string; // ISO date string
  lastAccessedAt: string;
  completed: boolean;
  completedAt?: string;
}
