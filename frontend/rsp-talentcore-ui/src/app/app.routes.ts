import { Routes } from '@angular/router';
import { authMatchGuard } from './core/guards/auth.guard';

export const routes: Routes = [
  {
    path: 'auth',
    children: [
      {
        path: 'login',
        loadComponent: () => import('./features/auth/login.component').then(m => m.LoginComponent)
      }
    ]
  },
  { path: '', redirectTo: 'auth/login', pathMatch: 'full' },
  {
    path: '',
    loadComponent: () => import('./core/layouts/shell.component').then(m => m.ShellComponent),
    canMatch: [authMatchGuard],
    children: [
      { path: '', redirectTo: 'dashboard', pathMatch: 'full' },
      { path: 'dashboard', loadComponent: () => import('./features/dashboard/dashboard.component').then(m => m.DashboardComponent) },
      { path: 'profile', loadComponent: () => import('./features/profile/profile.component').then(m => m.ProfileComponent) },
      { path: 'users', loadComponent: () => import('./features/users/search/search-users.component').then(m => m.SearchUsersComponent) },
      { path: 'resources', loadComponent: () => import('./features/resources/search/search-resources.component').then(m => m.SearchResourcesComponent) }
    ]
  },
  { path: '**', redirectTo: 'auth/login' }
];
