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
import CourseDetail from '@/pages/CourseDetail';
import LessonPage from '@/pages/LessonPage';
import AiCenter from '@/pages/AiCenter';
import About from '@/pages/About';
import PrivacyPolicy from '@/pages/PrivacyPolicy';
import TermsOfService from '@/pages/TermsOfService';
import CookiePolicy from '@/pages/CookiePolicy';

import { AuthProvider } from '@/contexts/AuthProvider';
import { RequireAny, RequireAuth, RequireGuest } from '@/components/RouteGuards';
import Profile from '@/pages/Profile';
import ScrollToTop from '@/components/ScrollToTop';

const queryClient = new QueryClient();

const AppRoutes = () => (
  <Routes>
    {/* ANY */}
    <Route path="/" element={<RequireAny><Index /></RequireAny>}/>

    <Route path="/courses" element={<RequireAny><Courses /></RequireAny>}/>

    <Route path="/about" element={<RequireAny><About /></RequireAny>} />
    <Route path="/privacy" element={<RequireAny><PrivacyPolicy /></RequireAny>} />
    <Route path="/terms" element={<RequireAny><TermsOfService /></RequireAny>} />
    <Route path="/cookies" element={<RequireAny><CookiePolicy /></RequireAny>} />

    {/* PUBLIC ONLY */}
    <Route path="/login" element={<RequireGuest><Login /></RequireGuest>} />
    <Route path="/signup" element={<RequireGuest><Signup /></RequireGuest>} />

    {/* PROTECTED */}
    <Route path="/dashboard" element={<RequireAuth><Dashboard /></RequireAuth>} />
    <Route path="/ai-center" element={<RequireAuth><AiCenter /></RequireAuth>} />
    <Route path="/profile" element={<RequireAuth><Profile /></RequireAuth>} />  

    <Route path="/courses/:courseId" element={<RequireAuth><CourseDetail /></RequireAuth>} />
    <Route path="/courses/:courseId/lessons/:lessonId" element={<RequireAuth><LessonPage /></RequireAuth>} />

    {/* CATCH-ALL */}
    <Route path="*" element={<RequireAny><NotFound /></RequireAny>} />
  </Routes>
);

const App = () => (
  <QueryClientProvider client={queryClient}>
    <TooltipProvider>
      <Toaster />
      <Sonner />
      <BrowserRouter>
        <ScrollToTop />
        <AuthProvider>
          <AppRoutes />
        </AuthProvider>
      </BrowserRouter>
    </TooltipProvider>
  </QueryClientProvider>
);

export default App;
