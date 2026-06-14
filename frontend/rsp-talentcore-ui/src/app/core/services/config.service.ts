import { Injectable } from '@angular/core';
import { environment } from '../../../environments/environment';

export interface AppConfig {
  apiBase: string;
  environment: 'development' | 'production';
}

@Injectable({ providedIn: 'root' })
export class ConfigService {
  private config: AppConfig = {
    apiBase: environment.apiBase,
    environment: environment.production ? 'production' : 'development'
  };

  async load(): Promise<void> {
    const win = typeof window !== 'undefined' ? (window as any) : null;
    const windowConfig = this.normalize(win?.__APP_CONFIG__);

    if (windowConfig) {
      this.config = windowConfig;
      return;
    }

    if (typeof fetch === 'undefined') {
      return;
    }

    try {
      const response = await fetch('/app-config.json', {
        cache: 'no-store',
        headers: { Accept: 'application/json' }
      });

      if (!response.ok) {
        return;
      }

      const runtimeConfig = this.normalize(await response.json());
      if (runtimeConfig) {
        this.config = runtimeConfig;
      }
    } catch {
      // Keep the build-time fallback when runtime config is not available.
    }
  }

  get<T extends keyof AppConfig>(key: T): AppConfig[T] {
    return this.config[key];
  }

  private normalize(value: Partial<AppConfig> | undefined | null): AppConfig | null {
    if (!value?.apiBase) {
      return null;
    }

    return {
      apiBase: value.apiBase.replace(/\/+$/, ''),
      environment: value.environment === 'production' ? 'production' : 'development'
    };
  }
}
