import { HttpInterceptorFn } from '@angular/common/http';
import { inject } from '@angular/core';
import { catchError, switchMap, throwError } from 'rxjs';
import { AuthService } from '../auth/auth.service';

export const jwtInterceptor: HttpInterceptorFn = (req, next) => {
  const auth = inject(AuthService);
  
  // Do not attach to authentication endpoints
  const skipAuthPaths = ['/api/users/login', '/api/users/refresh-token'];
  const skipRefreshRetryPaths = [...skipAuthPaths, '/api/users/logout'];
  if (skipAuthPaths.some(p => req.url.includes(p))) {
    return next(req);
  }

  const token = auth.getAccessToken();
  const authReq = token ? req.clone({ setHeaders: { Authorization: `Bearer ${token}` } }) : req;

  return next(authReq).pipe(
    catchError(err => {
      if (
        err?.status === 401 &&
        auth.getRefreshToken() &&
        !skipRefreshRetryPaths.some(p => req.url.includes(p))
      ) {
        // attempt refresh via AuthService; it handles logout on failure and prevents parallel refreshes
        return auth.refreshToken().pipe(
          switchMap(() => {
            const t = auth.getAccessToken();
            const retryReq = t ? req.clone({ setHeaders: { Authorization: `Bearer ${t}` } }) : req;
            return next(retryReq);
          })
        );
      }
      return throwError(() => err);
    })
  );
};
