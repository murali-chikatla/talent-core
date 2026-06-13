import { CommonModule } from '@angular/common';
import { Component, OnInit, signal } from '@angular/core';
import { Router } from '@angular/router';
import { MatButtonModule } from '@angular/material/button';
import { MatChipsModule } from '@angular/material/chips';
import { MatDialog } from '@angular/material/dialog';
import { MatIconModule } from '@angular/material/icon';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { MatSnackBar, MatSnackBarModule } from '@angular/material/snack-bar';
import { catchError, finalize, map, of } from 'rxjs';
import { AuthService } from '../../core/auth/auth.service';
import { CurrentUserResponse } from '../../core/auth/auth.models';
import { LoggingService } from '../../core/services/logging.service';
import { ChangePasswordDialogComponent } from './change-password-dialog.component';

@Component({
  standalone: true,
  selector: 'feature-profile',
  imports: [
    CommonModule,
    MatButtonModule,
    MatChipsModule,
    MatIconModule,
    MatProgressSpinnerModule,
    MatSnackBarModule
  ],
  template: `
    <section class="profile-shell">
      <div class="profile-header">
        <div>
          <p class="eyebrow">Account</p>
          <h1>Profile</h1>
          <p class="profile-copy">{{ userEmail }}</p>
        </div>

        <button mat-raised-button color="primary" class="primary-action" type="button" (click)="openChangePassword()">
          <span class="material-symbols-outlined">lock_reset</span>
          Change Password
        </button>
      </div>

      <div *ngIf="loading()" class="loading-state">
        <mat-progress-spinner mode="indeterminate" diameter="44"></mat-progress-spinner>
        <span>Loading profile...</span>
      </div>

      <div *ngIf="error()" class="error-state">
        <span class="material-symbols-outlined">error_outline</span>
        <div>
          <h3>Profile unavailable</h3>
          <p>{{ error() }}</p>
        </div>
      </div>

      <div *ngIf="!loading() && !error()" class="profile-grid">
        <section class="panel identity-panel">
          <div class="profile-avatar">{{ initials }}</div>
          <div class="identity-copy">
            <h2>{{ displayName }}</h2>
            <p>{{ userEmail }}</p>
          </div>

          <div class="role-list" *ngIf="currentUser()?.roles?.length">
            <mat-chip-set aria-label="Assigned roles">
              <mat-chip *ngFor="let role of currentUser()?.roles">{{ role }}</mat-chip>
            </mat-chip-set>
          </div>
        </section>

        <section class="panel session-panel">
          <div class="panel-head">
            <div>
              <p class="panel-label">Session</p>
              <h2>Token Status</h2>
            </div>
            <span class="status-chip" [class.warning]="!profileTokenValid()">
              {{ profileTokenValid() ? 'Validated' : 'Review' }}
            </span>
          </div>

          <div class="detail-grid">
            <div>
              <span class="detail-label">Email</span>
              <span class="detail-value">{{ userEmail }}</span>
            </div>
            <div>
              <span class="detail-label">Roles</span>
              <span class="detail-value">{{ rolesText }}</span>
            </div>
          </div>
        </section>

      </div>
    </section>
  `,
  styles: [
    `
      .profile-shell {
        display: grid;
        gap: 1rem;
        padding: 1rem 1.5rem 1.5rem;
        max-width: 1320px;
        margin: 0 auto;
      }

      .profile-header {
        display: flex;
        justify-content: space-between;
        gap: 1rem;
        align-items: center;
        background: linear-gradient(180deg, rgba(201, 32, 47, 0.08), rgba(255, 255, 255, 0.8));
        border: 1px solid rgba(201, 32, 47, 0.12);
        border-radius: 16px;
        padding: 0.9rem 1.1rem;
        box-shadow: var(--shadow-soft);
      }

      .eyebrow,
      .panel-label {
        margin: 0;
        font-size: 0.76rem;
        letter-spacing: 0.15em;
        text-transform: uppercase;
        color: #b9242f;
      }

      .profile-header h1 {
        margin: 0.3rem 0 0;
        font-size: clamp(1.65rem, 2vw, 2.25rem);
        line-height: 1.1;
      }

      .profile-copy {
        margin: 0.35rem 0 0;
        color: var(--tc-grey);
      }

      .primary-action {
        min-height: 40px;
        border-radius: 999px;
        background: var(--tc-red);
        color: #ffffff;
        box-shadow: 0 10px 22px rgba(201, 32, 47, 0.22);
        display: inline-flex;
        align-items: center;
        gap: 0.45rem;
      }

      .profile-grid {
        display: grid;
        grid-template-columns: 0.85fr 1fr;
        gap: 1rem;
      }

      .panel {
        background: white;
        border-radius: 18px;
        padding: 1.15rem;
        box-shadow: var(--shadow-soft);
        border: 1px solid rgba(23, 33, 43, 0.06);
      }

      .identity-panel {
        display: grid;
        align-content: start;
        gap: 1rem;
      }

      .profile-avatar {
        width: 76px;
        height: 76px;
        display: grid;
        place-items: center;
        border-radius: 22px;
        background: #fde7e9;
        color: var(--tc-red);
        font-size: 1.6rem;
        font-weight: 800;
      }

      .identity-copy h2,
      .panel h2 {
        margin: 0;
        font-size: 1.35rem;
      }

      .identity-copy p {
        margin: 0.35rem 0 0;
        color: var(--tc-grey);
      }

      .role-list {
        display: flex;
        flex-wrap: wrap;
        gap: 0.5rem;
      }

      .panel-head {
        display: flex;
        align-items: center;
        justify-content: space-between;
        gap: 1rem;
        margin-bottom: 1rem;
      }

      .panel-label {
        margin-bottom: 0.35rem;
      }

      .status-chip {
        display: inline-flex;
        align-items: center;
        padding: 0.55rem 0.9rem;
        border-radius: 999px;
        background: #d1f6e8;
        color: #107d4b;
        font-weight: 700;
        font-size: 0.85rem;
      }

      .status-chip.warning {
        background: #fff4dc;
        color: #9a620d;
      }

      .detail-grid {
        display: grid;
        grid-template-columns: repeat(2, minmax(0, 1fr));
        gap: 0.85rem;
      }

      .detail-grid div {
        display: grid;
        gap: 0.25rem;
        padding: 0.8rem 0.9rem;
        border-radius: 14px;
        background: #fbf7f7;
      }

      .detail-label {
        color: var(--tc-grey);
        font-size: 0.78rem;
        text-transform: uppercase;
        font-weight: 700;
      }

      .detail-value {
        color: var(--tc-slate);
        font-weight: 700;
        overflow-wrap: anywhere;
      }

      .resource-panel {
        grid-column: 1 / -1;
      }

      .loading-state,
      .error-state {
        display: grid;
        grid-template-columns: auto 1fr;
        gap: 1rem;
        align-items: center;
        padding: 1.25rem 1.5rem;
        border-radius: 18px;
        background: white;
        box-shadow: var(--shadow-soft);
      }

      .loading-state {
        color: var(--tc-slate);
      }

      .error-state {
        border: 1px solid rgba(201, 32, 47, 0.16);
        color: #991b1b;
      }

      .error-state h3 {
        margin: 0 0 0.35rem;
      }

      @media (max-width: 900px) {
        .profile-header,
        .profile-grid {
          grid-template-columns: 1fr;
        }

        .profile-header {
          flex-direction: column;
          align-items: stretch;
        }
      }

      @media (max-width: 720px) {
        .profile-shell {
          padding: 1rem;
        }

        .detail-grid {
          grid-template-columns: 1fr;
        }
      }
    `
  ]
})
export class ProfileComponent implements OnInit {
  loading = signal(false);
  error = signal<string | null>(null);
  currentUser = signal<CurrentUserResponse | null>(null);
  profileTokenValid = signal(false);

