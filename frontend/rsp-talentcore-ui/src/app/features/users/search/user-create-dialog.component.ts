import { CommonModule } from '@angular/common';
import { Component, inject, signal } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { MatButtonModule } from '@angular/material/button';
import { MatDialogModule, MatDialogRef } from '@angular/material/dialog';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatIconModule } from '@angular/material/icon';
import { MatInputModule } from '@angular/material/input';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { MatSelectModule } from '@angular/material/select';
import { finalize, map, of, switchMap } from 'rxjs';
import { UsersService } from '../users.service';
import { ALL_ROLES, ROLE_LABELS, UserRole } from '../models/role.model';
import { UserRequest } from '../models/user.model';

@Component({
  standalone: true,
  selector: 'app-user-create-dialog',
  imports: [
    CommonModule,
    ReactiveFormsModule,
    MatButtonModule,
    MatDialogModule,
    MatFormFieldModule,
    MatIconModule,
    MatInputModule,
    MatProgressSpinnerModule,
    MatSelectModule
  ],
  template: `
    <h2 mat-dialog-title>Create User</h2>

    <mat-dialog-content class="dialog-content">
      <form [formGroup]="form" class="dialog-form">
        <div *ngIf="error()" class="dialog-error">
          <span class="material-symbols-outlined">error_outline</span>
          <span>{{ error() }}</span>
        </div>

        <div class="dialog-grid">
          <mat-form-field appearance="outline">
            <mat-label>Employee Code</mat-label>
            <input matInput formControlName="employeeCode" autocomplete="off" />
          </mat-form-field>

          <mat-form-field appearance="outline">
            <mat-label>First Name</mat-label>
            <input matInput formControlName="firstName" autocomplete="given-name" />
          </mat-form-field>

          <mat-form-field appearance="outline">
            <mat-label>Last Name</mat-label>
            <input matInput formControlName="lastName" autocomplete="family-name" />
          </mat-form-field>

          <mat-form-field appearance="outline">
            <mat-label>Email</mat-label>
            <input matInput formControlName="email" autocomplete="email" />
          </mat-form-field>

          <mat-form-field appearance="outline">
            <mat-label>Initial Password</mat-label>
            <input matInput type="password" formControlName="password" autocomplete="new-password" />
          </mat-form-field>

          <mat-form-field appearance="outline">
            <mat-label>Roles</mat-label>
            <mat-select formControlName="roles" multiple>
              <mat-option *ngFor="let role of roles" [value]="role">{{ roleLabel(role) }}</mat-option>
            </mat-select>
          </mat-form-field>
        </div>
      </form>
    </mat-dialog-content>

    <mat-dialog-actions align="end">
      <button mat-button mat-dialog-close type="button" [disabled]="saving()">Cancel</button>
      <button mat-raised-button color="primary" type="button" (click)="onSubmit()" [disabled]="saving() || form.invalid">
        <mat-progress-spinner *ngIf="saving()" mode="indeterminate" diameter="18"></mat-progress-spinner>
        <span>{{ saving() ? 'Creating' : 'Create User' }}</span>
      </button>
    </mat-dialog-actions>
  `,
  styles: [
    `
      .dialog-content {
        width: min(720px, 82vw);
      }

      .dialog-form {
        display: grid;
        gap: 1rem;
        padding-top: 0.25rem;
      }

      .dialog-grid {
        display: grid;
        grid-template-columns: repeat(2, minmax(0, 1fr));
        gap: 0.85rem;
      }

      .dialog-error {
        display: flex;
        align-items: center;
        gap: 0.5rem;
        border: 1px solid rgba(201, 32, 47, 0.18);
        border-radius: 12px;
        background: #fff2f3;
        color: #991b1b;
        padding: 0.75rem 0.9rem;
        font-weight: 600;
      }

      button[color='primary'] {
        background: var(--tc-red);
        color: #ffffff;
      }

      button mat-progress-spinner {
        margin-right: 0.45rem;
      }

      @media (max-width: 720px) {
        .dialog-content {
          width: 100%;
        }

        .dialog-grid {
          grid-template-columns: 1fr;
        }
      }
    `
  ]
})
export class UserCreateDialogComponent {
  private fb = inject(FormBuilder);
  private users = inject(UsersService);
  private dialogRef = inject<MatDialogRef<UserCreateDialogComponent, boolean>>(MatDialogRef);

  saving = signal(false);
  error = signal<string | null>(null);
  roles = ALL_ROLES;

  form = this.fb.group({
    employeeCode: ['', [Validators.required]],
    firstName: ['', [Validators.required]],
    lastName: [''],
    email: ['', [Validators.required, Validators.email]],
    password: ['', [Validators.required, Validators.minLength(8)]],
    roles: [[] as string[]]
  });

  roleLabel(role: string): string {
    return ROLE_LABELS[role as UserRole] ?? role;
  }

  onSubmit(): void {
    this.form.markAllAsTouched();
    if (this.form.invalid || this.saving()) return;

    const raw = this.form.getRawValue();
    const request: UserRequest = {
      employeeCode: raw.employeeCode?.trim() ?? '',
      firstName: raw.firstName?.trim() ?? '',
      lastName: raw.lastName?.trim() || undefined,
      email: raw.email?.trim() ?? '',
      password: raw.password ?? ''
    };
    const roleCodes = raw.roles ?? [];

    this.saving.set(true);
    this.error.set(null);

    this.users.register(request).pipe(
      switchMap(user => {
        if (!roleCodes.length || !user.userId) {
          return of(user);
        }
        return this.users.assignRoles({ userId: user.userId, roleCodes }).pipe(map(() => user));
      }),
      finalize(() => this.saving.set(false))
    ).subscribe({
      next: () => this.dialogRef.close(true),
      error: err => this.error.set(this.errorMessage(err))
    });
  }

  private errorMessage(err: unknown): string {
    const response = err as { error?: { message?: string } | string; message?: string };
    if (typeof response.error === 'string') return response.error;
    return response.error?.message || response.message || 'Unable to create user.';
  }
}
