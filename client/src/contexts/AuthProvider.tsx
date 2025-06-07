import React, { ReactNode, useEffect, useState } from 'react';
import type { LoginPayload, UserLoginResponse } from '@/types';
import { useNavigate } from 'react-router-dom';
import * as userService from '@/services/user.service.ts';
import { AuthContext } from '@/contexts/AuthContext.tsx';

export function AuthProvider({ children }: { children: ReactNode }) {
  const [user, setUser] = useState<UserLoginResponse | null>(null);
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
    setToken(jwtToken);
    setUser(userData);
  }

  // Login function that accepts either email or username, plus password and rememberMe flag
  const login = async (opts: {
    email?: string;
    username?: string;
    password: string;
    rememberMe: boolean;
  }) => {
    // Extract rememberMe separately; build a proper LoginPayload for userService.login
    const { email = '', username = '', password, rememberMe } = opts;
    const payload: LoginPayload = { email, username, password };

    // Attempt login
    const response = await userService.login(payload);
    // response.jwtToken must exist, and response carries whatever user fields your backend returns
    persistAuth(response.jwtToken, response, rememberMe);
    navigate('/dashboard');
  };

  // Register function to register a new user
  const register = async (opts: {
    firstName: string;
    lastName: string;
    username: string;
    email: string;
    password: string;
  }) => {
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
      console.log('Registration successful:', response);
      //For now, we login the user immediately after registration - Later you might want to redirect to a confirmation page
      const loginPayload: LoginPayload = {
        email: response.email,
        username: response.username,
        password, // Use the same password for login
      };
      await login({
        ...loginPayload,
        rememberMe: true,
      });
    }
  };

  const logout = () => {
    localStorage.removeItem('token');
    localStorage.removeItem('user');
    sessionStorage.removeItem('token');
    sessionStorage.removeItem('user');
    localStorage.removeItem('rememberedIdentifier');
    userService.setAuthToken(null); // Clear the auth token in userService

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
        const parsedUser: UserLoginResponse = JSON.parse(savedUserString);
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

  return (
    <AuthContext.Provider value={{ user, token, loading, login, logout, register }}>
      {children}
    </AuthContext.Provider>
  );
}
