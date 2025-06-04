import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from '@/components/ui/card';
import { Button } from '@/components/ui/button';
import { Input } from '@/components/ui/input';
import { Label } from '@/components/ui/label';
import { Textarea } from '@/components/ui/textarea';
import { Avatar, AvatarFallback, AvatarImage } from '@/components/ui/avatar';
import { Badge } from '@/components/ui/badge';
import { Progress } from '@/components/ui/progress';
import { Tabs, TabsContent, TabsList, TabsTrigger } from '@/components/ui/tabs';
import { Tooltip, TooltipContent, TooltipTrigger } from '@/components/ui/tooltip';
import {
  User,
  Camera,
  Eye,
  EyeOff,
  BookOpen,
  Bookmark,
  Trophy,
  Target,
  Award,
  Star
} from 'lucide-react';
import Navbar from '@/components/Navbar';
import { useToast } from '@/hooks/use-toast';
import { CategoryPayload, UserProfileResponse, Level, LEVEL_TO_PERCENT } from '@/types';
import { useAuth } from '@/hooks/useAuth.ts';
import * as userService from '@/services/user.service.ts';

const Profile = () => {
  const { user: authUser } = useAuth(); // get current user and their id
  const [user, setUser] = useState<UserProfileResponse | null>(null);
  const [loading, setLoading] = useState(true);
  const [showPassword, setShowPassword] = useState(false);
  const [showConfirmPassword, setShowConfirmPassword] = useState(false);
  const [formData, setFormData] = useState({
    firstName: '',
    lastName: '',
    email: '',
    password: '',
    confirmPassword: '',
    bio: '',
    interests: [] as CategoryPayload[],
    profilePicture: ''
  });
  const navigate = useNavigate();
  const { toast } = useToast();

  useEffect(() => {
    if (!authUser) {
      navigate('/login');
      return;
    }
    (async () => {
      setLoading(true);
      try {
        const profile = await userService.getUserProfile(authUser.id);
        setUser(profile);
        setFormData({
          firstName: profile.firstName || '',
          lastName: profile.lastName || '',
          email: profile.email || '',
          password: '',
          confirmPassword: '',
          bio: profile.bio || '',
          interests: profile.interests || [],
          profilePicture: profile.profilePictureUrl || 'https://i.pravatar.cc/150?img=69'
        });
      } catch (e) {
        console.error("Error loading profile:", e);
        toast({
          title: "Profile Load Error",
          description: "Could not load your profile.",
          variant: "destructive"
        });
        navigate('/dashboard');
      } finally {
        setLoading(false);
      }
    })();
  }, [authUser, navigate, toast]);


  const handleInputChange = (field: string, value: string) => {
    setFormData(prev => ({
      ...prev,
      [field]: value
    }));
  };

  const handleSaveProfile = () => {
    if (formData.password && formData.password !== formData.confirmPassword) {
      toast({
        title: "Password Mismatch",
        description: "Password and confirm password do not match.",
        variant: "destructive",
      });
      return;
    }

    // Update user in localStorage
    const updatedUser = {
      ...user,
      name: `${formData.firstName} ${formData.lastName}`,
      email: formData.email,
      profilePicture: formData.profilePicture,
      bio: formData.bio,
      interests: formData.interests
    };

    localStorage.setItem('user', JSON.stringify(updatedUser));
    setUser(updatedUser);

    toast({
      title: "Profile Updated",
      description: "Your profile has been successfully updated.",
    });
  };

  const handleImageUpload = (event: React.ChangeEvent<HTMLInputElement>) => {
    const file = event.target.files?.[0];
    if (file) {
      const reader = new FileReader();
      reader.onload = (e) => {
        const result = e.target?.result as string;
        setFormData(prev => ({ ...prev, profilePicture: result }));
      };
      reader.readAsDataURL(file);
    }
  };
const interests = [
    { id: 1, name: "Web Development", description: "Building websites and web applications" },
    { id: 2, name: "Data Science", description: "Analyzing data to extract insights" },
    { id: 3, name: "Machine Learning", description: "Creating models that learn from data" },
    { id: 4, name: "UI/UX Design", description: "Designing user-friendly interfaces" },
    { id: 5, name: "DevOps", description: "Automating software development processes" }
  ];

  const skills = [
    { id: 3, name: "Python", description: "High-level programming language for general-purpose programming", level: 'ADVANCED', category: interests[1] },
    { id: 5, name: "UI/UX Design", description: "Designing user interfaces and user experiences", level: 'INTERMEDIATE', category: interests[3] }
  ];

  const skillsInProgress = [
    { id: 1, name: "React", description: "JavaScript library for building user interfaces", level: 'BEGINNER' , category: interests[0] },
    { id: 2, name: "Node.js", description: "JavaScript runtime for server-side development", level: 'INTERMEDIATE', category: interests[0] },
  ];

  const enrolledCourses = [
    { id: 1, title: "React Development", progress: 75, status: "In Progress" },
    { id: 2, title: "JavaScript Fundamentals", progress: 100, status: "Completed" },
    { id: 3, title: "UI/UX Design Principles", progress: 30, status: "In Progress" }
  ];

  const bookmarkedCourses = [
    { id: 4, title: "Advanced React Patterns", instructor: "John Smith" },
    { id: 5, title: "GraphQL Fundamentals", instructor: "Jane Wilson" },
    { id: 6, title: "TypeScript Mastery", instructor: "Bob Johnson" }
  ];

  const achievements = [
    { title: "First Course Completed", date: "2024-01-20", icon: Trophy },
    { title: "Week Streak", date: "2024-01-18", icon: Target },
    { title: "React Expert", date: "2024-01-15", icon: Award },
    { title: "JavaScript Master", date: "2024-01-10", icon: Star }
  ];

  if (loading) {
    return <div>Loading...</div>;
  }
  if (!user) return null;

  return (
    <div className="min-h-screen bg-gray-50">
      <Navbar />

      <div className="max-w-6xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
        <div className="mb-8">
          <h1 className="text-3xl font-bold text-gray-900 mb-2">Profile Settings</h1>
          <p className="text-gray-600">Manage your account settings and preferences.</p>
        </div>

        <Tabs defaultValue="personal" className="space-y-6">
          <TabsList className="grid w-full grid-cols-5">
            <TabsTrigger value="personal">Personal Info</TabsTrigger>
            <TabsTrigger value="skills">Skills</TabsTrigger>
            <TabsTrigger value="courses">Courses</TabsTrigger>
            <TabsTrigger value="bookmarks">Bookmarks</TabsTrigger>
            <TabsTrigger value="achievements">Achievements</TabsTrigger>
          </TabsList>

          <TabsContent value="personal" className="space-y-6">
            <div className="grid lg:grid-cols-3 gap-6">
              <Card className="lg:col-span-2">
                <CardHeader>
                  <CardTitle>Personal Information</CardTitle>
                  <CardDescription>Update your personal details and account settings.</CardDescription>
                </CardHeader>
                <CardContent className="space-y-4">
                  <div className="grid grid-cols-2 gap-4">
                    <div className="space-y-2">
                      <Label htmlFor="firstName">First Name</Label>
                      <Input
                        id="firstName"
                        value={formData.firstName}
                        onChange={(e) => handleInputChange('firstName', e.target.value)}
                        className="bg-gray-100"
                        readOnly
                      />
                    </div>
                    <div className="space-y-2">
                      <Label htmlFor="lastName">Last Name</Label>
                      <Input
                        id="lastName"
                        value={formData.lastName}
                        onChange={(e) => handleInputChange('lastName', e.target.value)}
                        className="bg-gray-100"
                        readOnly
                      />
                    </div>
                  </div>

                  <div className="space-y-2">
                    <Label htmlFor="email">Email</Label>
                    <Input
                      id="email"
                      type="email"
                      value={formData.email}
                      onChange={(e) => handleInputChange('email', e.target.value)}
                      className="bg-gray-100"
                      readOnly
                    />
                  </div>

                  <div className="grid grid-cols-2 gap-4">
                    <div className="space-y-2">
                      <Label htmlFor="password">New Password</Label>
                      <div className="relative">
                        <Input
                          id="password"
                          type={showPassword ? "text" : "password"}
                          value={formData.password}
                          onChange={(e) => handleInputChange('password', e.target.value)}
                          placeholder="Enter new password"
                        />
                        <Button
                          type="button"
                          variant="ghost"
                          size="sm"
                          className="absolute right-0 top-0 h-full px-3 py-2 hover:bg-transparent"
                          onClick={() => setShowPassword(!showPassword)}
                        >
                          {showPassword ? <EyeOff className="h-4 w-4" /> : <Eye className="h-4 w-4" />}
                        </Button>
                      </div>
                    </div>
                    <div className="space-y-2">
                      <Label htmlFor="confirmPassword">Confirm Password</Label>
                      <div className="relative">
                        <Input
                          id="confirmPassword"
                          type={showConfirmPassword ? "text" : "password"}
                          value={formData.confirmPassword}
                          onChange={(e) => handleInputChange('confirmPassword', e.target.value)}
                          placeholder="Confirm new password"
                        />
                        <Button
                          type="button"
                          variant="ghost"
                          size="sm"
                          className="absolute right-0 top-0 h-full px-3 py-2 hover:bg-transparent"
                          onClick={() => setShowConfirmPassword(!showConfirmPassword)}
                        >
                          {showConfirmPassword ? <EyeOff className="h-4 w-4" /> : <Eye className="h-4 w-4" />}
                        </Button>
                      </div>
                    </div>
                  </div>

                  <div className="space-y-2">
                    <Label htmlFor="bio">Bio</Label>
                    <Textarea
                      id="bio"
                      value={formData.bio}
                      onChange={(e) => handleInputChange('bio', e.target.value)}
                      placeholder={formData.bio || "Tell us about yourself..."}
                      rows={4}
                    />
                  </div>

                  <div className="flex flex-wrap gap-2">
                    {formData.interests.map((interest) => (
                      <Tooltip key={interest.id}>
                        <TooltipTrigger asChild>
                          <Badge variant="secondary">{interest.name}</Badge>
                        </TooltipTrigger>
                        {interest.description && (
                          <TooltipContent>{interest.description}</TooltipContent>
                        )}
                      </Tooltip>
                    ))}
                  </div>

                  <Button onClick={handleSaveProfile} className="w-full">
                    Save Changes
                  </Button>
                </CardContent>
              </Card>

              <Card>
                <CardHeader>
                  <CardTitle>Profile Picture</CardTitle>
                  <CardDescription>Upload a new profile picture.</CardDescription>
                </CardHeader>
                <CardContent className="space-y-4">
                  <div className="flex flex-col items-center space-y-4">
                    <Avatar className="w-32 h-32">
                      <AvatarImage src={formData.profilePicture} alt="Profile" />
                      <AvatarFallback>
                        <User className="w-16 h-16" />
                      </AvatarFallback>
                    </Avatar>

                    <div className="w-full">
                      <Label htmlFor="picture" className="cursor-pointer">
                        <div className="flex items-center justify-center space-x-2 w-full p-2 border-2 border-dashed border-gray-300 rounded-lg hover:border-gray-400 transition-colors">
                          <Camera className="w-5 h-5" />
                          <span>Upload Picture</span>
                        </div>
                        <Input
                          id="picture"
                          type="file"
                          accept="image/*"
                          onChange={handleImageUpload}
                          className="hidden"
                        />
                      </Label>
                    </div>
                  </div>
                </CardContent>
              </Card>
            </div>
          </TabsContent>

          <TabsContent value="skills" className="space-y-6">
            <div className="grid md:grid-cols-2 gap-6">
              <Card>
                <CardHeader>
                  <CardTitle>Skills in Progress</CardTitle>
                  <CardDescription>Skills you're currently learning</CardDescription>
                </CardHeader>
                <CardContent className="space-y-4">
                  {skillsInProgress.map((skill) => (
                    <div key={skill.name} className="space-y-2">
                      <div className="flex justify-between">
                        <span className="text-sm font-medium">{skill.name}</span>
                        <span className="text-sm text-gray-600">{skill.level}</span>
                      </div>
                      <Progress value={LEVEL_TO_PERCENT[skill.level]} className="h-2" />
                    </div>
                  ))}
                </CardContent>
              </Card>

              <Card>
                <CardHeader>
                  <CardTitle>Completed Skills</CardTitle>
                  <CardDescription>Skills you've mastered</CardDescription>
                </CardHeader>
                <CardContent className="space-y-4">
                  {skills.map((skill) => (
                    <div key={skill.name} className="flex items-center justify-between p-3 bg-green-50 rounded-lg">
                      <span className="font-medium text-green-800">{skill.name}</span>
                      <Badge className="bg-green-600">Mastered</Badge>
                    </div>
                  ))}
                </CardContent>
              </Card>
            </div>
          </TabsContent>

          <TabsContent value="courses" className="space-y-6">
            <Card>
              <CardHeader>
                <CardTitle>Enrolled Courses</CardTitle>
                <CardDescription>Courses you're currently taking or have completed</CardDescription>
              </CardHeader>
              <CardContent className="space-y-4">
                {enrolledCourses.map((course) => (
                  <div key={course.id} className="border rounded-lg p-4">
                    <div className="flex justify-between items-start mb-3">
                      <div>
                        <h3 className="font-semibold">{course.title}</h3>
                        <Badge variant={course.status === "Completed" ? "default" : "secondary"}>
                          {course.status}
                        </Badge>
                      </div>
                      <span className="text-sm text-gray-600">{course.progress}%</span>
                    </div>
                    <Progress value={course.progress} className="h-2" />
                  </div>
                ))}
              </CardContent>
            </Card>
          </TabsContent>

          <TabsContent value="bookmarks" className="space-y-6">
            <Card>
              <CardHeader>
                <CardTitle>Bookmarked Courses</CardTitle>
                <CardDescription>Courses you've saved for later</CardDescription>
              </CardHeader>
              <CardContent className="space-y-4">
                {bookmarkedCourses.map((course) => (
                  <div key={course.id} className="flex items-center justify-between p-4 border rounded-lg">
                    <div className="flex items-center space-x-3">
                      <Bookmark className="w-5 h-5 text-blue-600" />
                      <div>
                        <h3 className="font-medium">{course.title}</h3>
                        <p className="text-sm text-gray-600">by {course.instructor}</p>
                      </div>
                    </div>
                    <Button size="sm">Enroll</Button>
                  </div>
                ))}
              </CardContent>
            </Card>
          </TabsContent>

          <TabsContent value="achievements" className="space-y-6">
            <div className="grid md:grid-cols-2 lg:grid-cols-3 gap-6">
              {achievements.map((achievement, index) => {
                const Icon = achievement.icon;
                return (
                  <Card key={index} className="text-center">
                    <CardContent className="p-6">
                      <div className="w-16 h-16 bg-yellow-100 rounded-full flex items-center justify-center mx-auto mb-4">
                        <Icon className="h-8 w-8 text-yellow-600" />
                      </div>
                      <h3 className="font-semibold text-gray-900 mb-2">{achievement.title}</h3>
                      <p className="text-xs text-gray-500">{achievement.date}</p>
                    </CardContent>
                  </Card>
                );
              })}
            </div>
          </TabsContent>
        </Tabs>
      </div>
    </div>
  );
};

export default Profile;
