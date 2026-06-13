import { Component, EventEmitter, Output } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, ReactiveFormsModule } from '@angular/forms';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatSelectModule } from '@angular/material/select';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { UserFilter } from '../models/user.model';
import { ROLE_LABELS, ALL_ROLES, UserRole } from '../models/role.model';

@Component({
  standalone: true,
  selector: 'app-user-filters',
  imports: [CommonModule, ReactiveFormsModule, MatFormFieldModule, MatInputModule, MatSelectModule, MatButtonModule, MatIconModule],
  template: `
    <form [formGroup]="filterForm" class="filter-panel">
      <div class="filter-grid">
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
          <mat-label>Active Status</mat-label>
          <mat-select formControlName="active">
            <mat-option [value]="''">All Statuses</mat-option>
            <mat-option [value]="true">Active</mat-option>
            <mat-option [value]="false">Inactive</mat-option>
          </mat-select>
        </mat-form-field>

        <mat-form-field appearance="outline">
          <mat-label>Role</mat-label>
          <mat-select formControlName="roleCode">
            <mat-option value="">All Roles</mat-option>
            <mat-option *ngFor="let r of roles" [value]="r">{{ roleLabel(r) }}</mat-option>
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
  styleUrls: ['./user-filters.component.scss']
})
export class UserFiltersComponent {
  @Output() search = new EventEmitter<UserFilter>();
  @Output() reset = new EventEmitter<void>();

  filterForm: FormGroup;
  roles = ALL_ROLES;

  constructor(private fb: FormBuilder) {
    this.filterForm = this.fb.group({
      firstName: [''],
      lastName: [''],
      email: [''],
      active: [''],
      roleCode: ['']
    });
  }

  roleLabel(role: string): string {
    return ROLE_LABELS[role as UserRole] ?? role;
  }

  onSearch(): void {
    const filters = { ...this.filterForm.value };
    if (filters.active === '') {
      delete filters.active;
    }
    Object.keys(filters).forEach(key => {
      if (filters[key] === '' || filters[key] === null || filters[key] === undefined) {
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
