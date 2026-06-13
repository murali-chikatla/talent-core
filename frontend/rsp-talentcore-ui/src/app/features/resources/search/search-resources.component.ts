import { Component, OnDestroy, OnInit, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Subject, Subscription, catchError, finalize, of, switchMap, tap } from 'rxjs';
import { ResourcesService } from '../resources.service';
import { Page } from '../../users/models/pagination.model';
import { Resource } from '../models/resource.model';
import { ResourceFiltersComponent } from './resource-filters.component';
import { ResourceTableComponent } from './resource-table.component';
import { LoggingService } from '../../../core/services/logging.service';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { MatIconModule } from '@angular/material/icon';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatSelectModule } from '@angular/material/select';
import { MatTooltipModule } from '@angular/material/tooltip';
import { MatButtonModule } from '@angular/material/button';
import { MatDialog } from '@angular/material/dialog';
import { MatSnackBar, MatSnackBarModule } from '@angular/material/snack-bar';
import { ResourceDialogComponent } from './resource-dialog.component';
import { ResourceDeleteDialogComponent } from './resource-delete-dialog.component';

@Component({
  standalone: true,
  selector: 'feature-resources-search',
  imports: [
    CommonModule,
    ResourceFiltersComponent,
    ResourceTableComponent,
    MatProgressSpinnerModule,
    MatIconModule,
    MatFormFieldModule,
    MatSelectModule,
    MatTooltipModule,
    MatButtonModule,
    MatSnackBarModule
  ],
  template: `
    <section class="resources-search">
      <div class="results-panel">
        <div class="results-header">
          <div>
            <h1>Resource Management</h1>
            <p>Use backend filters to quickly locate resources across the enterprise.</p>
          </div>
          <div class="page-controls">
            <button mat-raised-button color="primary" class="create-button" type="button" (click)="openCreateResource()">
              Create Resource
            </button>
          </div>
        </div>

        <div class="filter-area">
          <app-resource-filters (search)="onFiltersSearch($event)" (reset)="onFiltersReset()"></app-resource-filters>
        </div>

        <div *ngIf="error()" class="error-state">
          <span class="material-symbols-outlined">error_outline</span>
          <div>
            <h3>Search failed</h3>
            <p>{{ error() }}</p>
          </div>
        </div>

        <div *ngIf="loading() && !error()" class="loading-state">
          <mat-progress-spinner mode="indeterminate" diameter="48"></mat-progress-spinner>
          <span>Loading resources...</span>
        </div>

        <app-resource-table
          *ngIf="!error()"
          [data]="data"
          [isLoading]="loading()"
          (pageChange)="onPageChange($event)"
          (sortChange)="onSortChange($event)"
          (viewResource)="openViewResource($event)"
          (editResource)="openEditResource($event)"
          (deleteResource)="openDeleteResource($event)">
        </app-resource-table>
      </div>
    </section>
  `,
  styles: [
    `
      .resources-search {
        display: grid;
        gap: 1rem;
        padding: 1rem 1.5rem 1.5rem;
      }

      .toolbar {
        display: flex;
        justify-content: space-between;
      }

      .results-panel {
        display: grid;
        gap: 0.9rem;
      }

      .filter-area {
        width: 100%;
      }

      .results-header {
        display: flex;
        align-items: center;
        justify-content: space-between;
        gap: 1rem;
        padding: 1.05rem 1.25rem;
        background: #ffffff;
        border-radius: 18px;
        box-shadow: var(--shadow-soft);
        border: 1px solid rgba(23, 33, 43, 0.06);
      }

      .results-header h1 {
        margin: 0;
        font-size: 1.65rem;
        line-height: 1.15;
      }

      .results-header p {
        margin: 0.35rem 0 0;
        color: var(--tc-grey);
      }

      .page-controls {
        display: flex;
        align-items: center;
        gap: 1rem;
      }

      .page-size-field {
        width: 140px;
      }

      .create-button {
        min-height: 40px;
        padding: 0 1.15rem;
        border-radius: 999px;
        background: var(--tc-red);
        color: #ffffff;
        box-shadow: 0 10px 22px rgba(201, 32, 47, 0.22);
        transition: transform 0.16s ease, box-shadow 0.16s ease, background 0.16s ease;
      }

      .create-button:hover {
        background: #b51c2a;
        box-shadow: 0 14px 28px rgba(201, 32, 47, 0.28);
        transform: translateY(-1px);
      }

      .create-button:focus-visible {
        outline: 3px solid rgba(201, 32, 47, 0.22);
        outline-offset: 2px;
      }

      .loading-state,
      .error-state {
        display: grid;
        grid-template-columns: auto 1fr;
        gap: 1rem;
        align-items: center;
        padding: 1.25rem 1.5rem;
        border-radius: 18px;
        background: white;
        box-shadow: var(--shadow-soft);
      }

      .loading-state {
        color: var(--tc-slate);
      }

      .error-state {
        border: 1px solid rgba(201, 32, 47, 0.16);
        color: #991b1b;
      }

      .error-state h3 {
        margin: 0 0 0.35rem;
      }

      @media (max-width: 900px) {
        .results-header {
          flex-direction: column;
          align-items: stretch;
        }
      }
    `
  ]
})
export class SearchResourcesComponent implements OnInit, OnDestroy {
  loading = signal(false);
  error = signal<string | null>(null);
  data: Page<Resource> = { items: [], total: 0, page: 0, size: 10 };
  page = 0;
  size = 10;
  sortBy?: string;
  sortDirection: 'ASC' | 'DESC' = 'ASC';
  filters: Partial<Resource> = {};

