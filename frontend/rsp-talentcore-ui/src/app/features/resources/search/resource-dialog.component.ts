import { CommonModule } from '@angular/common';
import { Component, Inject, OnInit, inject, signal } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { MatButtonModule } from '@angular/material/button';
import { MAT_DIALOG_DATA, MatDialogModule, MatDialogRef } from '@angular/material/dialog';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatIconModule } from '@angular/material/icon';
import { MatInputModule } from '@angular/material/input';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { MatSelectModule } from '@angular/material/select';
import { finalize } from 'rxjs';
import { ResourcesService } from '../resources.service';
import { ALL_RESOURCE_STATUS, RESOURCE_STATUS_LABELS, Resource, ResourceRequest, ResourceStatus } from '../models/resource.model';

export interface ResourceDialogData {
  mode: 'create' | 'view' | 'edit';
  id?: number;
}

@Component({
  standalone: true,
  selector: 'app-resource-dialog',
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
    <h2 mat-dialog-title>{{ title }}</h2>

    <mat-dialog-content class="dialog-content">
      <div *ngIf="loading()" class="dialog-loading">
        <mat-progress-spinner mode="indeterminate" diameter="36"></mat-progress-spinner>
        <span>Loading resource...</span>
      </div>

      <form *ngIf="!loading()" [formGroup]="form" class="dialog-form">
        <div *ngIf="error()" class="dialog-error">
          <span class="material-symbols-outlined">error_outline</span>
          <span>{{ error() }}</span>
        </div>

        <div class="dialog-grid">
          <mat-form-field appearance="outline">
            <mat-label>Employee ID</mat-label>
            <input matInput formControlName="employeeId" autocomplete="off" />
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
            <mat-label>Mobile</mat-label>
            <input matInput formControlName="mobile" autocomplete="tel" />
          </mat-form-field>

          <mat-form-field appearance="outline">
            <mat-label>Experience Years</mat-label>
            <input matInput type="number" min="0" formControlName="experienceYears" />
          </mat-form-field>

          <mat-form-field appearance="outline">
            <mat-label>Primary Skill</mat-label>
            <input matInput formControlName="primarySkill" autocomplete="off" />
          </mat-form-field>

          <mat-form-field appearance="outline">
            <mat-label>Status</mat-label>
            <mat-select formControlName="status">
              <mat-option *ngFor="let status of statuses" [value]="status">{{ statusLabel(status) }}</mat-option>
            </mat-select>
          </mat-form-field>
        </div>
      </form>
    </mat-dialog-content>

    <mat-dialog-actions align="end">
      <button mat-button mat-dialog-close type="button">{{ readonly ? 'Close' : 'Cancel' }}</button>
      <button *ngIf="!readonly" mat-raised-button color="primary" type="button" (click)="onSubmit()" [disabled]="saving() || form.invalid || loading()">
        <mat-progress-spinner *ngIf="saving()" mode="indeterminate" diameter="18"></mat-progress-spinner>
        <span>{{ saving() ? 'Saving' : actionLabel }}</span>
      </button>
    </mat-dialog-actions>
  `,
  styles: [
    `
      .dialog-content {
        width: min(760px, 84vw);
      }

      .dialog-form,
      .dialog-loading {
        display: grid;
        gap: 1rem;
        padding-top: 0.25rem;
      }

      .dialog-loading {
        grid-template-columns: auto 1fr;
        align-items: center;
        color: var(--tc-grey);
        min-height: 140px;
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
export class ResourceDialogComponent implements OnInit {
  private fb = inject(FormBuilder);

  loading = signal(false);
  saving = signal(false);
  error = signal<string | null>(null);
  statuses = ALL_RESOURCE_STATUS;

  form = this.fb.group({
    employeeId: ['', [Validators.required]],
    firstName: ['', [Validators.required]],
    lastName: ['', [Validators.required]],
    email: ['', [Validators.required, Validators.email]],
    mobile: [''],
    experienceYears: [0, [Validators.min(0)]],
    primarySkill: ['', [Validators.required]],
    status: [ResourceStatus.ACTIVE, [Validators.required]]
  });

  constructor(
    @Inject(MAT_DIALOG_DATA) public data: ResourceDialogData,
    private resources: ResourcesService,
    private dialogRef: MatDialogRef<ResourceDialogComponent, boolean>
  ) {}

  ngOnInit(): void {
    if (this.readonly) {
      this.form.disable();
    }

    if (this.data.mode === 'view' || this.data.mode === 'edit') {
      this.loadResource();
    }
  }

  get readonly(): boolean {
    return this.data.mode === 'view';
  }

  get title(): string {
    if (this.data.mode === 'view') return 'View Resource';
    if (this.data.mode === 'edit') return 'Edit Resource';
    return 'Create Resource';
  }

  get actionLabel(): string {
    return this.data.mode === 'edit' ? 'Save Changes' : 'Create Resource';
  }

  statusLabel(status: ResourceStatus): string {
    return RESOURCE_STATUS_LABELS[status] || status;
  }

  onSubmit(): void {
    this.form.markAllAsTouched();
    if (this.form.invalid || this.saving() || this.readonly) return;

    const request = this.toRequest();
    const action = this.data.mode === 'edit'
      ? this.resources.update(this.requireId(), request)
      : this.resources.create(request);

    this.saving.set(true);
    this.error.set(null);

    action.pipe(finalize(() => this.saving.set(false))).subscribe({
      next: () => this.dialogRef.close(true),
      error: err => this.error.set(this.errorMessage(err))
    });
  }

  private loadResource(): void {
    const id = this.requireId();
    this.loading.set(true);
    this.error.set(null);

    this.resources.getById(id).pipe(
      finalize(() => this.loading.set(false))
    ).subscribe({
      next: resource => this.patchResource(resource),
      error: err => this.error.set(this.errorMessage(err))
    });
  }

  private patchResource(resource: Resource): void {
    this.form.patchValue({
      employeeId: resource.employeeId ?? '',
      firstName: resource.firstName ?? '',
      lastName: resource.lastName ?? '',
      email: resource.email ?? '',
      mobile: resource.mobile ?? '',
      experienceYears: resource.experienceYears ?? 0,
      primarySkill: resource.primarySkill ?? '',
      status: resource.status ?? ResourceStatus.ACTIVE
    });

    if (this.readonly) {
      this.form.disable();
    }
  }

  private toRequest(): ResourceRequest {
    const raw = this.form.getRawValue();
    const experienceYears = Number(raw.experienceYears);
    return {
      employeeId: this.clean(raw.employeeId),
      firstName: this.clean(raw.firstName),
      lastName: this.clean(raw.lastName),
      email: this.clean(raw.email),
      mobile: this.clean(raw.mobile),
      experienceYears: Number.isFinite(experienceYears) ? experienceYears : undefined,
      primarySkill: this.clean(raw.primarySkill),
      status: raw.status ?? undefined
    };
  }

  private clean(value: unknown): string | undefined {
    const text = String(value ?? '').trim();
    return text || undefined;
  }

  private requireId(): number {
    if (typeof this.data.id === 'number') {
      return this.data.id;
    }
    throw new Error('Resource id is required.');
  }

  private errorMessage(err: unknown): string {
    const response = err as { error?: { message?: string } | string; message?: string };
    if (typeof response.error === 'string') return response.error;
    return response.error?.message || response.message || 'Unable to save resource.';
  }
}
