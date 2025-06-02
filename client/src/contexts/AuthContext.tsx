// src/contexts/AuthContext.tsx
import React, { createContext, ReactNode, useContext, useEffect, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import * as userService from '@/services/user.service.ts';
import type { LoginPayload, UserLoginResponse } from '@/types';

interface AuthContextType {
  user: UserLoginResponse | null;
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
  }) => Promise<void>;
  logout: () => void;
}

const AuthContext = createContext<AuthContextType>({
  user: null,
  token: null,
  loading: true,
  login: async () => {
  },
  logout: () => {
  },
});

export function useAuth() {
  return useContext(AuthContext);
}

export function AuthProvider({ children }: { children: ReactNode }) {
  const [user, setUser] = useState<UserLoginResponse | null>(null);
  const [token, setToken] = useState<string | null>(null);
  const [loading, setLoading] = useState<boolean>(true);
  const navigate = useNavigate();

  // Helper: persist token + user into chosen storage (localStorage if rememberMe, else sessionStorage)
  function persistAuth(
    jwtToken: string,
    userData: UserLoginResponse,
    rememberMe: boolean,
  ) {
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

    setToken(jwtToken);
    setUser(userData);

  }

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

  const logout = () => {
    localStorage.removeItem('token');
    localStorage.removeItem('user');
    sessionStorage.removeItem('token');
    sessionStorage.removeItem('user');
    localStorage.removeItem('rememberedIdentifier');

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
  }, []); // <--- make sure you have []

  return (
    <AuthContext.Provider value={{ user, token, loading, login, logout }}>
      {children}
    </AuthContext.Provider>
  );
}
