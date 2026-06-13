import { CanActivateFn } from '@angular/router';
import { inject } from '@angular/core';
import { AuthService } from '../auth/auth.service';

export const roleGuard = (requiredRoles: string[]): CanActivateFn => {
  return () => {
    const auth = inject(AuthService);
    if (!auth.isAuthenticated()) return false;
    const roles = auth.getUserRoles();
    return requiredRoles.some(r => roles.includes(r));
  };
};
