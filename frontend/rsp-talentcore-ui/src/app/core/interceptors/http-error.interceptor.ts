import { HttpInterceptorFn, HttpErrorResponse } from '@angular/common/http';
import { inject } from '@angular/core';
import { throwError } from 'rxjs';
import { catchError } from 'rxjs/operators';
import { LoggingService } from '../services/logging.service';

export const httpErrorInterceptor: HttpInterceptorFn = (req, next) => {
  const logger = inject(LoggingService);
  
  return next(req).pipe(
    catchError((err: HttpErrorResponse) => {
      logger.error('HTTP error', { url: req.url, status: err.status, error: err.error });
      return throwError(() => err);
    })
  );
};
