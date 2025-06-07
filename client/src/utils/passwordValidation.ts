import zxcvbn from 'zxcvbn';

export interface PasswordValidationResult {
  valid: boolean;
  errorType?: 'strength' | 'mismatch';
  message?: string;
  suggestions?: string[];
  score?: number;
}

export function validatePassword(
  password: string,
  confirmPassword: string,
  minScore: number = 2,
): PasswordValidationResult {
  // 1. Password strength
  const { score, feedback } = zxcvbn(password || '');
  if (score < minScore) {
    return {
      valid: false,
      errorType: 'strength',
      message: feedback.suggestions.join(' ') || 'Please choose a stronger password.',
      suggestions: feedback.suggestions,
      score,
    };
  }
  // 2. Password match
  if (password !== confirmPassword) {
    return {
      valid: false,
      errorType: 'mismatch',
      message: 'Passwords do not match. Please ensure both passwords are identical.',
      score,
    };
  }
  // 3. All good!
  return { valid: true, score };
}
