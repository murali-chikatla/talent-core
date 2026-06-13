import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable, catchError, map, throwError } from 'rxjs';
import { ConfigService } from '../../core/services/config.service';
import { LoggingService } from '../../core/services/logging.service';
import { AssignRoleRequest, ChangePasswordRequest, User, UserFilter, UserRequest } from './models/user.model';
import { Page } from './models/pagination.model';

export interface UserSearchParams {
  page?: number;
  size?: number;
  sortBy?: string;
  sortDirection?: 'ASC' | 'DESC';
  filters?: UserFilter;
}

@Injectable({ providedIn: 'root' })
export class UsersService {
  constructor(private http: HttpClient, private cfg: ConfigService, private logger: LoggingService) {}

  search(params: UserSearchParams): Observable<Page<User>> {
    const url = `${this.cfg.get('apiBase')}/users/search`;
    let httpParams = new HttpParams()
      .set('page', String(params.page ?? 0))
      .set('size', String(params.size ?? 10));

    if (params.sortBy) {
      httpParams = httpParams.set('sortBy', params.sortBy);
    }
    if (params.sortDirection) {
      httpParams = httpParams.set('sortDirection', params.sortDirection);
    }
    if (params.filters) {
      Object.entries(params.filters).forEach(([key, value]) => {
        if (value !== undefined && value !== null && String(value) !== '') {
          httpParams = httpParams.set(key, String(value));
        }
      });
    }

    return this.http.get<any>(url, { params: httpParams }).pipe(
      map(res => {
        return {
          items: res?.content ?? [],
          total: res?.totalElements ?? 0,
          page: res?.page ?? params.page ?? 0,
          size: res?.size ?? params.size ?? 10
        } as Page<User>;
      }),
      catchError(err => {
        this.logger.error('Users search failed', err);
        return throwError(() => err);
      })
    );
  }

  register(user: UserRequest): Observable<User> {
    const url = `${this.cfg.get('apiBase')}/users/register`;
    return this.http.post<User>(url, user).pipe(
      catchError(err => {
        this.logger.error('Register user failed', err);
        return throwError(() => err);
      })
    );
  }

  assignRoles(request: AssignRoleRequest): Observable<void> {
    const url = `${this.cfg.get('apiBase')}/users/roles`;
    return this.http.post<void>(url, request).pipe(
      catchError(err => {
        this.logger.error('Assign roles failed', err);
        return throwError(() => err);
      })
    );
  }

  changePassword(request: ChangePasswordRequest): Observable<void> {
    const url = `${this.cfg.get('apiBase')}/users/change-password`;
    return this.http.post<void>(url, request).pipe(
      catchError(err => {
        this.logger.error('Change password failed', err);
        return throwError(() => err);
      })
    );
  }
}
