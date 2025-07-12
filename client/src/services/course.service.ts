import { API_BASE_URL } from '@/constants/app.ts';
import type { CourseResponse, CourseSummaryResponse, Level, Language, CoursePayload } from '@/types';
import { parseErrorResponse } from '@/utils/response.utils.ts';

const BASE_URL = `${API_BASE_URL}/courses`;

// Holds the current JWT. Set via setAuthToken().
let authToken: string | null = null;

/**
 * Configure the "Authorization: Bearer <token>" header for future requests.
 * Pass `null` to clear it.
 */
export function setAuthToken(token: string | null) {
  authToken = token;
}

/**
 * Fetch public courses (no authentication required).
 * @throws ApiError object { status: number, message: string } on 4xx/5xx
 */
export async function getPublicCourses(): Promise<CourseSummaryResponse[]> {
  const resp = await fetch(`${BASE_URL}/public`, {
    method: 'GET',
    headers: { 'Content-Type': 'application/json' },
  });

  if (!resp.ok) {
    throw await parseErrorResponse(resp);
  }

  return await resp.json();
}

/**
 * Fetch all courses (requires authentication).
 * Requires that setAuthToken(token) has been called earlier.
 * @throws ApiError object { status: number, message: string } on 4xx/5xx or if no token
 */
export async function getAllCourses(): Promise<CourseSummaryResponse[]> {
  if (!authToken) {
    throw { status: 401, message: 'No authentication token provided' };
  }

  const resp = await fetch(`${BASE_URL}`, {
    method: 'GET',
    headers: {
      'Content-Type': 'application/json',
      Authorization: `Bearer ${authToken}`,
    },
  });

  if (!resp.ok) {
    throw await parseErrorResponse(resp);
  }

  return await resp.json();
}

/**
 * Fetch all published courses (requires authentication).
 * Requires that setAuthToken(token) has been called earlier.
 * @throws ApiError object { status: number, message: string } on 4xx/5xx or if no token
 */
export async function getAllPublishedCourses(): Promise<CourseSummaryResponse[]> {
  const resp = await fetch(`${BASE_URL}/published`, {
    method: 'GET',
    headers: {
      'Content-Type': 'application/json',
      Authorization: `Bearer ${authToken}`,
    },
  });

  if (!resp.ok) {
    throw await parseErrorResponse(resp);
  }

  return await resp.json();
}

/**
 * Fetch a specific course by ID (requires authentication).
 * Requires that setAuthToken(token) has been called earlier.
 * @param courseId The ID of the course to fetch
 * @throws ApiError object { status: number, message: string } on 4xx/5xx or if no token
 */
export async function getCourse(courseId: string): Promise<CourseResponse> {
  if (!authToken) {
    throw { status: 401, message: 'No authentication token provided' };
  }

  const resp = await fetch(`${BASE_URL}/${courseId}`, {
    method: 'GET',
    headers: {
      'Content-Type': 'application/json',
      Authorization: `Bearer ${authToken}`,
    },
  });

  if (!resp.ok) {
    throw await parseErrorResponse(resp);
  }

  return await resp.json();
}

/**
 * Search courses with advanced filters (public endpoint).
 * Works for both authenticated and non-authenticated users.
 * @param params Search parameters
 * @throws ApiError object { status: number, message: string } on 4xx/5xx
 */
export async function searchCourses(params: {
  instructor?: string;
  level?: Level;
  language?: Language;
  skill?: string;
  category?: string;
  title?: string;
  isPublished?: boolean;
  isPublic?: boolean;
}): Promise<CourseSummaryResponse[]> {
  const searchParams = new URLSearchParams();
  Object.entries(params).forEach(([key, value]) => {
    if (value !== undefined && value !== null && value !== '') {
      searchParams.append(key, value.toString());
    }
  });

  // Add authentication status parameter
  const isAuthenticated = authToken !== null;
  searchParams.append('isAuthenticated', isAuthenticated.toString());

  const headers: Record<string, string> = {
    'Content-Type': 'application/json',
  };

  // Add auth token if available
  if (authToken) {
    headers.Authorization = `Bearer ${authToken}`;
  }

  const resp = await fetch(`${BASE_URL}/search?${searchParams.toString()}`, {
    method: 'GET',
    headers,
  });

  if (!resp.ok) {
    throw await parseErrorResponse(resp);
  }

  return await resp.json();
}

/**
 * Enroll in a course (requires authentication).
 * Requires that setAuthToken(token) has been called earlier.
 * @param courseId The ID of the course to enroll in
 * @param userId The ID of the user enrolling
 * @throws ApiError object { status: number, message: string } on 4xx/5xx or if no token
 */
export async function enrollInCourse(courseId: string, userId: string): Promise<CourseResponse> {
  if (!authToken) {
    throw { status: 401, message: 'No authentication token provided' };
  }

  const resp = await fetch(`${BASE_URL}/${courseId}/enroll/${userId}`, {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
      Authorization: `Bearer ${authToken}`,
    },
  });

  if (!resp.ok) {
    throw await parseErrorResponse(resp);
  }

  return await resp.json();
}

