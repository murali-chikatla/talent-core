import { Component, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { MatIconModule } from '@angular/material/icon';

interface MenuItem { label: string; path: string; icon: string; }

@Component({
  standalone: true,
  selector: 'app-nav',
  imports: [CommonModule, RouterModule, MatIconModule],
  template: `
    <nav class="tc-nav mat-elevation-z2">
      <button class="hamburger" type="button" (click)="collapsed.update(v => !v)" aria-label="Toggle navigation">
        <span class="material-symbols-outlined">menu</span>
      </button>
      <ul [class.open]="collapsed()">
        <li *ngFor="let m of menu" routerLinkActive="active" [routerLinkActiveOptions]="{ exact: true }">
          <a [routerLink]="m.path" (click)="collapsed.set(false)">
            <span class="material-symbols-outlined nav-icon">{{ m.icon }}</span>
            {{ m.label }}
          </a>
        </li>
      </ul>
    </nav>
  `,
  styleUrls: ['./navigation.component.scss']
})
export class NavigationComponent {
  collapsed = signal(false);

  menu: MenuItem[] = [
    { label: 'Dashboard', path: '/dashboard', icon: 'dashboard' },
    { label: 'Users', path: '/users', icon: 'groups' },
    { label: 'Resources', path: '/resources', icon: 'inventory' }
  ];
}
