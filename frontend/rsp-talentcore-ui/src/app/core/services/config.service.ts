import { Injectable } from '@angular/core';
import { environment } from '../../../environments/environment';

export interface AppConfig {
  apiBase: string;
  environment: 'development' | 'production';
}

@Injectable({ providedIn: 'root' })
export class ConfigService {
  private config: AppConfig;

  constructor() {
    // runtime configuration can be attached to window.__APP_CONFIG__ by server
    const win = typeof window !== 'undefined' ? (window as any) : null;
    this.config = win?.__APP_CONFIG__ || {
      apiBase: environment.apiBase,
      environment: environment.production ? 'production' : 'development'
    };
  }

  get<T extends keyof AppConfig>(key: T): AppConfig[T] {
    return this.config[key];
  }
}
