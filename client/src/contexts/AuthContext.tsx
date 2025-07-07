// src/contexts/AuthContext.tsx
import { createContext } from 'react';
import type { UserLoginResponse } from '@/types';

// Extended user type that includes profile data
interface ExtendedUserData extends UserLoginResponse {
  bookmarkedCourseIds?: string[];
}

interface AuthContextType {
  user: ExtendedUserData | null;
  token: string | null;
  loading: boolean;
  /**
   * Attempts to log in.
   * The `opts` object must include exactly one of `email` or `username`, plus `password`, plus `rememberMe`.
   * Internally, `rememberMe` is used to decide whether to use localStorage (persistent) or sessionStorage.
   */
  login: (opts: {
    email?: string;
    username?: string;
    password: string;
    rememberMe: boolean;
  }) => Promise<UserLoginResponse>;
  logout: () => void;
  register: (opts: {
    firstName: string;
    lastName: string;
    username: string;
    email: string;
    password: string;
  }) => Promise<UserLoginResponse>;
  /**
   * Updates the user's bookmarked courses in the context and storage.
   * @param courseId The ID of the course to bookmark/unbookmark
   * @param isBookmarked Whether to add (true) or remove (false) the bookmark
   */
  updateUserBookmarks: (courseId: string, isBookmarked: boolean) => void;
}

export const AuthContext = createContext<AuthContextType>({
  user: null,
  token: null,
  loading: true,
  login: async () => {
    throw new Error('Login function not implemented. Make sure to wrap your app in AuthProvider.');
  },
  register: async () => {
    throw new Error('Register function not implemented. Make sure to wrap your app in AuthProvider.');
  },
  logout: () => {},
  updateUserBookmarks: () => {
    throw new Error('updateUserBookmarks function not implemented. Make sure to wrap your app in AuthProvider.');
  },
});
