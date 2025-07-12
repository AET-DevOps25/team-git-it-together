import React, { ReactNode, useEffect, useState, useCallback } from 'react';
import type { LoginPayload, UserLoginResponse } from '@/types';
import { useNavigate } from 'react-router-dom';
import * as userService from '@/services/user.service';
import * as courseService from '@/services/course.service';
import * as dashboardService from '@/services/dashboard.service';
import * as aiChatService from '@/services/aiChat.service';
import { AuthContext } from '@/contexts/AuthContext';

// Extended user type that includes profile data
interface ExtendedUserData extends UserLoginResponse {
  bookmarkedCourseIds?: string[];
}

export function AuthProvider({ children }: { children: ReactNode }) {
  const [user, setUser] = useState<ExtendedUserData | null>(null);
  const [token, setToken] = useState<string | null>(null);
  const [loading, setLoading] = useState<boolean>(true);
  const navigate = useNavigate();

  // Helper: persist token + user into chosen storage (localStorage if rememberMe, else sessionStorage)
  function persistAuth(jwtToken: string, userData: UserLoginResponse, rememberMe: boolean) {
    const storage = rememberMe ? localStorage : sessionStorage;
    storage.setItem('token', jwtToken);
    storage.setItem('user', JSON.stringify(userData));

    if (rememberMe) {
      const identifier = userData.email ?? userData.username;
      if (identifier) {
        localStorage.setItem('rememberedIdentifier', identifier);
      }
    } else {
      localStorage.removeItem('rememberedIdentifier');
    }

    userService.setAuthToken(jwtToken);
    courseService.setAuthToken(jwtToken);
    dashboardService.setAuthToken(jwtToken);
    aiChatService.setAuthToken(jwtToken);
    setToken(jwtToken);
    setUser(userData);
  }

  // Login function that accepts either email or username, plus password and rememberMe flag
  const login = async (opts: {
    email?: string;
    username?: string;
    password: string;
    rememberMe: boolean;
  }): Promise<UserLoginResponse> => {
    // Extract rememberMe separately; build a proper LoginPayload for userService.login
    const { email = '', username = '', password, rememberMe } = opts;
    const payload: LoginPayload = { email, username, password };

    // Attempt login
    const response = await userService.login(payload);
    
    // Fetch user profile to get bookmarked courses
    try {
      const userProfile = await userService.getUserProfile(response.id);
      const bookmarkedCourseIds = userProfile.bookmarkedCourses?.map(course => course.id) || [];
      const extendedUserData: ExtendedUserData = {
        ...response,
        bookmarkedCourseIds
      };
      persistAuth(response.jwtToken, extendedUserData, rememberMe);
    } catch (error) {
      console.error('Failed to fetch user profile:', error);
      // Fallback to basic user data without bookmarked courses
      persistAuth(response.jwtToken, response, rememberMe);
    }
    
    // Check if there's a redirect path in the location state
    const location = window.location;
    const urlParams = new URLSearchParams(location.search);
    const redirectTo = urlParams.get('redirect') || '/dashboard';
    navigate(redirectTo);
    return response;
  };

  // Register function to register a new user
  const register = async (opts: {
    firstName: string;
    lastName: string;
    username: string;
    email: string;
    password: string;
  }): Promise<UserLoginResponse> => {
    const { firstName, lastName, username, email, password } = opts;
    // Call the userService.register method
    const response = await userService.register({
      firstName,
      lastName,
      username,
      email,
      password,
    });
    // if response.id exists, registration was successful
    if (response && response.id) {

      //For now, we login the user immediately after registration - Later you might want to redirect to a confirmation page
      const loginPayload: LoginPayload = {
        email: response.email,
        username: response.username,
        password, // Use the same password for login
      };
      // The login function returns the user payload, so we can return it from here.
      return login({
        ...loginPayload,
        rememberMe: true, // Auto-remember on register
      });
    }
    // This path should not be reached if userService.register throws on failure, but as a safeguard:
    throw new Error('Registration failed: server did not return a valid user.');
  };

  const logout = () => {
    localStorage.removeItem('token');
    localStorage.removeItem('user');
    sessionStorage.removeItem('token');
    sessionStorage.removeItem('user');
    localStorage.removeItem('rememberedIdentifier');
    userService.setAuthToken(null); // Clear the auth token in userService
    courseService.setAuthToken(null); // Clear the auth token in courseService
    dashboardService.setAuthToken(null); // Clear the auth token in dashboardService

    setToken(null);
    setUser(null);
    navigate('/');
  };

  // On mount, try to rehydrate from localStorage OR sessionStorage
  useEffect(() => {
    const savedToken = localStorage.getItem('token') || sessionStorage.getItem('token');
    const savedUserString = localStorage.getItem('user') || sessionStorage.getItem('user');
    if (savedToken && savedUserString) {
      try {
        userService.setAuthToken(savedToken);
        courseService.setAuthToken(savedToken);
        dashboardService.setAuthToken(savedToken);
        aiChatService.setAuthToken(savedToken);
        const parsedUser: ExtendedUserData = JSON.parse(savedUserString);
        setToken(savedToken);
        setUser(parsedUser);
      } catch {
        // Only clear storage if parse fails, not on every mount!
        localStorage.removeItem('token');
        localStorage.removeItem('user');
        sessionStorage.removeItem('token');
        sessionStorage.removeItem('user');
        setToken(null);
        setUser(null);
      }
    }
    setLoading(false);
  }, []);

  // Function to update user's bookmarked courses
  const updateUserBookmarks = useCallback((courseId: string, isBookmarked: boolean) => {
    if (!user) return;
    
    // Check if the bookmark status is actually changing to avoid unnecessary updates
    const currentBookmarks = user.bookmarkedCourseIds || [];
    const isCurrentlyBookmarked = currentBookmarks.includes(courseId);
    
    if (isCurrentlyBookmarked === isBookmarked) {
      // No change needed
      return;
    }
    
    const updatedBookmarks = isBookmarked 
      ? [...currentBookmarks, courseId]
      : currentBookmarks.filter(id => id !== courseId);
    
    const updatedUser = {
      ...user,
      bookmarkedCourseIds: updatedBookmarks
    };
    
    setUser(updatedUser);
    
    // Update storage
    const storage = localStorage.getItem('token') ? localStorage : sessionStorage;
    storage.setItem('user', JSON.stringify(updatedUser));
  }, [user]);

  return (
    <AuthContext.Provider value={{ user, token, loading, login, logout, register, updateUserBookmarks }}>
      {children}
    </AuthContext.Provider>
  );
}
