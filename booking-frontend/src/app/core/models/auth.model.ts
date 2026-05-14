export interface LoginRequest {
  email: string;
  password: string;
}

export interface User {
  id: string;
  name: string;
  email: string;
  rol: 'ADMIN' | 'USER' | 'OWNER';
}

export interface AuthResponse {
  accessToken: string;
  refreshToken: string;
  user: User;
}
