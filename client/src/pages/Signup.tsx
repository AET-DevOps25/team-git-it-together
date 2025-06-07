import validator from "validator";
import zxcvbn from "zxcvbn";
import React, { useState } from 'react';
import { Link } from 'react-router-dom';
import { Button } from '@/components/ui/button';
import { Input } from '@/components/ui/input';
import { Label } from '@/components/ui/label';
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from '@/components/ui/card';
import { BookOpen, Eye, EyeOff, UserPlus } from 'lucide-react';
import { APP_NAME } from '@/constants/app.ts';
import { useAuth } from '@/hooks/useAuth';
import { useToast } from '@/hooks/use-toast';

const Signup = () => {
  const [formData, setFormData] = useState({
    firstName: '',
    lastName: '',
    username: '',   // <-- new field
    email: '',
    password: '',
    confirmPassword: ''
  });
  const [isLoading, setIsLoading] = useState(false);
  const [showPassword, setShowPassword] = useState(false);
  const [showConfirmPassword, setShowConfirmPassword] = useState(false);

  const { register } = useAuth();
  const { toast } = useToast();

  const handleChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    setFormData({ ...formData, [e.target.name]: e.target.value });
  };

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    // 1. Required fields
    for (const [key, value] of Object.entries(formData)) {
      if (!value.trim()) {
        toast({
          title: "Validation Error",
          description: `The ${key} field is required.`,
          variant: "destructive"
        });
        return;
      }
    }
    // 2. Email format
    if (!validator.isEmail(formData.email.trim())) {
      toast({
        title: "Invalid Email",
        description: "Please enter a valid email address.",
        variant: "destructive"
      });
      return;
    }
    // 3. Username format
    if (!/^[a-zA-Z0-9_]{3,20}$/.test(formData.username.trim())) {
      toast({
        title: "Invalid Username",
        description: "Username must be 3-20 characters, letters/numbers/underscores only.",
        variant: "destructive"
      });
      return;
    }
    // 4. Password strength (zxcvbn)
    const passwordStrength = zxcvbn(formData.password);
    if (passwordStrength.score < 2) {
      toast({
        title: "Weak Password",
        description: passwordStrength.feedback.suggestions.join(" ") || "Please choose a stronger password.",
        variant: "destructive"
      });
      return;
    }
    // 5. Password match
    if (formData.password !== formData.confirmPassword) {
      toast({
        title: "Passwords do not match",
        description: "Please ensure both passwords are identical.",
        variant: "destructive"
      });
      return;
    }
    setIsLoading(true);
    try {
      await register({
        email: formData.email.trim(),
        username: formData.username.trim(),
        password: formData.password.trim(),
        firstName: formData.firstName.trim(),
        lastName: formData.lastName.trim(),
      });
      toast({
        // @ts-expect-error - The title should accept a ReactNode and is implemented correctly
        title: (
          <span className="flex items-center gap-2 text-green-700">
          <UserPlus className="w-5 h-5 text-green-500" />
          Account Created!
        </span>
        ),
        description: (
          <span className="text-green-600">
          Welcome to {APP_NAME}, {formData.firstName.trim()}! The journey of learning begins now.
        </span>
        ),
        variant: "success",
      });

    } catch (err: any) {
    let message = "Please check your input or try again later.";
    if (err && typeof err === "object" && "message" in err && typeof (err as any).message === "string") {
      message = (err as { message: string }).message;
    }
    toast({
      title: "Registration Failed",
      description:
        message === "username is already used"
          ? "Username is already in use. Please choose another one."
          : message,
      variant: "destructive"
    });
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
          <h1 className="text-3xl font-bold text-gray-900 mb-2">Join {APP_NAME}</h1>
          <p className="text-gray-600">Create your account to start learning</p>
        </div>

        <Card className="shadow-xl border-0">
          <CardHeader className="text-center">
            <CardTitle className="flex items-center justify-center space-x-2">
              <UserPlus className="h-5 w-5" />
              <span>Create Account</span>
            </CardTitle>
            <CardDescription>
              Enter your information to get started
            </CardDescription>
          </CardHeader>
          <CardContent>
            <form onSubmit={handleSubmit} className="space-y-4">
              <div className="grid grid-cols-2 gap-4">
                <div className="space-y-2">
                  <Label htmlFor="firstName">First Name</Label>
                  <Input
                    id="firstName"
                    name="firstName"
                    placeholder="John"
                    value={formData.firstName}
                    onChange={handleChange}
                    required
                    className="h-11"
                    disabled={isLoading}
                  />
                </div>
                <div className="space-y-2">
                  <Label htmlFor="lastName">Last Name</Label>
                  <Input
                    id="lastName"
                    name="lastName"
                    placeholder="Doe"
                    value={formData.lastName}
                    onChange={handleChange}
                    required
                    className="h-11"
                    disabled={isLoading}
                  />
                </div>
              </div>

              <div className="space-y-2">
                <Label htmlFor="username">Username</Label>
                <Input
                  id="username"
                  name="username"
                  placeholder="yourusername"
                  value={formData.username}
                  onChange={handleChange}
                  required
                  className="h-11"
                  disabled={isLoading}
                />
              </div>

              <div className="space-y-2">
                <Label htmlFor="email">Email</Label>
                <Input
                  id="email"
                  name="email"
                  type="email"
                  placeholder="you@example.com"
                  value={formData.email}
                  onChange={handleChange}
                  required
                  className="h-11"
                  disabled={isLoading}
                />
              </div>

              <div className="space-y-2">
                <Label htmlFor="password">Password</Label>
                <div className="relative">
                <Input
                  id="password"
                  name="password"
                  type={showPassword ? "text" : "password"}
                  placeholder="Create a password"
                  value={formData.password}
                  onChange={handleChange}
                  required
                  className="h-11"
                  disabled={isLoading}
                />
                <Button
                  type="button"
                  variant="ghost"
                  size="sm"
                  className="absolute right-0 top-0 h-full px-3 py-2 hover:bg-transparent"
                  onClick={() => setShowPassword(!showPassword)}
                >
                  {showPassword ? <Eye className="h-4 w-4" /> : <EyeOff className="h-4 w-4" />}
                </Button>
              </div>
              </div>

              <div className="space-y-2">
                <Label htmlFor="confirmPassword">Confirm Password</Label>
                <div className="relative">

                <Input
                  id="confirmPassword"
                  name="confirmPassword"
                  type={showConfirmPassword ? "text" : "password"}
                  placeholder="Confirm your password"
                  value={formData.confirmPassword}
                  onChange={handleChange}
                  required
                  className="h-11"
                  disabled={isLoading}
                />
                  <Button
                    type="button"
                    variant="ghost"
                    size="sm"
                    className="absolute right-0 top-0 h-full px-3 py-2 hover:bg-transparent"
                    onClick={() => setShowConfirmPassword(!showConfirmPassword)}
                  >
                    {showConfirmPassword ? <Eye className="h-4 w-4" /> : <EyeOff className="h-4 w-4" />}
                  </Button>
              </div>
              </div>

              <div className="text-sm">
                <label className="flex items-start space-x-2">
                  <input type="checkbox" className="mt-1 rounded border-gray-300" required disabled={isLoading} />
                  <span className="text-gray-600">
                    I agree to the{' '}
                    <Link to="/terms" className="text-blue-600 hover:text-blue-700">
                      Terms of Service
                    </Link>{' '}
                    and{' '}
                    <Link to="/privacy" className="text-blue-600 hover:text-blue-700">
                      Privacy Policy
                    </Link>
                  </span>
                </label>
              </div>

              <Button type="submit" className="w-full h-11 text-lg" disabled={isLoading}>
                {isLoading ? "Creating..." : "Create Account"}
              </Button>

              <div className="text-center text-sm text-gray-600">
                Already have an account?{' '}
                <Link to="/login" className="text-blue-600 hover:text-blue-700 font-medium">
                  Sign in
                </Link>
              </div>
            </form>
          </CardContent>
        </Card>
      </div>
    </div>
  );
};

export default Signup;
