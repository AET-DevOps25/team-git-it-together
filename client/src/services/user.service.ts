/**
 * This Service provide all user related operations
 */

import { API_BASE_URL } from '@/constants/app.ts';
import type {
  LoginPayload,
  RegisterPayload,
  UpdatePayload,
  UserLoginResponse,
  UserProfileResponse,
  UserRegisterResponse,
} from '@/types';
import { parseErrorResponse } from '@/utils/response.utils.ts';

const BASE_URL = `${API_BASE_URL}/users`;

// Holds the current JWT. Set via setAuthToken().
let authToken: string | null = null;

/**
 * Configure the “Authorization: Bearer <token>” header for future requests.
 * Pass `null` to clear it.
 */
export function setAuthToken(token: string | null) {
  authToken = token;
}

/**
 * Register a new user.
 * @param payload - The user registration payload
 * @returns The user registration response
 * @throws ApiError object { status: number, message: string } on 4xx/5xx
 */
export async function register(payload: RegisterPayload): Promise<UserRegisterResponse> {
  const resp = await fetch(`${BASE_URL}/register`, {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify(payload),
  });

  if (!resp.ok) {
    throw await parseErrorResponse(resp);
  }

  return await resp.json();
}

/**
 * Log in a user and receive a JWT token in the response.
 * @param payload - The user login payload
 * @returns The user login response
 * @throws ApiError object { status: number, message: string } on 4xx/5xx
 */
export async function login(payload: LoginPayload): Promise<UserLoginResponse> {
  const resp = await fetch(`${BASE_URL}/login`, {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify(payload),
  });

  if (!resp.ok) {
    throw await parseErrorResponse(resp);
  }

  return await resp.json();
}

/**
 * Fetch the profile for the currently authenticated user.
 * Requires that setAuthToken(token) has been called earlier.
 * @param userId  ID of the user whose profile to fetch
 * @throws ApiError object { status: number, message: string } on 4xx/5xx or if no token
 */
export async function getUserProfile(userId: string): Promise<UserProfileResponse> {
  if (!authToken) {
    throw { status: 401, message: 'No authentication token provided' };
  }

  const resp = await fetch(`${BASE_URL}/${userId}/profile`, {
    method: 'GET',
    headers: {
      Authorization: `Bearer ${authToken}`,
    },
  });

  if (!resp.ok) {
    throw await parseErrorResponse(resp);
  }

  return await resp.json();
}

/**
 * Update the profile for the currently authenticated user.
 * Requires that setAuthToken(token) has been called earlier.
 * @param userId  ID of the user to update
 * @param payload Fields to update
 * @throws ApiError object { status: number, message: string } on 4xx/5xx or if no token
 */
export async function updateUserProfile(
  userId: string,
  payload: UpdatePayload,
): Promise<UserProfileResponse> {
  if (!authToken) {
    throw { status: 401, message: 'No authentication token provided' };
  }

  const resp = await fetch(`${BASE_URL}/${userId}/profile`, {
    method: 'PUT',
    headers: {
      'Content-Type': 'application/json',
      Authorization: `Bearer ${authToken}`,
    },
    body: JSON.stringify(payload),
  });

  if (!resp.ok) {
    throw await parseErrorResponse(resp);
  }

  return await resp.json();
}

/**
 * Delete the currently authenticated user's account.
 * Requires that setAuthToken(token) has been called earlier.
 * @param userId  ID of the user to delete
 * @throws ApiError object { status: number, message: string } on 4xx/5xx or if no token
 */
export async function deleteUserAccount(userId: string): Promise<void> {
  if (!authToken) {
    throw { status: 401, message: 'No authentication token provided' };
  }

  const resp = await fetch(`${BASE_URL}/${userId}/profile`, {
    method: 'DELETE',
    headers: {
      Authorization: `Bearer ${authToken}`,
    },
  });

  if (!resp.ok) {
    throw await parseErrorResponse(resp);
  }
  // No content response expected on success
  if (resp.status !== 204) {
    throw { status: resp.status, message: 'Unexpected response from server' };
  }
  // If we reach here, the account was successfully deleted
  setAuthToken(null); // Clear the auth token
  return;
}
