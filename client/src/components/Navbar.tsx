import React, { useState } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { Button } from '@/components/ui/button';
import { Avatar, AvatarFallback, AvatarImage } from '@/components/ui/avatar';
import {
  DropdownMenu,
  DropdownMenuContent,
  DropdownMenuItem,
  DropdownMenuSeparator,
  DropdownMenuTrigger,
} from '@/components/ui/dropdown-menu';
import { BookOpen, LogOut, Menu, X, User, Settings } from 'lucide-react';
import { APP_NAME } from '@/constants/app.ts';
import { useAuth } from '@/hooks/useAuth';
import { useToast } from '@/hooks/use-toast.ts';

const Navbar = () => {
  const [isMenuOpen, setIsMenuOpen] = useState(false);
  const { user, logout } = useAuth();
  const { toast } = useToast();
  const navigate = useNavigate();

  const fullName =
    user && user.firstName && user.lastName
      ? `${user.firstName} ${user.lastName}`
      : user?.username || 'User';

  const profileInitial =
    user?.firstName?.charAt(0)?.toUpperCase() || user?.username?.charAt(0)?.toUpperCase() || 'U';

  const handleLogout = () => {
    toast({
      // @ts-expect-error - The title should accept a ReactNode and is implemented correctly
      title: (
        <span className="flex items-center gap-2 text-green-700">
          <LogOut className="h-5 w-5 text-green-500" />
          Logged out
        </span>
      ),
      description: (
        <span className="text-green-700">You have been successfully logged out. See you soon!</span>
      ),
      variant: 'success',
    });
    logout();
  };

  return (
    <nav className="sticky top-0 z-50 border-b border-gray-100 bg-white/80 backdrop-blur-md">
      <div className="mx-auto max-w-7xl px-4 sm:px-6 lg:px-8">
        <div className="flex h-16 items-center justify-between">
          <Link to="/" className="flex items-center space-x-2">
            <BookOpen className="h-8 w-8 text-blue-600" />
            <span className="text-xl font-bold text-gray-900">{APP_NAME}</span>
          </Link>

          {/* Desktop Navigation */}
          <div className="hidden items-center space-x-8 md:flex">
            <Link to="/" className="text-gray-700 transition-colors hover:text-blue-600">
              Home
            </Link>
            <Link to="/courses" className="text-gray-700 transition-colors hover:text-blue-600">
              Courses
            </Link>
            {user && (
              <Link to="/dashboard" className="text-gray-700 transition-colors hover:text-blue-600">
                Dashboard
              </Link>
            )}
            <Link to="/about" className="text-gray-700 transition-colors hover:text-blue-600">
              About
            </Link>

            {/* Profile/Logout Menu */}
            {user ? (
              <DropdownMenu>
                <DropdownMenuTrigger asChild>
                  <Button variant="ghost" className="relative h-8 w-8 rounded-full">
                    <Avatar className="h-8 w-8">
                      <AvatarImage src={user.profilePictureUrl} alt={fullName} />
                      <AvatarFallback>{profileInitial}</AvatarFallback>
                    </Avatar>
                  </Button>
                </DropdownMenuTrigger>
                <DropdownMenuContent className="w-56" align="end" forceMount>
                  <div className="flex items-center gap-2 p-2">
                    <div className="flex flex-col leading-none">
                      <p className="font-medium">{fullName}</p>
                      <p className="w-[200px] truncate text-sm text-muted-foreground">
                        {user.username}
                      </p>
                    </div>
                  </div>
                  <DropdownMenuSeparator />
                  <DropdownMenuItem onClick={() => navigate('/profile')}>
                    <User className="mr-2 h-4 w-4" />
                    <span>Profile</span>
                  </DropdownMenuItem>
                  <DropdownMenuItem onClick={() => navigate('/dashboard')}>
                    <Settings className="mr-2 h-4 w-4" />
                    <span>Dashboard</span>
                  </DropdownMenuItem>
                  <DropdownMenuSeparator />
                  <DropdownMenuItem onClick={handleLogout}>
                    <LogOut className="mr-2 h-4 w-4" />
                    <span>Log out</span>
                  </DropdownMenuItem>
                </DropdownMenuContent>
              </DropdownMenu>
            ) : (
              <div className="flex items-center space-x-4">
                <Link to="/login">
                  <Button variant="outline" size="sm">
                    Login
                  </Button>
                </Link>
                <Link to="/signup">
                  <Button size="sm">Get Started</Button>
                </Link>
              </div>
            )}
          </div>

          {/* Mobile menu button */}
          <div className="md:hidden">
            <Button variant="ghost" size="sm" onClick={() => setIsMenuOpen(!isMenuOpen)}>
              {isMenuOpen ? <X className="h-5 w-5" /> : <Menu className="h-5 w-5" />}
            </Button>
          </div>
        </div>

        {/* Mobile Navigation */}
        {isMenuOpen && (
          <div className="border-t border-gray-100 py-4 md:hidden">
            <div className="flex flex-col space-y-4">
              <Link to="/" className="text-gray-700 transition-colors hover:text-blue-600">
                Home
              </Link>
              <Link to="/courses" className="text-gray-700 transition-colors hover:text-blue-600">
                Courses
              </Link>
              {user && (
                <Link
                  to="/dashboard"
                  className="text-gray-700 transition-colors hover:text-blue-600"
                >
                  Dashboard
                </Link>
              )}
              <Link to="/about" className="text-gray-700 transition-colors hover:text-blue-600">
                About
              </Link>
              <div className="flex flex-col space-y-2 pt-4">
                {user ? (
                  <>
                    <Link to="/profile">
                      <Button variant="outline" size="sm" className="w-full">
                        Profile
                      </Button>
                    </Link>
                    <Button
                      variant="outline"
                      size="sm"
                      className="w-full"
                      onClick={() => {
                        handleLogout();
                        setIsMenuOpen(false);
                      }}
                    >
                      Logout
                    </Button>
                  </>
                ) : (
                  <>
                    <Link to="/login">
                      <Button variant="outline" size="sm" className="w-full">
                        Login
                      </Button>
                    </Link>
                    <Link to="/signup">
                      <Button size="sm" className="w-full">
                        Get Started
                      </Button>
                    </Link>
                  </>
                )}
              </div>
            </div>
          </div>
        )}
      </div>
    </nav>
  );
};

export default Navbar;