/**
 * Unenroll from a course (requires authentication).
 * Requires that setAuthToken(token) has been called earlier.
 * @param courseId The ID of the course to unenroll from
 * @param userId The ID of the user unenrolling
 * @throws ApiError object { status: number, message: string } on 4xx/5xx or if no token
 */
export async function unenrollFromCourse(courseId: string, userId: string): Promise<void> {
  if (!authToken) {
    throw { status: 401, message: 'No authentication token provided' };
  }

  const resp = await fetch(`${BASE_URL}/${courseId}/enroll/${userId}`, {
    method: 'DELETE',
    headers: {
      'Content-Type': 'application/json',
      Authorization: `Bearer ${authToken}`,
    },
  });

  if (!resp.ok) {
    throw await parseErrorResponse(resp);
  }
}

/**
 * Complete a lesson and update progress (requires authentication).
 * @param courseId The ID of the course
 * @param lessonId The ID of the lesson to complete
 * @param userId The ID of the user
 * @param currentCourse The current course data to update
 * @throws ApiError object { status: number, message: string } on 4xx/5xx or if no token
 */
export async function completeLesson(courseId: string, lessonId: string, userId: string, currentCourse: CourseResponse): Promise<CourseResponse> {
  if (!authToken) {
    throw { status: 401, message: 'No authentication token provided' };
  }

  // Find the user's enrollment and update currentLesson
  const updatedEnrolledUsers = currentCourse.enrolledUsers.map(enrolledUser => {
    if (enrolledUser.userId === userId) {
      // Get the current lesson order from the URL and send currentLesson + 1
      const currentLessonOrder = parseInt(lessonId);
      
      return {
        ...enrolledUser,
        currentLesson: currentLessonOrder + 1
        // Don't send progress - server will calculate it based on currentLesson
      };
    }
    return enrolledUser;
  });

  // Prepare the update request
  const updateRequest = {
    enrolledUsers: updatedEnrolledUsers
  };

  console.log('CompleteLesson Debug:', {
    courseId,
    lessonId,
    userId,
    currentLessonOrder: parseInt(lessonId),
    newCurrentLesson: parseInt(lessonId) + 1,
    updateRequest
  });

  const resp = await fetch(`${BASE_URL}/${courseId}`, {
    method: 'PUT',
    headers: {
      'Content-Type': 'application/json',
      Authorization: `Bearer ${authToken}`,
    },
    body: JSON.stringify(updateRequest),
  });

  if (!resp.ok) {
    throw await parseErrorResponse(resp);
  }

  return await resp.json();
}

/**
 * Complete a course for a user (requires authentication).
 * @param courseId The ID of the course to complete
 * @param userId The ID of the user
 * @param currentCourse The current course data to update
 * @throws ApiError object { status: number, message: string } on 4xx/5xx or if no token
 */
export async function completeCourse(courseId: string, userId: string, currentCourse: CourseResponse): Promise<CourseResponse> {
  if (!authToken) {
    throw { status: 401, message: 'No authentication token provided' };
  }

  // First, call the completion endpoint
  const completionResp = await fetch(`${BASE_URL}/${courseId}/complete/${userId}`, {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
      Authorization: `Bearer ${authToken}`,
    },
  });

  if (!completionResp.ok) {
    throw await parseErrorResponse(completionResp);
  }

  // Then, update the progress to 100%
  const updatedEnrolledUsers = currentCourse.enrolledUsers.map(enrolledUser => {
    if (enrolledUser.userId === userId) {
      return {
        ...enrolledUser,
        progress: 100.0
      };
    }
    return enrolledUser;
  });

  // Prepare the update request
  const updateRequest = {
    enrolledUsers: updatedEnrolledUsers
  };

  const updateResp = await fetch(`${BASE_URL}/${courseId}`, {
    method: 'PUT',
    headers: {
      'Content-Type': 'application/json',
      Authorization: `Bearer ${authToken}`,
    },
    body: JSON.stringify(updateRequest),
  });

  if (!updateResp.ok) {
    throw await parseErrorResponse(updateResp);
  }

  return await updateResp.json();
}

/**
 * Bookmark a course (requires authentication).
 * Requires that setAuthToken(token) has been called earlier.
 * @param courseId The ID of the course to bookmark
 * @param userId The ID of the user bookmarking
 * @throws ApiError object { status: number, message: string } on 4xx/5xx or if no token
 */
export async function bookmarkCourse(courseId: string, userId: string): Promise<void> {
  if (!authToken) {
    throw { status: 401, message: 'No authentication token provided' };
  }

  const resp = await fetch(`${BASE_URL}/${courseId}/bookmark/${userId}`, {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
      Authorization: `Bearer ${authToken}`,
    },
  });

  if (!resp.ok) {
    throw await parseErrorResponse(resp);
  }
}

/**
 * Remove bookmark from a course (requires authentication).
 * Requires that setAuthToken(token) has been called earlier.
 * @param courseId The ID of the course to unbookmark
 * @param userId The ID of the user unbookmarking
 * @throws ApiError object { status: number, message: string } on 4xx/5xx or if no token
 */
