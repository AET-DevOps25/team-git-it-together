import { Toaster } from '@/components/ui/toaster';
import { Toaster as Sonner } from '@/components/ui/sonner';
import { TooltipProvider } from '@/components/ui/tooltip';
import { QueryClient, QueryClientProvider } from '@tanstack/react-query';
import { BrowserRouter, Route, Routes } from 'react-router-dom';

import Index from './pages/Index';
import Login from './pages/Login';
import Signup from './pages/Signup';
import Dashboard from './pages/Dashboard';
import NotFound from './pages/NotFound';
import Courses from '@/pages/Courses';

import { AuthProvider } from '@/contexts/AuthProvider';
import { RequireAuth, RequireGuest } from '@/components/RouteGuards';
import Profile from '@/pages/Profile';

const queryClient = new QueryClient();

const AppRoutes = () => (
  <Routes>
    {/* PUBLIC */}
    <Route path="/" element={<Index />} />

    <Route
      path="/login"
      element={
        <RequireGuest>
          <Login />
        </RequireGuest>
      }
    />

    <Route
      path="/signup"
      element={
        <RequireGuest>
          <Signup />
        </RequireGuest>
      }
    />

    {/* PROTECTED */}
    <Route
      path="/dashboard"
      element={
        <RequireAuth>
          <Dashboard />
        </RequireAuth>
      }
    />

    <Route
      path="/courses"
      element={
        <RequireAuth>
          <Courses />
        </RequireAuth>
      }
    />

    <Route
      path="/profile"
      element={
        <RequireAuth>
          <Profile />
        </RequireAuth>
      }
    />

    {/* CATCH-ALL */}
    <Route path="*" element={<NotFound />} />
  </Routes>
);

const App = () => (
  <QueryClientProvider client={queryClient}>
    <TooltipProvider>
      <Toaster />
      <Sonner />
      <BrowserRouter>
        <AuthProvider>
          <AppRoutes />
        </AuthProvider>
      </BrowserRouter>
    </TooltipProvider>
  </QueryClientProvider>
);

export default App;
