// src/pages/Login.tsx
import React, { FormEvent, useEffect, useState } from 'react';
import { Link } from 'react-router-dom';
import { Button } from '@/components/ui/button';
import { Input } from '@/components/ui/input';
import { Label } from '@/components/ui/label';
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from '@/components/ui/card';
import { AlertCircle, BookOpen, CheckCircle2, HelpCircle, LogIn, WifiOff } from 'lucide-react';
import { useToast } from '@/hooks/use-toast';
import { APP_NAME } from '@/constants/app.ts';
import { useAuth } from '@/hooks/useAuth';
import { ApiError } from '@/types';

const Login = () => {
  const [identifier, setIdentifier] = useState<string>('');
  const [password, setPassword] = useState<string>('');
  const [rememberMe, setRememberMe] = useState<boolean>(false);
  const [isLoading, setIsLoading] = useState<boolean>(false);
  const { login } = useAuth();
  const { toast } = useToast();

  // Simple email regex to distinguish email vs. username
  const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;

  // Type guard for ApiError
  function isApiError(obj: unknown): obj is ApiError {
    return (
      typeof obj === 'object' &&
      obj !== null &&
      'status' in obj &&
      'message' in obj
    );
  }

  // On mount, check if we previously saved a "remembered identifier"
  useEffect(() => {
    const savedIdentifier = localStorage.getItem('rememberedIdentifier');
    if (savedIdentifier) {
      setIdentifier(savedIdentifier);
      setRememberMe(true);
    }
  }, []);

  const handleSubmit = async (e: FormEvent<HTMLFormElement>) => {
    e.preventDefault();

    // Basic front-end validation
    if (!identifier.trim() || !password.trim()) {
      let message = "";
      if (!identifier.trim() && !password.trim()) {
        message = "Both your username (or e-mail) and password are required.";
      } else if (!identifier.trim()) {
        message = "Please enter your username or e-mail.";
      } else {
        message = "Please enter your password.";
      }
      toast({
        // @ts-expect-error - The title should accept a ReactNode and is implemented correctly
        title: (
          <span className="flex items-center gap-2 text-red-700">
        <AlertCircle className="w-5 h-5 text-red-500" />
        Missing credentials
      </span>
        ),
        description: (
          <span className="text-red-700">
        {message}
      </span>
        ),
        variant: "error",
      });
      return;
    }

    setIsLoading(true);

    // Build login payload: exactly one of email/username must be a non-empty string
    const email = emailRegex.test(identifier) ? identifier.trim() : '';
    const username = email ? '' : identifier.trim();

    try {
      // AuthContext.login(...) will handle setting storage, token, user and navigation
      await login({ email, username, password: password.trim(), rememberMe });
      toast({
        // @ts-expect-error - The title should accept a ReactNode and is implemented correctly
        title: (
          <span className="flex items-center gap-2 text-green-700">
            <CheckCircle2 className="w-5 h-5 text-green-500" />
            Login Successful
          </span>
        ),
        description: (<span className="text-green-700">{`Welcome back, ${username}!`}</span>),
        variant: "success",
      });

    } catch (err: unknown) {
    if (isApiError(err)) {
      const { status, message } = err;
      toast({
        // @ts-expect-error - The title should accept a ReactNode and is implemented correctly
        title: (
          <span className="flex items-center gap-2 text-red-700">
          <AlertCircle className="w-5 h-5 text-red-500" />
            {status === 401 ? "Invalid Credentials" : "Login Failed"}
        </span>
        ),
        description: (
          <span className="text-red-700">{message}</span>
        ),
        variant: "error",
      });
    } else if (err instanceof Error) {
      toast({
        // @ts-expect-error - The title should accept a ReactNode and is implemented correctly
        title: (
          <span className="flex items-center gap-2 text-red-700">
          <WifiOff className="w-5 h-5 text-red-500" />
          Network Error
        </span>
        ),
        description: (
          <span className="text-red-700">{err.message}</span>
        ),
        variant: "error",
      });
    } else {
      toast({
        // @ts-expect-error - The title should accept a ReactNode and is implemented correctly
        title: (
          <span className="flex items-center gap-2 text-red-700">
          <HelpCircle className="w-5 h-5 text-red-500" />
          Unexpected Error
        </span>
        ),
        description: (
          <span className="text-red-700">Something went wrong. Please try again later.</span>
        ),
        variant: "error",
      });
    }
  } finally {
    setIsLoading(false);
  }
  };

  return (
    <div className="min-h-screen bg-gradient-to-br from-blue-50 to-indigo-100 flex items-center justify-center p-4">
      <div className="w-full max-w-md">
        <div className="text-center mb-8">
          <Link to="/" className="inline-flex items-center space-x-2 mb-4">
            <BookOpen className="h-8 w-8 text-blue-600" />
            <span className="text-2xl font-bold text-gray-900">{APP_NAME}</span>
          </Link>
          <h1 className="text-3xl font-bold text-gray-900 mb-2">Welcome Back</h1>
          <p className="text-gray-600">Sign in to continue your learning journey</p>
        </div>

        <Card className="shadow-xl border-0">
          <CardHeader className="text-center">
            <CardTitle className="flex items-center justify-center space-x-2">
              <LogIn className="h-5 w-5" />
              <span>Sign In</span>
            </CardTitle>
            <CardDescription>
              Demo: FoulenBenFoulen, foulen.benFoulen@mail.com, easyPassword123
            </CardDescription>
          </CardHeader>

          <CardContent>
            <form onSubmit={handleSubmit} className="space-y-4">
              {/* Username / Email Field */}
              <div className="space-y-2">
                <Label htmlFor="identifier">Username or Email</Label>
                <Input
                  id="identifier"
                  name="identifier"
                  type="text"
                  placeholder="Enter your username or email"
                  value={identifier}
                  onChange={(e) => setIdentifier(e.target.value)}
                  disabled={isLoading}
                  className="h-11"
                  autoComplete="username"
                  required
                />
              </div>

              {/* Password Field */}
              <div className="space-y-2">
                <Label htmlFor="password">Password</Label>
                <Input
                  id="password"
                  name="password"
                  type="password"
                  placeholder="Enter your password"
                  value={password}
                  onChange={(e) => setPassword(e.target.value)}
                  disabled={isLoading}
                  className="h-11"
                  autoComplete="current-password"
                  required
                />
              </div>

              {/* Remember Me & Forgot Password */}
              <div className="flex items-center justify-between text-sm">
                <label htmlFor="rememberMe" className="flex items-center space-x-2">
                  <input
                    id="rememberMe"
                    name="rememberMe"
                    type="checkbox"
                    className="rounded border-gray-300"
                    checked={rememberMe}
                    onChange={(e) => setRememberMe(e.target.checked)}
                    disabled={isLoading}
                  />
                  <span className="text-gray-600">Remember me</span>
                </label>

                <Link
                  to="/forgot-password"
                  className="text-blue-600 hover:text-blue-700"
                >
                  Forgot password?
                </Link>
              </div>

              {/* Submit Button */}
              <Button
                type="submit"
                className="w-full h-11 text-lg"
                disabled={isLoading}
              >
                {isLoading ? 'Signing In...' : 'Sign In'}
              </Button>

              {/* Sign-Up Link */}
              <div className="text-center text-sm text-gray-600">
                Don’t have an account?{' '}
                <Link
                  to="/signup"
                  className="text-blue-600 hover:text-blue-700 font-medium"
                >
                  Sign up
                </Link>
              </div>
            </form>
          </CardContent>
        </Card>
      </div>
    </div>
  );
};

export default Login;
