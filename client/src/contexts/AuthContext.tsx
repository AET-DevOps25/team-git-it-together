// src/contexts/AuthContext.tsx
import { createContext } from 'react';
import type { UserLoginResponse } from '@/types';

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

export const AuthContext = createContext<AuthContextType>({
  user: null,
  token: null,
  loading: true,
  login: async () => {
  },
  logout: () => {
  },
});

