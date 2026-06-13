import { Injectable } from '@angular/core';

@Injectable({ providedIn: 'root' })
export class LoggingService {
  info(message: string, meta?: any) {
    console.info('[INFO]', message, meta || '');
  }
  warn(message: string, meta?: any) {
    console.warn('[WARN]', message, meta || '');
  }
  error(message: string, meta?: any) {
    console.error('[ERROR]', message, meta || '');
  }
}
