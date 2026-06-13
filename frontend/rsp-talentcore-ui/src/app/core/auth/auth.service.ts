import { Injectable, signal } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { BehaviorSubject, Observable, catchError, finalize, map, of, shareReplay, switchMap, take, throwError, tap } from 'rxjs';
import { ConfigService } from '../../core/services/config.service';
import { TokenStorageService } from './token-storage.service';
import { LoginRequest, LoginResponse, RefreshTokenResponse, CurrentUserResponse } from './auth.models';
import { LoggingService } from '../services/logging.service';

@Injectable({ providedIn: 'root' })
export class AuthService {
  private accessToken$: BehaviorSubject<string | null>;
  private refreshInProgress$?: Observable<RefreshTokenResponse>;
  private lastActivity = Date.now();
  private idleTimeoutMs = 30 * 60 * 1000; // 30 minutes
  currentUser = signal<null | CurrentUserResponse>(null);

  constructor(
    private http: HttpClient,
    private cfg: ConfigService,
    private tokenStorage: TokenStorageService,
    private logger: LoggingService
  ) {
    this.accessToken$ = new BehaviorSubject<string | null>(this.tokenStorage.getAccessToken());
    this.startActivityMonitor();
    this.scheduleStoredSessionHydration();
  }

  private scheduleStoredSessionHydration(): void {
    if (typeof window === 'undefined') {
      return;
    }

    window.setTimeout(() => this.hydrateStoredSession(), 0);
  }

  private hydrateStoredSession(): void {
    const token = this.tokenStorage.getAccessToken();
    if (token) {
      if (this.tokenStorage.isTokenExpired(token)) {
        const refresh = this.tokenStorage.getRefreshToken();
        if (refresh) {
          this.refreshToken().pipe(
            switchMap(() => this.loadCurrentUser())
          ).subscribe({
            next: u => this.currentUser.set(u),
            error: () => this.logout(false)
          });
        } else {
          this.logout(false);
        }
      } else {
        this.loadCurrentUser().subscribe({ next: u => this.currentUser.set(u), error: () => {} });
      }
    }
  }

  isAuthenticated(): boolean {
    const accessToken = this.tokenStorage.getAccessToken();
    if (accessToken && !this.tokenStorage.isTokenExpired(accessToken)) {
      return true;
    }

    // Allow preserved session when a refresh token exists and access token is expired.
    return !!this.tokenStorage.getRefreshToken();
  }

  validateStoredSession() {
    const accessToken = this.tokenStorage.getAccessToken();

    if (accessToken && !this.tokenStorage.isTokenExpired(accessToken)) {
      const profile = this.currentUser();
      return (profile ? of(profile) : this.loadCurrentUser()).pipe(
        map(() => true),
        catchError(err => {
          if (this.isAuthFailure(err)) {
            this.logout(false);
            return of(false);
          }
          return of(true);
        }),
        take(1)
      );
    }

    if (!this.tokenStorage.getRefreshToken()) {
      this.logout(false);
      return of(false);
    }

    return this.refreshToken().pipe(
      switchMap(() => this.loadCurrentUser()),
      map(() => true),
      catchError(err => {
        if (this.isAuthFailure(err)) {
          this.logout(false);
          return of(false);
        }
        return of(false);
      }),
      take(1)
    );
  }

  private isAuthFailure(err: unknown): boolean {
    const status = (err as { status?: number } | null)?.status;
    return status === 401 || status === 403;
  }

  getAccessToken(): string | null {
    return this.tokenStorage.getAccessToken();
  }

  getAccessToken$() {
    return this.accessToken$.asObservable();
  }

  login(credentials: LoginRequest) {
    const url = `${this.cfg.get('apiBase')}/users/login`;
    return this.http.post<LoginResponse>(url, credentials).pipe(
      tap(res => {
        this.applyTokens({ accessToken: res.accessToken, refreshToken: res.refreshToken });
      }),
      catchError(err => {
        this.logger.error('Login failed', err);
        return throwError(() => err);
      })
    );
  }

  logout(notifyServer = true) {
    this.logger.info('Logging out user');
    const refreshToken = this.tokenStorage.getRefreshToken();

    if (notifyServer && refreshToken) {
      const url = `${this.cfg.get('apiBase')}/users/logout`;
      this.http.post<void>(url, { refreshToken }).pipe(
        catchError(err => {
          this.logger.error('Logout request failed', err);
          return of(void 0);
        }),
        take(1)
      ).subscribe();
    }

    this.clearTokens();
    this.currentUser.set(null);
  }

  private applyTokens(res: { accessToken: string; refreshToken?: string }) {
    this.tokenStorage.clear();
    this.tokenStorage.setAccessToken(res.accessToken);
    if (res.refreshToken) {
      this.tokenStorage.setRefreshToken(res.refreshToken);
    }
    this.accessToken$.next(res.accessToken);
  }

  private clearTokens() {
    this.tokenStorage.clear();
    this.accessToken$.next(null);
  }

  refreshToken() {
    // Prevent parallel refreshes using shared replay observable
    if (this.refreshInProgress$) return this.refreshInProgress$;

    const refresh = this.tokenStorage.getRefreshToken();
    if (!refresh) {
      this.logout(false);
      return throwError(() => new Error('No refresh token'));
    }

    const url = `${this.cfg.get('apiBase')}/users/refresh-token`;
    this.refreshInProgress$ = this.http.post<RefreshTokenResponse>(url, { refreshToken: refresh }).pipe(
      tap(res => this.applyTokens({ accessToken: res.accessToken, refreshToken: refresh })),
      catchError(err => {
        this.logger.error('Refresh token failed', err);
        this.logout(false);
        return throwError(() => err);
      }),
      finalize(() => {
        this.refreshInProgress$ = undefined;
      }),
      shareReplay({ bufferSize: 1, refCount: false })
    );

    return this.refreshInProgress$;
  }

  loadCurrentUser() {
    const url = `${this.cfg.get('apiBase')}/users/current`;
    return this.http.get<CurrentUserResponse>(url).pipe(
      tap(u => this.currentUser.set(u)),
      catchError(err => {
        this.logger.error('Load current user failed', err);
        return throwError(() => err);
      })
    );
  }

  getUserRoles(): string[] {
    const payload = this.tokenStorage.decodeToken(this.tokenStorage.getAccessToken());
    return payload?.roles || [];
  }

  getRefreshToken(): string | null {
    return this.tokenStorage.getRefreshToken();
  }

  private startActivityMonitor() {
    if (typeof window === 'undefined') {
      return;
    }

    const events = ['mousemove', 'keydown', 'scroll', 'touchstart'];
    events.forEach(ev => window.addEventListener(ev, () => this.lastActivity = Date.now()));
    // simple poll
    const check = () => {
      if (this.isAuthenticated() && Date.now() - this.lastActivity > this.idleTimeoutMs) {
        this.logger.info('Session expired due to inactivity');
        this.logout();
      }
      setTimeout(check, 60_000);
    };
    setTimeout(check, 60_000);
  }
}
