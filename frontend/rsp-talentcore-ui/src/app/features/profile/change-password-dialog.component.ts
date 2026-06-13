import { CommonModule } from '@angular/common';
import { Component, inject, signal } from '@angular/core';
import { AbstractControl, FormBuilder, ReactiveFormsModule, ValidationErrors, ValidatorFn, Validators } from '@angular/forms';
import { MatButtonModule } from '@angular/material/button';
import { MatDialogModule, MatDialogRef } from '@angular/material/dialog';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatIconModule } from '@angular/material/icon';
import { MatInputModule } from '@angular/material/input';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { finalize } from 'rxjs';
import { UsersService } from '../users/users.service';
import { ChangePasswordRequest } from '../users/models/user.model';

@Component({
  standalone: true,
  selector: 'app-change-password-dialog',
  imports: [
    CommonModule,
    ReactiveFormsModule,
    MatButtonModule,
    MatDialogModule,
    MatFormFieldModule,
    MatIconModule,
    MatInputModule,
    MatProgressSpinnerModule
  ],
  template: `
    <h2 mat-dialog-title>Change Password</h2>

    <mat-dialog-content class="dialog-content">
      <form [formGroup]="form" class="dialog-form">
        <div *ngIf="error()" class="dialog-error">
          <span class="material-symbols-outlined">error_outline</span>
          <span>{{ error() }}</span>
        </div>

        <mat-form-field appearance="outline">
          <mat-label>Current Password</mat-label>
          <input matInput type="password" formControlName="currentPassword" autocomplete="current-password" />
        </mat-form-field>

        <mat-form-field appearance="outline">
          <mat-label>New Password</mat-label>
          <input matInput type="password" formControlName="newPassword" autocomplete="new-password" />
        </mat-form-field>

        <mat-form-field appearance="outline">
          <mat-label>Confirm Password</mat-label>
          <input matInput type="password" formControlName="confirmPassword" autocomplete="new-password" />
          <mat-error *ngIf="form.hasError('passwordMismatch')">Passwords must match</mat-error>
        </mat-form-field>
      </form>
    </mat-dialog-content>

    <mat-dialog-actions align="end">
      <button mat-button mat-dialog-close type="button" [disabled]="saving()">Cancel</button>
      <button mat-raised-button color="primary" type="button" (click)="onSubmit()" [disabled]="saving() || form.invalid">
        <mat-progress-spinner *ngIf="saving()" mode="indeterminate" diameter="18"></mat-progress-spinner>
        <span>{{ saving() ? 'Saving' : 'Save Password' }}</span>
      </button>
    </mat-dialog-actions>
  `,
  styles: [
    `
      .dialog-content {
        width: min(480px, 82vw);
      }

      .dialog-form {
        display: grid;
        gap: 0.85rem;
        padding-top: 0.25rem;
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

      @media (max-width: 640px) {
        .dialog-content {
          width: 100%;
        }
      }
    `
  ]
})
export class ChangePasswordDialogComponent {
  private fb = inject(FormBuilder);

  saving = signal(false);
  error = signal<string | null>(null);

  private passwordsMatch: ValidatorFn = (control: AbstractControl): ValidationErrors | null => {
    const newPassword = control.get('newPassword')?.value;
    const confirmPassword = control.get('confirmPassword')?.value;
    return newPassword && confirmPassword && newPassword !== confirmPassword
      ? { passwordMismatch: true }
      : null;
  };

  form = this.fb.group({
    currentPassword: ['', [Validators.required]],
    newPassword: ['', [Validators.required, Validators.minLength(8), Validators.maxLength(20)]],
    confirmPassword: ['', [Validators.required]]
  }, { validators: this.passwordsMatch });

  constructor(
    private users: UsersService,
    private dialogRef: MatDialogRef<ChangePasswordDialogComponent, boolean>
  ) {}

  onSubmit(): void {
    this.form.markAllAsTouched();
    if (this.form.invalid || this.saving()) return;

    const raw = this.form.getRawValue();
    const request: ChangePasswordRequest = {
      currentPassword: raw.currentPassword ?? '',
      newPassword: raw.newPassword ?? '',
      confirmPassword: raw.confirmPassword ?? ''
    };

    this.saving.set(true);
    this.error.set(null);

    this.users.changePassword(request).pipe(
      finalize(() => this.saving.set(false))
    ).subscribe({
      next: () => this.dialogRef.close(true),
      error: err => this.error.set(this.errorMessage(err))
    });
  }

  private errorMessage(err: unknown): string {
    const response = err as { error?: { message?: string } | string; message?: string };
    if (typeof response.error === 'string') return response.error;
    return response.error?.message || response.message || 'Unable to change password.';
  }
}