export async function unbookmarkCourse(courseId: string, userId: string): Promise<void> {
  if (!authToken) {
    throw { status: 401, message: 'No authentication token provided' };
  }

  const resp = await fetch(`${BASE_URL}/${courseId}/bookmark/${userId}`, {
    method: 'DELETE',
    headers: {
      'Content-Type': 'application/json',
      Authorization: `Bearer ${authToken}`,
    },
  });

  if (!resp.ok) {
    throw await parseErrorResponse(resp);
  }
}

/**
 * Get the enrolled courses for a user (requires authentication).
 * Requires that setAuthToken(token) has been called earlier.
 * @param userId The ID of the user whose enrolled courses to fetch
 * @returns The enrolled courses for the user
 * @throws ApiError object { status: number, message: string } on 4xx/5xx or if no token
 */
export async function getUserEnrolledCourses(userId: string): Promise<any[]> {
  if (!authToken) {
    throw { status: 401, message: 'No authentication token provided' };
  }

  const resp = await fetch(`${BASE_URL}/user/${userId}/enrolled`, {
    method: 'GET',
    headers: {
      'Content-Type': 'application/json',
      Authorization: `Bearer ${authToken}`,
    },
  });

  if (!resp.ok) {
    throw await parseErrorResponse(resp);
  }

  return await resp.json();
}

/**
 * Generate a response from a prompt using the AI model (requires authentication).
 * Requires that setAuthToken(token) has been called earlier.
 * @param prompt The prompt to send to the AI model
 * @returns The AI-generated response
 * @throws ApiError object { status: number, message: string } on 4xx/5xx or if no token
 */
export async function generateResponseFromPrompt(prompt: string): Promise<string> {
  if (!authToken) {
    throw { status: 401, message: 'No authentication token provided' };
  }
  // POST "/generate/prompt"
  const resp = await fetch(`${BASE_URL}/generate/prompt`, {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
      Authorization: `Bearer ${authToken}`,
    },
    body: JSON.stringify({ prompt }),
  });

  if (!resp.ok) {
    throw await parseErrorResponse(resp);
  }

  const raw = await resp.text();
  try {
    const data = JSON.parse(raw);
    return data.response;
  } catch {
    return raw;
  }
}

/**
 * Generate a course for a user based on a prompt and skills (requires authentication).
 * Requires that setAuthToken(token) has been called earlier.
 * @param userId The ID of the user for whom to generate the course
 * @param prompt The prompt to guide course generation
 * @param skills An array of skills to include in the course
 * @returns The generated course content
 * @throws ApiError object { status: number, message: string } on 4xx/5xx or if no token
 */
export async function generateCourseForUser(userId: string, prompt: string, skills: string[]): Promise<CoursePayload | string> {
  if (!authToken) {
    throw { status: 401, message: 'No authentication token provided' };
  }
  // POST "/generate/learning_path/{userId}"
  const resp = await fetch(`${BASE_URL}/generate/learning_path/${userId}`, {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
      Authorization: `Bearer ${authToken}`,
    },
    body: JSON.stringify({ prompt, skills }),
  });

  if (!resp.ok) {
    throw await parseErrorResponse(resp);
  }

  const raw = await resp.text();
  try {
    const data = JSON.parse(raw);
    // The backend returns the course object directly, not wrapped in a 'response' property
    return data;
  } catch {
    return raw;
  }
}

/**
 * Confirm course generation for a user (requires authentication).
 * Requires that setAuthToken(token) has been called earlier.
 * 
 * This endpoint will auto confirm the last generated course for the user.
 * It is used to finalize the course generation process after the AI has created the course content.
 * 
 * @param userId The ID of the user confirming the course generation
 * @returns Confirmation message or updated course data
 * @throws ApiError object { status: number, message: string } on 4xx/5xx or if no token
 */
export async function confirmCourseGeneration(userId: string): Promise<CourseResponse | string> {
  if (!authToken) {
    throw { status: 401, message: 'No authentication token provided' };
  }
  //POST "/generate/learning_path/{userId}/confirm"
  const resp = await fetch(`${BASE_URL}/generate/learning_path/${userId}/confirm`, {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
      Authorization: `Bearer ${authToken}`,
    },
  });

  if (!resp.ok) {
    throw await parseErrorResponse(resp);
  }

  const raw = await resp.text();
  try {
    const data = JSON.parse(raw);
    return data;
  } catch {
    return raw;
  }
}

/**
 * Crawl and embed a URL.
 * 
 * This endpoint will crawl the URL and embed the content into the database of the genai service.
 * This will improve the quality of the course generation.
 * 
 * @param url The URL to crawl and embed
 * @returns The crawled and embedded data
 */
export async function crawlAndEmbedUrl(url: string): Promise<any> {
  if (!authToken) {
    throw { status: 401, message: 'No authentication token provided' };
  }

  const resp = await fetch(`${BASE_URL}/embed/url`, {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
      Authorization: `Bearer ${authToken}`,
    },
    body: JSON.stringify({ url }),
  });

  if (!resp.ok) {
    throw await parseErrorResponse(resp);
  }

  const raw = await resp.text();
  try {
    return JSON.parse(raw);
  } catch {
    return { raw };
  }
}