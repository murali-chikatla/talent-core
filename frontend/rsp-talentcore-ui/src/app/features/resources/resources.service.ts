import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable, catchError, map, throwError } from 'rxjs';
import { ConfigService } from '../../core/services/config.service';
import { LoggingService } from '../../core/services/logging.service';
import { Page } from '../users/models/pagination.model';
import { Resource, ResourceFilter, ResourceRequest } from './models/resource.model';

export interface ResourceSearchParams {
  page?: number;
  size?: number;
  sortBy?: string;
  sortDirection?: 'ASC' | 'DESC';
  filters?: ResourceFilter;
}

@Injectable({ providedIn: 'root' })
export class ResourcesService {
  constructor(private http: HttpClient, private cfg: ConfigService, private logger: LoggingService) {}

  search(params: ResourceSearchParams): Observable<Page<Resource>> {
    const url = `${this.cfg.get('apiBase')}/resources/search`;
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
        } as Page<Resource>;
      }),
      catchError(err => {
        this.logger.error('Resource search failed', err);
        return throwError(() => err);
      })
    );
  }

  getById(id: number): Observable<Resource> {
    const url = `${this.cfg.get('apiBase')}/resources/${id}`;
    return this.http.get<Resource>(url).pipe(
      catchError(err => {
        this.logger.error(`Get resource ${id} failed`, err);
        return throwError(() => err);
      })
    );
  }

  create(request: ResourceRequest): Observable<Resource> {
    const url = `${this.cfg.get('apiBase')}/resources`;
    return this.http.post<Resource>(url, request).pipe(
      catchError(err => {
        this.logger.error('Create resource failed', err);
        return throwError(() => err);
      })
    );
  }

  update(id: number, request: ResourceRequest): Observable<Resource> {
    const url = `${this.cfg.get('apiBase')}/resources/${id}`;
    return this.http.put<Resource>(url, request).pipe(
      catchError(err => {
        this.logger.error(`Update resource ${id} failed`, err);
        return throwError(() => err);
      })
    );
  }

  delete(id: number): Observable<void> {
    const url = `${this.cfg.get('apiBase')}/resources/${id}`;
    return this.http.delete<void>(url).pipe(
      catchError(err => {
        this.logger.error(`Delete resource ${id} failed`, err);
        return throwError(() => err);
      })
    );
  }
}
