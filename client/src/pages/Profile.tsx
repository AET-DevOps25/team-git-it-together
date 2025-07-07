import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from '@/components/ui/card';
import { Button } from '@/components/ui/button';
import { Input } from '@/components/ui/input';
import { Label } from '@/components/ui/label';
import { Textarea } from '@/components/ui/textarea';
import { Avatar, AvatarFallback, AvatarImage } from '@/components/ui/avatar';
import { Badge } from '@/components/ui/badge';
import { Tabs, TabsContent, TabsList, TabsTrigger } from '@/components/ui/tabs';
import {
  User,
  Camera,
  Eye,
  EyeOff,
  Trash,
} from 'lucide-react';
import Navbar from '@/components/Navbar';
import { useToast } from '@/hooks/use-toast';
import {
  UserProfileResponse,
  UpdatePayload,
} from '@/types';
import { useAuth } from '@/hooks/useAuth.ts';
import _ from 'lodash';
import * as userService from '@/services/user.service.ts';
import { validatePassword } from '@/utils/passwordValidation.ts';
import { Switch } from '@/components/ui/switch.tsx';
import { PasswordStrengthBar } from '@/components/ui';

import { ConfirmDeletionDialog } from '@/components/ConfirmDeletionDialog.tsx';

const Profile = () => {
  const { user: authUser } = useAuth(); // get current user and their id
  const [user, setUser] = useState<UserProfileResponse | null>(null);
  const [loading, setLoading] = useState(true);
  const [showPassword, setShowPassword] = useState(false);
  const [showConfirmPassword, setShowConfirmPassword] = useState(false);
  const [changePassword, setChangePassword] = useState(false);
  const [deleteDialogOpen, setDeleteDialogOpen] = useState(false);

  const [formData, setFormData] = useState({
    firstName: '',
    lastName: '',
    email: '',
    password: '',
    confirmPassword: '',
    bio: '',
    profilePictureUrl: '',
  });
  const navigate = useNavigate();
  const { toast } = useToast();
  const { logout } = useAuth();

  useEffect(() => {
    if (!authUser) {
      toast({
        title: 'You have been logged out',
        description: 'Please log in again to access your profile.',
        variant: 'destructive',
      });
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
          profilePictureUrl: profile.profilePictureUrl
        });
      } catch (e) {
        console.error('Error loading profile:', e);
        toast({
          title: e.message,
          description: 'Error Loading your Profile. Please contact support if this issue persists.',
          variant: 'destructive',
        });
        navigate('/dashboard');
      } finally {
        setLoading(false);
      }
    })();
  }, [authUser, navigate, toast]);

  const handleInputChange = (field: string, value: string) => {
    setFormData((prev) => ({
      ...prev,
      [field]: value,
    }));
  };

  const handleSaveProfile = () => {
    // 1) check if the user is logged in
    if (!authUser) {
      toast({
        title: 'You are not logged in',
        description: 'Please log in to update your profile.',
        variant: 'destructive',
      });
      navigate('/login');
      return;
    }

    // Define the updated user data
    const update: UpdatePayload = {};
    // 2) validate form data
    // 2.1) Check if pasword is being changed
    if (changePassword) {
      const result = validatePassword(formData.password, formData.confirmPassword);
      if (!result.valid) {
        toast({
          title: result.errorType === 'mismatch' ? 'Passwords do not match' : 'Weak Password',
          description: result.message,
          variant: 'destructive',
        });
        return;
      }
      update.password = formData.password;
    }
    // 2.2) Check if bio have changed
    if (formData.bio !== user.bio) {
      if (formData.bio.trim().length > 500) {
        toast({
          title: 'Bio is too long',
          description: 'Please limit your bio to 500 characters.',
          variant: 'destructive',
        });
        return;
      } else {
        formData.bio = formData.bio.trim();
        update.bio = formData.bio;
      }
    }

    // 2.3) Check if profile picture has changed
    if (formData.profilePictureUrl !== user.profilePictureUrl) {
      // Currently, we are not validating the image size or type as we use url
      update.profilePictureUrl = formData.profilePictureUrl;
    }


    // 2.4) Check if we have any update
    if (_.isEmpty(update)) {
      toast({
        title: 'No changes detected',
        description: 'Please make some changes before saving.',
        variant: 'info',
      });
      return;
    }
    console.log('Updating user profile with data:', update);
    // 3) Update the user profile
    userService
      .updateUserProfile(authUser.id, update)
      .then((updatedUser) => {
        setUser(updatedUser);
        setFormData((prev) => ({
          ...prev,
          firstName: updatedUser.firstName || '',
          lastName: updatedUser.lastName || '',
          email: updatedUser.email || '',
          bio: updatedUser.bio || '',
          profilePictureUrl: updatedUser.profilePictureUrl,
        }));
        toast({
          title: 'Profile Updated',
          description: 'Your profile has been successfully updated.',
          variant: 'success',
        });
      })
      .catch((error) => {
        console.error('Error updating profile:', error);
        toast({
          title: 'Update Failed',
          description: error.message || 'An error occurred while updating your profile.',
          variant: 'destructive',
        });
      });
  };

  const handleDeleteAccount = async () => {
    if (!authUser) {
      toast({
        title: 'You are not logged in',
        description: 'Please log in to delete your account.',
        variant: 'destructive',
      });
      navigate('/login');
      return;
    }

    try {
      await userService.deleteUserAccount(authUser.id);
      toast({
        title: 'Account Deleted',
        description: 'Your account has been successfully deleted.',
        variant: 'success',
      });
      // log out the user
      logout();
    } catch (error) {
      console.error('Error deleting account:', error);
      toast({
        title: 'Deletion Failed',
        description: error.message || 'An error occurred while deleting your account.',
        variant: 'destructive',
      });
    }
  };

  const handleImageUpload = (event: React.ChangeEvent<HTMLInputElement>) => {
    const file = event.target.files?.[0];
    if (file) {
      const reader = new FileReader();
      reader.onload = (e) => {
        const result = e.target?.result as string;
        setFormData((prev) => ({ ...prev, profilePicture: result }));
      };
      reader.readAsDataURL(file);
    }
  };

  if (loading) {
    return <div>Loading...</div>;
  }
  if (!user) return null;

  return (
    <div className="min-h-screen bg-gray-50">
      <Navbar />

      <div className="mx-auto max-w-6xl px-4 py-8 sm:px-6 lg:px-8">
        <div className="mb-8">
          <h1 className="mb-2 text-3xl font-bold text-gray-900">Profile Settings</h1>
          <p className="text-gray-600">Manage your account settings and preferences.</p>
        </div>

        <Tabs defaultValue="personal" className="space-y-6">
          <TabsList className="grid w-full grid-cols-1">
            <TabsTrigger value="personal">Personal Info</TabsTrigger>
          </TabsList>
          {/* Personal Information Tab */}
          <TabsContent value="personal" className="space-y-6">
            <div className="grid gap-6 lg:grid-cols-3">
              <Card className="lg:col-span-2">
                <CardHeader>
                  <CardTitle>Personal Information</CardTitle>
                  <CardDescription>
                    Update your personal details and account settings.
                  </CardDescription>
                </CardHeader>
                <CardContent className="space-y-4">
                  <div className="grid grid-cols-2 gap-4">
                    <div className="space-y-2">
                      <Label htmlFor="firstName">First Name</Label>
                      <Input
                        id="firstName"
                        value={formData.firstName || ''}
                        onChange={(e) => handleInputChange('firstName', e.target.value)}
                        className="bg-gray-100"
                        readOnly
                        disabled={true}
                      />
                    </div>
                    <div className="space-y-2">
                      <Label htmlFor="lastName">Last Name</Label>
                      <Input
                        id="lastName"
                        value={formData.lastName || ''}
                        onChange={(e) => handleInputChange('lastName', e.target.value)}
                        className="bg-gray-100"
                        readOnly
                        disabled={true}
                      />
                    </div>
                  </div>

                  <div className="space-y-2">
                    <Label htmlFor="email">Email</Label>
                    <Input
                      id="email"
                      type="email"
                      value={formData.email || ''}
                      onChange={(e) => handleInputChange('email', e.target.value)}
                      className="bg-gray-100"
                      readOnly
                      disabled={true}
                    />
                  </div>

                  <div className="space-y-2">
                    <Label htmlFor="userId">User ID</Label>
                    <Input
                      id="userId"
                      type="text"
                      value={user?.id || authUser?.id || ''}
                      className="bg-gray-100 font-mono text-sm"
                      readOnly
                      disabled={true}
                    />
                  </div>

                  <div className="mb-4 flex items-center gap-3">
                    <Switch
                      id="change-password-switch"
                      checked={changePassword}
                      onCheckedChange={setChangePassword}
                    />
                    <Label htmlFor="change-password-switch" className="cursor-pointer">
                      Change Password
                    </Label>
                  </div>

                  <div className="grid grid-cols-2 gap-4">
                    <div className="space-y-2">
                      <Label htmlFor="password">New Password</Label>
                      <div className="relative">
                        <Input
                          id="password"
                          type={showPassword ? 'text' : 'password'}
                          value={formData.password}
                          onChange={(e) => handleInputChange('password', e.target.value)}
                          placeholder="Enter new password"
                          disabled={!changePassword}
                        />
                        <Button
                          type="button"
                          variant="ghost"
                          size="sm"
                          className="absolute right-0 top-0 h-full px-3 py-2 hover:bg-transparent"
                          onClick={() => setShowPassword(!showPassword)}
                          tabIndex={-1}
                          disabled={!changePassword}
                        >
                          {showPassword ? (
                            <Eye className="h-4 w-4" />
                          ) : (
                            <EyeOff className="h-4 w-4" />
                          )}
                        </Button>
                      </div>
                      {/* Password Strength Bar */}
                      {changePassword && <PasswordStrengthBar password={formData.password} />}
                    </div>
                    <div className="space-y-2">
                      <Label htmlFor="confirmPassword">Confirm Password</Label>
                      <div className="relative">
                        <Input
                          id="confirmPassword"
                          type={showConfirmPassword ? 'text' : 'password'}
                          value={formData.confirmPassword}
                          onChange={(e) => handleInputChange('confirmPassword', e.target.value)}
                          placeholder="Confirm new password"
                          disabled={!changePassword}
                        />
                        <Button
                          type="button"
                          variant="ghost"
                          size="sm"
                          className="absolute right-0 top-0 h-full px-3 py-2 hover:bg-transparent"
                          onClick={() => setShowConfirmPassword(!showConfirmPassword)}
                          tabIndex={-1}
                          disabled={!changePassword}
                        >
                          {showConfirmPassword ? (
                            <EyeOff className="h-4 w-4" />
                          ) : (
                            <Eye className="h-4 w-4" />
                          )}
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
                      placeholder={formData.bio || 'Tell us more about yourself...'}
                      rows={4}
                    />
                  </div>

                  <div className="space-y-4">
                    <div>
                      <Label className="text-sm font-medium">Current Skills</Label>
                      <div className="mt-2 flex flex-wrap gap-2">
                        {(user?.skills || []).map((skill, index) => (
                          <Badge key={index} variant="secondary" className="bg-green-100 text-green-800 border-green-200">
                            {skill}
                          </Badge>
                        ))}
                        {(!user?.skills || user.skills.length === 0) && (
                          <p className="text-sm text-gray-500">No mastered skills yet</p>
                        )}
                      </div>
                    </div>
                    <div>
                      <Label className="text-sm font-medium">Skills in Progress</Label>
                      <div className="mt-2 flex flex-wrap gap-2">
                        {(user?.skillsInProgress || []).map((skill, index) => (
                          <Badge key={index} variant="secondary" className="bg-yellow-100 text-yellow-800 border-yellow-200">
                            {skill}
                          </Badge>
                        ))}
                        {(!user?.skillsInProgress || user.skillsInProgress.length === 0) && (
                          <p className="text-sm text-gray-500">No skills in progress</p>
                        )}
                      </div>
                    </div>
                  </div>

                  <Button onClick={handleSaveProfile} className="w-full">
                    Save Changes
                  </Button>
                </CardContent>
              </Card>
              {/* Profile Picture Card */}
              <Card>
                <CardHeader>
                  <CardTitle>Profile Picture</CardTitle>
                  <CardDescription>Upload a new profile picture.</CardDescription>
                </CardHeader>
                <CardContent className="space-y-4">
                  <div className="flex flex-col items-center space-y-4">
                    <Avatar className="h-32 w-32">
                      <AvatarImage src={formData.profilePictureUrl} alt="Profile" />
                      <AvatarFallback>
                        <User className="h-16 w-16" />
                      </AvatarFallback>
                    </Avatar>
                    <div className="w-full">
                      <Label htmlFor="picture" className="cursor-pointer">
                        <div className="flex w-full items-center justify-center space-x-2 rounded-lg border-2 border-dashed border-gray-300 p-2 transition-colors hover:border-gray-400">
                          <Camera className="h-5 w-5" />
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
            {/* Delete Account Button */}
            <div className="mt-4 flex justify-center">
              <Button
                variant="destructive"
                size="sm"
                className="gap-2"
                onClick={() => setDeleteDialogOpen(true)}
              >
                <Trash className="h-4 w-4" />
                Delete Account
              </Button>
            </div>

            <ConfirmDeletionDialog
              open={deleteDialogOpen}
              onOpenChange={setDeleteDialogOpen}
              onConfirm={handleDeleteAccount}
            />
          </TabsContent>
        </Tabs>
      </div>
    </div>
  );
};

export default Profile;
