export interface ApiError {
  status: number;
  error: string;
  message: string;
  path: string;
  timestamp?: string;
  details?: string[];
}
