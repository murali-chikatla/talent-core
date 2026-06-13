import { Component, EventEmitter, Output } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, ReactiveFormsModule } from '@angular/forms';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatSelectModule } from '@angular/material/select';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { ResourceFilter, ResourceStatus, RESOURCE_STATUS_LABELS } from '../models/resource.model';

@Component({
  standalone: true,
  selector: 'app-resource-filters',
  imports: [CommonModule, ReactiveFormsModule, MatFormFieldModule, MatInputModule, MatSelectModule, MatButtonModule, MatIconModule],
  template: `
    <form [formGroup]="filterForm" class="filter-panel">
      <div class="filter-grid">
        <mat-form-field appearance="outline">
          <mat-label>Employee ID</mat-label>
          <input matInput formControlName="employeeId" placeholder="e.g., EMP001" />
        </mat-form-field>

        <mat-form-field appearance="outline">
          <mat-label>First Name</mat-label>
          <input matInput formControlName="firstName" placeholder="Search by first name" />
        </mat-form-field>

        <mat-form-field appearance="outline">
          <mat-label>Last Name</mat-label>
          <input matInput formControlName="lastName" placeholder="Search by last name" />
        </mat-form-field>

        <mat-form-field appearance="outline">
          <mat-label>Email</mat-label>
          <input matInput formControlName="email" placeholder="Search by email" />
        </mat-form-field>

        <mat-form-field appearance="outline">
          <mat-label>Experience Years</mat-label>
          <input matInput formControlName="experienceYears" type="number" min="0" placeholder="e.g., 8" />
        </mat-form-field>

        <mat-form-field appearance="outline">
          <mat-label>Primary Skill</mat-label>
          <input matInput formControlName="primarySkill" placeholder="e.g., Java" />
        </mat-form-field>

        <mat-form-field appearance="outline">
          <mat-label>Status</mat-label>
          <mat-select formControlName="status">
            <mat-option value="">All Statuses</mat-option>
            <mat-option *ngFor="let status of statuses" [value]="status">{{ statusLabel(status) }}</mat-option>
          </mat-select>
        </mat-form-field>
      </div>

      <div class="filter-actions">
        <button mat-raised-button color="primary" (click)="onSearch()" type="button">
          <span class="material-symbols-outlined">search</span>
          Search
        </button>
        <button mat-stroked-button (click)="onReset()" type="button">
          <span class="material-symbols-outlined">refresh</span>
          Reset
        </button>
      </div>
    </form>
  `,
  styles: [
    `
      .filter-panel {
        display: grid;
        background: white;
        border-radius: 18px;
        padding: 1.2rem 1.25rem;
        box-shadow: var(--shadow-soft);
        border: 1px solid rgba(23, 33, 43, 0.06);
      }

      .filter-grid {
        display: grid;
        grid-template-columns: repeat(4, minmax(160px, 1fr));
        gap: 0.85rem;
        margin-bottom: 1rem;
      }

      .filter-actions {
        display: flex;
        justify-content: flex-end;
        gap: 0.85rem;
        flex-wrap: wrap;
      }

      .filter-actions button {
        min-height: 40px;
        border-radius: 999px;
        display: inline-flex;
        align-items: center;
        gap: 0.5rem;
      }

      .filter-actions button:hover {
        transform: translateY(-1px);
      }

      .filter-actions [color='primary'] {
        background: var(--tc-red);
        color: #ffffff;
        box-shadow: 0 10px 22px rgba(201, 32, 47, 0.22);
      }

      .filter-actions span.material-symbols-outlined {
        font-size: 1.2rem;
      }

      :host ::ng-deep .mat-mdc-form-field-subscript-wrapper {
        display: none;
      }

      :host ::ng-deep .mat-mdc-text-field-wrapper {
        background: #ffffff;
      }

      @media (max-width: 960px) {
        .filter-grid {
          grid-template-columns: repeat(2, minmax(0, 1fr));
        }
      }

      @media (max-width: 720px) {
        .filter-grid {
          grid-template-columns: 1fr;
        }

        .filter-actions button {
          width: 100%;
          justify-content: center;
        }
      }
    `
  ]
})
export class ResourceFiltersComponent {
  @Output() search = new EventEmitter<ResourceFilter>();
  @Output() reset = new EventEmitter<void>();

  filterForm: FormGroup;
  statuses = Object.values(ResourceStatus) as ResourceStatus[];

  constructor(private fb: FormBuilder) {
    this.filterForm = this.fb.group({
      employeeId: [''],
      firstName: [''],
      lastName: [''],
      email: [''],
      experienceYears: [''],
      primarySkill: [''],
      status: ['']
    });
  }

  statusLabel(status: ResourceStatus): string {
    return RESOURCE_STATUS_LABELS[status] || status;
  }

  onSearch(): void {
    const filters = { ...this.filterForm.value };
    if (filters.experienceYears !== '') {
      const parsed = Number(filters.experienceYears);
      filters.experienceYears = Number.isFinite(parsed) ? parsed : undefined;
    } else {
      delete filters.experienceYears;
    }

    Object.keys(filters).forEach(key => {
      if (filters[key] === '' || filters[key] === undefined || filters[key] === null) {
        delete filters[key];
      }
    });

    this.search.emit(filters);
  }

  onReset(): void {
    this.filterForm.reset();
    this.reset.emit();
  }
}
