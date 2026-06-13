import { Component, Input, Output, EventEmitter, OnInit, OnChanges, SimpleChanges, ChangeDetectionStrategy } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatTableModule } from '@angular/material/table';
import { MatPaginatorModule, PageEvent } from '@angular/material/paginator';
import { MatSortModule, Sort } from '@angular/material/sort';
import { MatTooltipModule } from '@angular/material/tooltip';
import { Resource } from '../models/resource.model';
import { Page } from '../../users/models/pagination.model';

@Component({
  standalone: true,
  selector: 'app-resource-table',
  imports: [CommonModule, MatButtonModule, MatIconModule, MatTableModule, MatPaginatorModule, MatSortModule, MatTooltipModule],
  template: `
    <div class="table-container">
      <mat-table [dataSource]="dataSource" matSort (matSortChange)="onSort($event)" class="resource-table">
        <ng-container matColumnDef="employeeId">
          <th mat-header-cell *matHeaderCellDef mat-sort-header>Employee ID</th>
          <td mat-cell *matCellDef="let resource">{{ resource.employeeId }}</td>
        </ng-container>

        <ng-container matColumnDef="firstName">
          <th mat-header-cell *matHeaderCellDef mat-sort-header>First Name</th>
          <td mat-cell *matCellDef="let resource">{{ resource.firstName }}</td>
        </ng-container>

        <ng-container matColumnDef="lastName">
          <th mat-header-cell *matHeaderCellDef mat-sort-header>Last Name</th>
          <td mat-cell *matCellDef="let resource">{{ resource.lastName }}</td>
        </ng-container>

        <ng-container matColumnDef="email">
          <th mat-header-cell *matHeaderCellDef mat-sort-header>Email</th>
          <td mat-cell *matCellDef="let resource">
            <a [href]="'mailto:' + resource.email" class="email-link">{{ resource.email }}</a>
          </td>
        </ng-container>

        <ng-container matColumnDef="primarySkill">
          <th mat-header-cell *matHeaderCellDef mat-sort-header>Primary Skill</th>
          <td mat-cell *matCellDef="let resource">{{ resource.primarySkill }}</td>
        </ng-container>

        <ng-container matColumnDef="experienceYears">
          <th mat-header-cell *matHeaderCellDef mat-sort-header>Experience Years</th>
          <td mat-cell *matCellDef="let resource">{{ resource.experienceYears }}</td>
        </ng-container>

        <ng-container matColumnDef="status">
          <th mat-header-cell *matHeaderCellDef mat-sort-header>Status</th>
          <td mat-cell *matCellDef="let resource">
            <span class="status-badge" [class.active]="resource.status === 'ACTIVE'">{{ resource.status }}</span>
          </td>
        </ng-container>

        <ng-container matColumnDef="mobile">
          <th mat-header-cell *matHeaderCellDef mat-sort-header>Mobile</th>
          <td mat-cell *matCellDef="let resource">{{ resource.mobile }}</td>
        </ng-container>

        <ng-container matColumnDef="actions">
          <th mat-header-cell *matHeaderCellDef>Actions</th>
          <td mat-cell *matCellDef="let resource" class="actions-cell">
            <button mat-icon-button type="button" matTooltip="View resource" aria-label="View resource" [disabled]="!resource.id" (click)="viewResource.emit(resource)">
              <span class="material-symbols-outlined">visibility</span>
            </button>
            <button mat-icon-button type="button" matTooltip="Edit resource" aria-label="Edit resource" [disabled]="!resource.id" (click)="editResource.emit(resource)">
              <span class="material-symbols-outlined">edit</span>
            </button>
            <button mat-icon-button type="button" class="delete-button" matTooltip="Delete resource" aria-label="Delete resource" [disabled]="!resource.id" (click)="deleteResource.emit(resource)">
              <span class="material-symbols-outlined">delete</span>
            </button>
          </td>
        </ng-container>

        <tr mat-header-row *matHeaderRowDef="displayedColumns; sticky: true" class="table-header"></tr>
        <tr mat-row *matRowDef="let resource; columns: displayedColumns;" class="table-row"></tr>
      </mat-table>

      <div *ngIf="!isLoading && dataSource.length === 0" class="empty-state">
        <span class="material-symbols-outlined">search_off</span>
        <h3>No resources found</h3>
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
  styles: [
    `
      .table-container {
        background: white;
        border-radius: 18px;
        box-shadow: var(--shadow-soft);
        overflow: auto;
        border: 1px solid rgba(23, 33, 43, 0.06);
      }

      .resource-table {
        width: 100%;
        min-width: 1040px;
        border-collapse: collapse;
      }

      .mat-mdc-header-row,
      .mat-header-row {
        background: #fcfbfb;
        height: 44px;
      }

      .mat-mdc-header-cell,
      .mat-mdc-cell,
      .mat-header-cell,
      .mat-cell {
        padding: 0.8rem 1rem;
        border-bottom: 1px solid rgba(23, 33, 43, 0.08);
      }

      .mat-mdc-header-cell,
      .mat-header-cell {
        color: var(--tc-slate);
        font-weight: 800;
        text-transform: uppercase;
        letter-spacing: 0.02em;
        font-size: 0.76rem;
        background: #fcfbfb;
        white-space: nowrap;
      }

      .mat-mdc-row,
      .table-row {
        height: 52px;
        transition: background 0.16s ease;
      }

      .mat-mdc-cell,
      .mat-cell {
        color: var(--tc-slate);
        font-size: 0.9rem;
      }

      .email-link {
        color: var(--tc-red);
        text-decoration: none;
      }

      .status-badge {
        display: inline-flex;
        align-items: center;
        justify-content: center;
        padding: 0.35rem 0.8rem;
        border-radius: 999px;
        font-weight: 700;
        font-size: 0.76rem;
        text-transform: uppercase;
        background: #f3f3f3;
      }

      .status-badge.active {
        background: #d1f6e8;
        color: #107d4b;
      }

      .actions-cell {
        white-space: nowrap;
      }

      .actions-cell button {
        color: var(--tc-red);
      }

      .actions-cell button:disabled {
        color: rgba(23, 33, 43, 0.24);
      }

      .actions-cell .delete-button {
        color: #991b1b;
      }

      .table-header {
        box-shadow: inset 0 -1px 0 rgba(0, 0, 0, 0.04);
      }

      .mat-mdc-row:hover,
      .table-row:hover {
        background: rgba(253, 231, 233, 0.6);
      }

      .empty-state {
        padding: 2rem;
        text-align: center;
        color: var(--tc-grey);
      }

      .mat-mdc-paginator,
      .mat-paginator {
        background: #ffffff;
        padding: 0.45rem 0.75rem;
        border-top: 1px solid rgba(23, 33, 43, 0.08);
      }

      :host ::ng-deep .mat-mdc-paginator-container {
        min-height: 48px;
        justify-content: flex-end;
        gap: 0.5rem;
      }

      :host ::ng-deep .mat-mdc-paginator-page-size {
        align-items: center;
      }

      @media (max-width: 960px) {
        .table-container {
          min-width: 100%;
        }
      }

      @media (max-width: 768px) {
        .resource-table {
          min-width: 920px;
        }
      }
    `
  ],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class ResourceTableComponent implements OnInit, OnChanges {
  @Input() data!: Page<Resource>;
  @Input() isLoading = false;
  @Output() pageChange = new EventEmitter<{ page: number; size: number }>();
  @Output() sortChange = new EventEmitter<{ sort: string; order: 'asc' | 'desc' }>();
  @Output() viewResource = new EventEmitter<Resource>();
  @Output() editResource = new EventEmitter<Resource>();
  @Output() deleteResource = new EventEmitter<Resource>();

  displayedColumns: string[] = ['employeeId', 'firstName', 'lastName', 'email', 'primarySkill', 'experienceYears', 'status', 'mobile', 'actions'];
  dataSource: Resource[] = [];
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
