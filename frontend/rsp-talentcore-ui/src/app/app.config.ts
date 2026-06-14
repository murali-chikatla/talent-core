import { ApplicationConfig, inject, provideAppInitializer } from '@angular/core';
import { provideRouter } from '@angular/router';
import { provideClientHydration, withEventReplay } from '@angular/platform-browser';
import { provideAnimationsAsync } from '@angular/platform-browser/animations/async';
import { provideHttpClient, withFetch, withInterceptors } from '@angular/common/http';

import { routes } from './app.routes';
import { loadingInterceptor } from './core/interceptors/loading.interceptor';
import { jwtInterceptor } from './core/interceptors/jwt.interceptor';
import { httpErrorInterceptor } from './core/interceptors/http-error.interceptor';
import { ConfigService } from './core/services/config.service';

export const appConfig: ApplicationConfig = {
  providers: [
    provideAppInitializer(() => inject(ConfigService).load()),
    provideClientHydration(withEventReplay()),
    provideAnimationsAsync(),
    provideRouter(routes),
    provideHttpClient(
      withFetch(),
      withInterceptors([loadingInterceptor, jwtInterceptor, httpErrorInterceptor])
    )
  ]
};
