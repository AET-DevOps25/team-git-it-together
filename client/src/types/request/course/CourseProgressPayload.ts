export interface CourseProgressPayload {
  courseId: string;
  userId: string; // User who is enrolled in the course
  progress?: number; // Percentage of course completed (0.0 to 100.0)
  enrolledAt?: string; // ISO string for date (LocalDateTime)
  lastAccessedAt?: string; // ISO string for date
  completed?: boolean; // Whether the course has been completed
  completedAt?: string | null; // ISO string or null
}
