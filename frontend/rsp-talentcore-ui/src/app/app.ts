import { Component, signal } from '@angular/core';
import { AuthService } from './core/auth/auth.service';
import { RouterOutlet } from '@angular/router';

@Component({
  selector: 'app-root',
  imports: [RouterOutlet],
  templateUrl: './app.html',
  styleUrl: './app.scss'
})
export class App {
  protected readonly title = signal('rsp-talentcore-ui');

  // Inject AuthService to ensure authentication is hydrated on app startup
  constructor(private auth: AuthService) {}
}
