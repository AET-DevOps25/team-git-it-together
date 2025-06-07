export interface UserLoginResponse {
  id: string;
  firstName: string;
  lastName: string;
  username: string;
  email: string;
  profilePictureUrl?: string;
  jwtToken: string;
}
