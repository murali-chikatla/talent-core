import { Component, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule, Router } from '@angular/router';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatMenuModule } from '@angular/material/menu';
import { MatTooltipModule } from '@angular/material/tooltip';
import { AuthService } from '../auth/auth.service';
import { TokenStorageService } from '../auth/token-storage.service';
import { LoggingService } from '../services/logging.service';

@Component({
  standalone: true,
  selector: 'app-header',
  imports: [CommonModule, RouterModule, MatButtonModule, MatIconModule, MatMenuModule, MatTooltipModule],
  template: `
    <header class="tc-header mat-elevation-z4">
      <div class="brand-panel">
        <a routerLink="/dashboard" class="brand-link">
          <div class="logo" aria-hidden="true">
            <span class="material-symbols-outlined">dashboard_customize</span>
          </div>
          <div class="brand-copy">
            <span class="brand-name">TalentCore</span>
            <span class="brand-tag">Enterprise SaaS Platform</span>
          </div>
        </a>
      </div>

      <div class="toolbar-actions">
        <button mat-icon-button matTooltip="Notifications" aria-label="Notifications" class="icon-button">
          <span class="material-symbols-outlined">notifications</span>
          <span class="badge">4</span>
        </button>

        <button mat-button [matMenuTriggerFor]="profileMenu" class="profile-button">
          <span class="profile-avatar">{{ userInitials() }}</span>
          <span class="profile-name">{{ getDisplayName() }}</span>
          <span class="material-symbols-outlined arrow-icon">keyboard_arrow_down</span>
        </button>

        <mat-menu #profileMenu="matMenu">
          <button mat-menu-item (click)="goProfile()">Profile</button>
          <button mat-menu-item (click)="logout()">Logout</button>
        </mat-menu>
      </div>
    </header>
  `,
  styleUrls: ['./header.component.scss']
})
export class HeaderComponent {
  constructor(public auth: AuthService, private tokenStorage: TokenStorageService, private router: Router, private logger: LoggingService) {}

  // Return the signal value for template consumption
  currentUser() {
    return this.auth.currentUser();
  }

  getDisplayName(): string {
    const profile = this.auth.currentUser();
    const name = [profile?.firstName, profile?.lastName].filter(Boolean).join(' ');
    if (name) return name;
    if (profile?.email) return profile.email;

    const payload = this.tokenStorage.decodeToken(this.tokenStorage.getAccessToken());
    const tokenName = [payload?.['firstName'], payload?.['lastName']].filter(Boolean).join(' ');
    return tokenName || (payload as any)?.email || (payload as any)?.sub || 'TalentCore User';
  }

  userInitials() {
    const value = this.getDisplayName() || 'TC';
    return value
      .split(/[.@\s]/)
      .map(part => part.charAt(0))
      .slice(0, 2)
      .join('')
      .toUpperCase();
  }

  goProfile() {
    this.router.navigate(['/profile']);
  }

  logout() {
    this.logger.info('User logout from header');
    this.auth.logout();
    this.router.navigate(['/auth/login']);
  }
}
