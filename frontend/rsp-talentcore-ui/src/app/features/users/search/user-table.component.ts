import { Component, Input, Output, EventEmitter, OnInit, OnChanges, SimpleChanges, ChangeDetectionStrategy } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MatTableModule } from '@angular/material/table';
import { MatPaginatorModule, PageEvent } from '@angular/material/paginator';
import { MatSortModule, Sort } from '@angular/material/sort';
import { User } from '../models/user.model';
import { Page } from '../models/pagination.model';

@Component({
  standalone: true,
  selector: 'app-user-table',
  imports: [CommonModule, MatTableModule, MatPaginatorModule, MatSortModule],
  template: `
    <div class="table-container">
      <mat-table [dataSource]="dataSource" matSort (matSortChange)="onSort($event)" class="user-table">
        <ng-container matColumnDef="userId">
          <th mat-header-cell *matHeaderCellDef mat-sort-header>User ID</th>
          <td mat-cell *matCellDef="let user">{{ user.userId }}</td>
        </ng-container>

        <ng-container matColumnDef="employeeCode">
          <th mat-header-cell *matHeaderCellDef mat-sort-header>Employee Code</th>
          <td mat-cell *matCellDef="let user">{{ user.employeeCode }}</td>
        </ng-container>

        <ng-container matColumnDef="firstName">
          <th mat-header-cell *matHeaderCellDef mat-sort-header>First Name</th>
          <td mat-cell *matCellDef="let user">{{ user.firstName }}</td>
        </ng-container>

        <ng-container matColumnDef="lastName">
          <th mat-header-cell *matHeaderCellDef mat-sort-header>Last Name</th>
          <td mat-cell *matCellDef="let user">{{ user.lastName }}</td>
        </ng-container>

        <ng-container matColumnDef="email">
          <th mat-header-cell *matHeaderCellDef mat-sort-header>Email</th>
          <td mat-cell *matCellDef="let user">
            <a [href]="'mailto:' + user.email" class="email-link">{{ user.email }}</a>
          </td>
        </ng-container>

        <ng-container matColumnDef="active">
          <th mat-header-cell *matHeaderCellDef mat-sort-header>Active</th>
          <td mat-cell *matCellDef="let user">
            <span class="status-badge" [class.active]="user.active">{{ user.active ? 'Active' : 'Inactive' }}</span>
          </td>
        </ng-container>

        <ng-container matColumnDef="roles">
          <th mat-header-cell *matHeaderCellDef mat-sort-header>Roles</th>
          <td mat-cell *matCellDef="let user">
            <div class="role-badges">
              <span *ngFor="let role of user.roles" class="role-badge">{{ role }}</span>
            </div>
          </td>
        </ng-container>

        <tr mat-header-row *matHeaderRowDef="displayedColumns; sticky: true" class="table-header"></tr>
        <tr mat-row *matRowDef="let user; columns: displayedColumns;" class="table-row"></tr>
      </mat-table>

      <div *ngIf="!isLoading && dataSource.length === 0" class="empty-state">
        <span class="material-symbols-outlined">person_off</span>
        <h3>No users found</h3>
        <p>Try adjusting your filters or search criteria</p>
      </div>

      <mat-paginator
        [length]="total"
        [pageSize]="pageSize"
        [pageSizeOptions]="[10, 25, 50]"
        (page)="onPageChange($event)"
        showFirstLastButtons>
      </mat-paginator>
    </div>
  `,
  styleUrls: ['./user-table.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class UserTableComponent implements OnInit, OnChanges {
  @Input() data!: Page<User>;
  @Input() isLoading = false;
  @Output() pageChange = new EventEmitter<{ page: number; size: number }>();
  @Output() sortChange = new EventEmitter<{ sort: string; order: 'asc' | 'desc' }>();

  displayedColumns: string[] = ['userId', 'employeeCode', 'firstName', 'lastName', 'email', 'active', 'roles'];
  dataSource: User[] = [];
  total = 0;
  pageSize = 10;

  ngOnInit(): void {
    this.updateDataSource();
  }

  ngOnChanges(changes: SimpleChanges): void {
    if (changes['data']) {
      this.updateDataSource();
    }
  }

  private updateDataSource(): void {
    if (this.data) {
      this.dataSource = this.data.items;
      this.total = this.data.total;
      this.pageSize = this.data.size;
    }
  }

  onPageChange(event: PageEvent): void {
    this.pageChange.emit({ page: event.pageIndex, size: event.pageSize });
  }

  onSort(sort: Sort): void {
    if (sort.active) {
      this.sortChange.emit({ sort: sort.active, order: (sort.direction || 'asc') as 'asc' | 'desc' });
    }
  }
}
