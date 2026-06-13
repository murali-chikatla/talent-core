export enum UserRole {
  SYSTEM_ADMIN = 'SYSTEM_ADMIN',
  ORGANIZATION_ADMIN = 'ORGANIZATION_ADMIN',
  RESOURCE_MANAGER = 'RESOURCE_MANAGER',
  TEAM_MANAGER = 'TEAM_MANAGER',
  EMPLOYEE = 'EMPLOYEE',
  VENDOR_MANAGER = 'VENDOR_MANAGER',
  VENDOR_RECRUITER = 'VENDOR_RECRUITER'
}

export const ROLE_LABELS: Record<UserRole, string> = {
  [UserRole.SYSTEM_ADMIN]: 'System Admin',
  [UserRole.ORGANIZATION_ADMIN]: 'Organization Admin',
  [UserRole.RESOURCE_MANAGER]: 'Resource Manager',
  [UserRole.TEAM_MANAGER]: 'Team Manager',
  [UserRole.EMPLOYEE]: 'Employee',
  [UserRole.VENDOR_MANAGER]: 'Vendor Manager',
  [UserRole.VENDOR_RECRUITER]: 'Vendor Recruiter'
};

export const ALL_ROLES = Object.values(UserRole);
