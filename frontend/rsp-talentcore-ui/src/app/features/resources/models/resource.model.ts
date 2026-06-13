export interface Resource {
  id?: number;
  employeeId: string;
  firstName: string;
  lastName: string;
  email: string;
  primarySkill: string;
  experienceYears: number;
  status: ResourceStatus;
  mobile: string;
}

export interface ResourceRequest {
  employeeId?: string;
  firstName?: string;
  lastName?: string;
  email?: string;
  mobile?: string;
  experienceYears?: number;
  primarySkill?: string;
  status?: ResourceStatus | string;
}

export interface ResourceFilter {
  employeeId?: string;
  firstName?: string;
  lastName?: string;
  email?: string;
  experienceYears?: number;
  primarySkill?: string;
  status?: ResourceStatus;
}

export enum ResourceStatus {
  ACTIVE = 'ACTIVE',
  INACTIVE = 'INACTIVE',
  BENCH = 'BENCH',
  ALLOCATED = 'ALLOCATED'
}

export const RESOURCE_STATUS_LABELS: Record<ResourceStatus, string> = {
  [ResourceStatus.ACTIVE]: 'Active',
  [ResourceStatus.INACTIVE]: 'Inactive',
  [ResourceStatus.BENCH]: 'Bench',
  [ResourceStatus.ALLOCATED]: 'Allocated'
};

export const ALL_RESOURCE_STATUS = Object.values(ResourceStatus);
