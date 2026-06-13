export interface User {
  userId: number;
  employeeCode: string;
  firstName: string;
  lastName: string;
  email: string;
  active: boolean;
  roles: string[];
  title?: string;
  department?: string;
  mobile?: string;
  createdAt?: string;
  updatedAt?: string;
  lastLogin?: string;
  // legacy fields for compatibility
  id?: string;
  username?: string;
  fullName?: string;
  status?: 'active' | 'inactive' | 'pending';
}

export interface UserFilter {
  firstName?: string;
  lastName?: string;
  email?: string;
  active?: boolean;
  roleCode?: string;
}

export interface UserRequest {
  employeeCode: string;
  firstName: string;
  lastName?: string;
  email: string;
  password: string;
}

export interface AssignRoleRequest {
  userId: number;
  roleCodes: string[];
}

export interface ChangePasswordRequest {
  currentPassword: string;
  newPassword: string;
  confirmPassword: string;
}

export enum UserStatus {
  ACTIVE = 'active',
  INACTIVE = 'inactive',
  PENDING = 'pending'
}

export const USER_STATUS_LABELS: Record<UserStatus, string> = {
  [UserStatus.ACTIVE]: 'Active',
  [UserStatus.INACTIVE]: 'Inactive',
  [UserStatus.PENDING]: 'Pending'
};

export const USER_STATUS_COLORS: Record<UserStatus, string> = {
  [UserStatus.ACTIVE]: '#128a3d',
  [UserStatus.INACTIVE]: '#6b7280',
  [UserStatus.PENDING]: '#f6b345'
};