  constructor(
    private auth: AuthService,
    private dialog: MatDialog,
    private snackBar: MatSnackBar,
    private router: Router,
    private logger: LoggingService
  ) {}

  ngOnInit(): void {
    this.loadProfile();
  }

  get displayName(): string {
    const profile = this.currentUser();
    const name = [profile?.firstName, profile?.lastName].filter(Boolean).join(' ');
    return name || profile?.email || 'TalentCore User';
  }

  get userEmail(): string {
    return this.currentUser()?.email || 'Signed in user';
  }

  get initials(): string {
    return this.displayName
      .split(/[.@\s]/)
      .filter(Boolean)
      .map(part => part.charAt(0))
      .slice(0, 2)
      .join('')
      .toUpperCase() || 'TC';
  }

  get rolesText(): string {
    return this.currentUser()?.roles?.join(', ') || 'No roles assigned';
  }

  openChangePassword(): void {
    const ref = this.dialog.open(ChangePasswordDialogComponent, {
      width: '540px',
      maxWidth: '92vw',
      autoFocus: 'first-tabbable'
    });

    ref.afterClosed().subscribe(changed => {
      if (!changed) return;
      this.snackBar.open('Password changed successfully. Please sign in again.', 'Close', { duration: 3500 });
      this.auth.logout(false);
      this.router.navigate(['/auth/login']);
    });
  }

  private loadProfile(): void {
    this.loading.set(true);
    this.error.set(null);

    this.auth.loadCurrentUser().pipe(
      map(user => ({ user, profileValid: true })),
      catchError(err => {
        this.logger.error('Profile load failed', err);
        return of({ user: null, profileValid: false });
      }),
      finalize(() => this.loading.set(false))
    ).subscribe({
      next: result => {
        this.currentUser.set(result.user);
        this.profileTokenValid.set(result.profileValid);
        if (!result.user) {
          this.error.set('Unable to load profile.');
        }
      },
      error: err => this.error.set(this.errorMessage(err))
    });
  }

  private errorMessage(err: unknown): string {
    const response = err as { error?: { message?: string } | string; message?: string };
    if (typeof response.error === 'string') return response.error;
    return response.error?.message || response.message || 'Unable to load profile.';
  }
}
