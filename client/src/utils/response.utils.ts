import type { ApiError } from '@/types';

export async function parseErrorResponse(response: Response): Promise<ApiError> {
  try {
    return await response.json();
  } catch {
    return {
      status: response.status,
      error: response.statusText,
      message: 'Unknown error',
      path: '',
    };
  }
}