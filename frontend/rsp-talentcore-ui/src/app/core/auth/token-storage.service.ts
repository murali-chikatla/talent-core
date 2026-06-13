import { Injectable } from '@angular/core';
import { JwtPayload } from './auth.models';

const ACCESS_KEY = 'rc_access_token_v1';
const REFRESH_KEY = 'rc_refresh_token_v1';

@Injectable({ providedIn: 'root' })
export class TokenStorageService {
  private getStorage(name: 'localStorage' | 'sessionStorage'): Storage | null {
    if (typeof window === 'undefined') return null;
    try {
      return window[name] ?? null;
    } catch {
      return null;
    }
  }

  private get writeStorage(): Storage | null {
    return this.getStorage('localStorage') ?? this.getStorage('sessionStorage');
  }

  private get readStorages(): Storage[] {
    return [this.getStorage('localStorage'), this.getStorage('sessionStorage')]
      .filter((storage): storage is Storage => !!storage);
  }

  setAccessToken(token: string) {
    try { this.writeStorage?.setItem(ACCESS_KEY, token); } catch { /* fallback */ }
  }

  setRefreshToken(token: string) {
    try { this.writeStorage?.setItem(REFRESH_KEY, token); } catch { /* fallback */ }
  }

  getAccessToken(): string | null {
    return this.getStoredValue(ACCESS_KEY);
  }

  getRefreshToken(): string | null {
    return this.getStoredValue(REFRESH_KEY);
  }

  clear(): void {
    this.readStorages.forEach(storage => {
      try {
        storage.removeItem(ACCESS_KEY);
        storage.removeItem(REFRESH_KEY);
      } catch {}
    });
  }

  decodeToken(token: string | null): JwtPayload | null {
    if (!token) return null;
    try {
      const parts = token.split('.');
      if (parts.length < 2) return null;
      if (typeof atob !== 'function') return null;
      const base64 = parts[1].replace(/-/g, '+').replace(/_/g, '/');
      const padded = base64.padEnd(Math.ceil(base64.length / 4) * 4, '=');
      const payload = JSON.parse(atob(padded));
      return payload as JwtPayload;
    } catch {
      return null;
    }
  }

  isTokenExpired(token?: string | null): boolean {
    const t = token ?? this.getAccessToken();
    if (!t) return true;
    const payload = this.decodeToken(t);
    if (!payload) return true;
    // exp is seconds since epoch per JWT spec
    const exp = (payload as any).exp;
    if (!exp) return false; // no exp claim -> treat as non-expiring
    try {
      return Date.now() > Number(exp) * 1000;
    } catch {
      return true;
    }
  }

  private getStoredValue(key: string): string | null {
    for (const storage of this.readStorages) {
      try {
        const value = storage.getItem(key);
        if (value && value !== 'undefined' && value !== 'null') return value;
        if (value === 'undefined' || value === 'null') {
          storage.removeItem(key);
        }
      } catch {}
    }
    return null;
  }
}
