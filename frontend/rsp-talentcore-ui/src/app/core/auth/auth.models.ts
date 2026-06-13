export interface LoginRequest {
  // Swagger uses `email` as the login identifier
  email: string;
  password: string;
}

export interface LoginResponse {
  accessToken: string;
  refreshToken?: string;
  tokenType?: string;
}

export interface RefreshTokenRequest {
  refreshToken: string;
}

export interface RefreshTokenResponse {
  accessToken: string;
  tokenType?: string;
}

export interface CurrentUserResponse {
  email: string;
  roles: string[];
  firstName?: string;
  lastName?: string;
}

export interface ErrorResponse {
  timestamp?: string;
  status?: number;
  message?: string;
  [k: string]: any;
}

export interface JwtPayload {
  sub?: string;
  exp?: number;
  iat?: number;
  roles?: string[];
  [k: string]: any;
}
