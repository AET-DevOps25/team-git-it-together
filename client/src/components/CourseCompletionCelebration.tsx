import React, { useEffect, useState } from 'react';
import { Card, CardContent } from '@/components/ui/card';
import { Button } from '@/components/ui/button';
import { Trophy, Star, CheckCircle, Sparkles, Award } from 'lucide-react';

interface CourseCompletionCelebrationProps {
  courseTitle: string;
  isVisible: boolean;
  onClose: () => void;
}

const CourseCompletionCelebration: React.FC<CourseCompletionCelebrationProps> = ({
  courseTitle,
  isVisible,
  onClose
}) => {
  const [showConfetti, setShowConfetti] = useState(false);
  const [showFireworks, setShowFireworks] = useState(false);

  useEffect(() => {
    if (isVisible) {
      setShowConfetti(true);
      setShowFireworks(true);
    }
  }, [isVisible]);

  if (!isVisible) return null;

  return (
    <>
      {/* Overlay */}
      <div className="fixed inset-0 bg-black bg-opacity-50 z-50 flex items-center justify-center">
        {/* Confetti Animation */}
        {showConfetti && (
          <div className="absolute inset-0 pointer-events-none">
            {[...Array(50)].map((_, i) => (
              <div
                key={i}
                className="absolute animate-bounce"
                style={{
                  left: `${Math.random() * 100}%`,
                  top: `${Math.random() * 100}%`,
                  animationDelay: `${Math.random() * 2}s`,
                  animationDuration: `${1 + Math.random() * 2}s`,
                }}
              >
                <div
                  className="w-2 h-2 rounded-full"
                  style={{
                    backgroundColor: ['#FFD700', '#FF6B6B', '#4ECDC4', '#45B7D1', '#96CEB4'][Math.floor(Math.random() * 5)],
                    transform: `rotate(${Math.random() * 360}deg)`,
                  }}
                />
              </div>
            ))}
          </div>
        )}

        {/* Fireworks Animation */}
        {showFireworks && (
          <div className="absolute inset-0 pointer-events-none">
            {[...Array(8)].map((_, i) => (
              <div
                key={i}
                className="absolute"
                style={{
                  left: `${20 + (i * 10)}%`,
                  top: `${30 + (i * 20)}%`,
                }}
              >
                <div className="relative">
                  {[...Array(12)].map((_, j) => (
                    <div
                      key={j}
                      className="absolute w-1 h-1 bg-yellow-400 rounded-full animate-ping"
                      style={{
                        transform: `rotate(${j * 30}deg) translateY(-20px)`,
                        animationDelay: `${i * 0.2}s`,
                      }}
                    />
                  ))}
                </div>
              </div>
            ))}
          </div>
        )}

        {/* Celebration Card */}
        <Card className="relative z-10 w-full max-w-md mx-4 bg-gradient-to-br from-yellow-400 via-orange-500 to-red-500 text-white border-0 shadow-2xl">
          <CardContent className="p-8 text-center">
            {/* Trophy Icon with Animation */}
            <div className="mb-6 flex justify-center">
              <div className="relative">
                <Trophy className="h-16 w-16 text-white animate-bounce" />
                <Sparkles className="h-6 w-6 text-yellow-200 absolute -top-2 -right-2 animate-pulse" />
                <Sparkles className="h-4 w-4 text-yellow-200 absolute -bottom-1 -left-1 animate-pulse" style={{ animationDelay: '0.5s' }} />
              </div>
            </div>

            {/* Congratulations Text */}
            <h2 className="text-3xl font-bold mb-4 animate-pulse">
              🎉 Congratulations! 🎉
            </h2>
            
            <p className="text-lg mb-6 font-medium">
              You've successfully completed
            </p>
            
            <h3 className="text-xl font-bold mb-6 bg-white bg-opacity-20 rounded-lg p-3">
              "{courseTitle}"
            </h3>

            {/* Achievement Icons */}
            <div className="flex justify-center space-x-4 mb-6">
              <div className="flex flex-col items-center">
                <CheckCircle className="h-8 w-8 text-green-300 mb-1" />
                <span className="text-sm">Completed</span>
              </div>
              <div className="flex flex-col items-center">
                <Star className="h-8 w-8 text-yellow-300 mb-1" />
                <span className="text-sm">Achievement</span>
              </div>
              <div className="flex flex-col items-center">
                <Award className="h-8 w-8 text-orange-300 mb-1" />
                <span className="text-sm">Certificate</span>
              </div>
            </div>

            {/* Action Buttons */}
            <div className="space-y-3">
              <Button 
                onClick={onClose}
                className="w-full bg-white text-orange-600 hover:bg-gray-100 font-semibold"
              >
                Continue Learning
              </Button>
            </div>

            {/* Floating Stars */}
            <div className="absolute top-4 left-4">
              <Star className="h-4 w-4 text-yellow-200 animate-ping" />
            </div>
            <div className="absolute top-8 right-6">
              <Star className="h-3 w-3 text-yellow-200 animate-ping" style={{ animationDelay: '0.3s' }} />
            </div>
            <div className="absolute bottom-8 left-6">
              <Star className="h-3 w-3 text-yellow-200 animate-ping" style={{ animationDelay: '0.6s' }} />
            </div>
            <div className="absolute bottom-4 right-4">
              <Star className="h-4 w-4 text-yellow-200 animate-ping" style={{ animationDelay: '0.9s' }} />
            </div>
          </CardContent>
        </Card>
      </div>
    </>
  );
};

export default CourseCompletionCelebration; 