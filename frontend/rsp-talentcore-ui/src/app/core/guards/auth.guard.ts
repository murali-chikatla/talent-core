import { inject } from '@angular/core';
import { CanMatchFn, CanActivateFn, Router } from '@angular/router';
import { map } from 'rxjs';
import { AuthService } from '../auth/auth.service';

export const authGuard: CanActivateFn = () => {
  const auth = inject(AuthService);
  const router = inject(Router);
  if (typeof window === 'undefined') return true;

  return auth.validateStoredSession().pipe(
    map(valid => valid ? true : router.createUrlTree(['/auth/login']))
  );
};

export const authMatchGuard: CanMatchFn = () => {
  const auth = inject(AuthService);
  const router = inject(Router);
  if (typeof window === 'undefined') return true;

  return auth.validateStoredSession().pipe(
    map(valid => valid ? true : router.createUrlTree(['/auth/login']))
  );
};
