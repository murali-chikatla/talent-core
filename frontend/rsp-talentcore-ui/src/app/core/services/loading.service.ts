import { Injectable, signal } from '@angular/core';

@Injectable({ providedIn: 'root' })
export class LoadingService {
  private counter = 0;
  public loading = signal(false);

  start() {
    this.counter++;
    this.loading.set(true);
  }

  stop() {
    this.counter = Math.max(0, this.counter - 1);
    if (this.counter === 0) this.loading.set(false);
  }

  reset() {
    this.counter = 0;
    this.loading.set(false);
  }
}
