import React from 'react';
import { Navigate } from 'react-router-dom';
import { useAuth } from '@/hooks/useAuth';

/**
 * Wrap any route you only want *authenticated* users to see.
 * If `loading` is still true, show a brief “Loading…” placeholder.
 * If `!user`, redirect to /login.
 * Otherwise, render the children.
 */
export const RequireAuth: React.FC<{ children: React.ReactNode }> = ({ children }) => {
  const { user, loading } = useAuth();

  if (loading) {
    return (
      <div className="flex h-screen items-center justify-center">
        Loading…
      </div>
    );
  }

  if (!user) {
    return <Navigate to="/login" replace />;
  }

  return <>{children}</>;
};

/**
 * Wrap any route you only want *unauthenticated* (guest) users to see—
 * e.g. /login or /signup. If `loading`, show “Loading…”. If `user` exists,
 * redirect to /dashboard. Otherwise, render children.
 */
export const RequireGuest: React.FC<{ children: React.ReactNode }> = ({ children }) => {
  const { user, loading } = useAuth();

  if (loading) {
    return (
      <div className="flex h-screen items-center justify-center">
        Loading…
      </div>
    );
  }

  if (user) {
    return <Navigate to="/dashboard" replace />;
  }

  return <>{children}</>;
};
