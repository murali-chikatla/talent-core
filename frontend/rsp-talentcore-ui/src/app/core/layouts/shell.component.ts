import { Component } from '@angular/core';
import { RouterOutlet } from '@angular/router';
import { HeaderComponent } from './header.component';
import { NavigationComponent } from './navigation.component';
import { FooterComponent } from './footer.component';

@Component({
  standalone: true,
  selector: 'app-shell',
  imports: [HeaderComponent, NavigationComponent, RouterOutlet, FooterComponent],
  template: `
    <div class="app-shell">
      <app-header></app-header>
      <app-nav></app-nav>
      <main class="app-main"><router-outlet></router-outlet></main>
      <app-footer></app-footer>
    </div>
  `,
  styleUrls: ['./shell.component.scss'],
})
export class ShellComponent {}