  private trigger$ = new Subject<void>();
  private subs = new Subscription();

  constructor(
    private resources: ResourcesService,
    private logger: LoggingService,
    private dialog: MatDialog,
    private snackBar: MatSnackBar
  ) {}

  ngOnInit() {
    this.subs.add(
      this.trigger$.pipe(
        tap(() => {
          this.loading.set(true);
          this.error.set(null);
        }),
        switchMap(() =>
          this.resources.search({
            page: this.page,
            size: this.size,
            sortBy: this.sortBy,
            sortDirection: this.sortDirection,
            filters: this.filters
          }).pipe(
            catchError(err => {
              this.logger.error('Resource search failed', err);
              if (err?.status !== 401 && err?.status !== 403 && err?.status !== 500) {
                this.error.set(err?.message || 'Unable to load resources.');
              }
              return of({ items: [], total: 0, page: this.page, size: this.size });
            }),
            finalize(() => this.loading.set(false))
          )
        )
      ).subscribe(result => {
        this.data = result;
        this.page = result.page;
        this.size = result.size;
      })
    );

    this.trigger$.next();
  }

  onFiltersSearch(filters: Partial<Resource>) {
    this.filters = filters;
    this.page = 0;
    this.trigger$.next();
  }

  onFiltersReset() {
    this.filters = {};
    this.page = 0;
    this.size = 10;
    this.sortBy = undefined;
    this.sortDirection = 'ASC';
    this.trigger$.next();
  }

  onPageChange(event: { page: number; size: number }) {
    this.page = event.page;
    this.size = event.size;
    this.trigger$.next();
  }

  onSortChange(event: { sort: string; order: 'asc' | 'desc' }) {
    this.sortBy = event.sort;
    this.sortDirection = event.order === 'asc' ? 'ASC' : 'DESC';
    this.trigger$.next();
  }

  openCreateResource(): void {
    const ref = this.dialog.open(ResourceDialogComponent, {
      width: '780px',
      maxWidth: '92vw',
      autoFocus: 'first-tabbable',
      data: { mode: 'create' }
    });

    this.subs.add(ref.afterClosed().subscribe(saved => {
      if (saved) {
        this.snackBar.open('Resource created successfully.', 'Close', { duration: 3000 });
        this.trigger$.next();
      }
    }));
  }

  openViewResource(resource: Resource): void {
    if (typeof resource.id !== 'number') return;
    this.dialog.open(ResourceDialogComponent, {
      width: '780px',
      maxWidth: '92vw',
      autoFocus: false,
      data: { mode: 'view', id: resource.id }
    });
  }

  openEditResource(resource: Resource): void {
    if (typeof resource.id !== 'number') return;
    const ref = this.dialog.open(ResourceDialogComponent, {
      width: '780px',
      maxWidth: '92vw',
      autoFocus: 'first-tabbable',
      data: { mode: 'edit', id: resource.id }
    });

    this.subs.add(ref.afterClosed().subscribe(saved => {
      if (saved) {
        this.snackBar.open('Resource updated successfully.', 'Close', { duration: 3000 });
        this.trigger$.next();
      }
    }));
  }

  openDeleteResource(resource: Resource): void {
    if (typeof resource.id !== 'number') return;
    const ref = this.dialog.open(ResourceDeleteDialogComponent, {
      width: '520px',
      maxWidth: '92vw',
      autoFocus: false,
      data: resource
    });

    this.subs.add(ref.afterClosed().subscribe(confirmed => {
      if (!confirmed || typeof resource.id !== 'number') return;

      this.loading.set(true);
      this.resources.delete(resource.id).pipe(
        finalize(() => this.loading.set(false))
      ).subscribe({
        next: () => {
          this.snackBar.open('Resource deleted successfully.', 'Close', { duration: 3000 });
          this.trigger$.next();
        },
        error: err => {
          this.logger.error('Resource delete failed', err);
          this.snackBar.open(this.errorMessage(err, 'Unable to delete resource.'), 'Close', { duration: 4000 });
        }
      });
    }));
  }

  private errorMessage(err: unknown, fallback: string): string {
    const response = err as { error?: { message?: string } | string; message?: string };
    if (typeof response.error === 'string') return response.error;
    return response.error?.message || response.message || fallback;
  }

  ngOnDestroy() {
    this.subs.unsubscribe();
  }
}
