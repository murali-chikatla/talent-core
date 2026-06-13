import { Component, OnDestroy, OnInit, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Subject, Subscription, catchError, finalize, of, switchMap, tap } from 'rxjs';
import { UsersService } from '../users.service';
import { Page } from '../models/pagination.model';
import { User, UserFilter } from '../models/user.model';
import { UserFiltersComponent } from './user-filters.component';
import { UserTableComponent } from './user-table.component';
import { LoggingService } from '../../../core/services/logging.service';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { MatIconModule } from '@angular/material/icon';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatSelectModule } from '@angular/material/select';
import { MatTooltipModule } from '@angular/material/tooltip';
import { MatButtonModule } from '@angular/material/button';
import { MatDialog, MatDialogModule } from '@angular/material/dialog';
import { MatSnackBar, MatSnackBarModule } from '@angular/material/snack-bar';
import { UserCreateDialogComponent } from './user-create-dialog.component';

@Component({
  standalone: true,
  selector: 'feature-users-search',
  imports: [CommonModule, UserFiltersComponent, UserTableComponent, MatProgressSpinnerModule, MatIconModule, MatFormFieldModule, MatSelectModule, MatTooltipModule, MatButtonModule, MatDialogModule, MatSnackBarModule],
  templateUrl: './search-users.component.html',
  styleUrls: ['./search-users.component.scss']
})
export class SearchUsersComponent implements OnInit, OnDestroy {
  private users = inject(UsersService);
  private logger = inject(LoggingService);
  private dialog = inject(MatDialog);
  private snackBar = inject(MatSnackBar);

  loading = signal(false);
  error = signal<string | null>(null);
  data: Page<User> = { items: [], total: 0, page: 0, size: 10 };
  page = 0;
  size = 10;
  sortBy?: string;
  sortDirection: 'ASC' | 'DESC' = 'ASC';
  filters: UserFilter = {};

  private trigger$ = new Subject<void>();
  private subs = new Subscription();

  ngOnInit() {
    this.subs.add(
      this.trigger$.pipe(
        tap(() => {
          this.loading.set(true);
          this.error.set(null);
        }),
        switchMap(() =>
          this.users.search({
            page: this.page,
            size: this.size,
            sortBy: this.sortBy,
            sortDirection: this.sortDirection,
            filters: this.filters
          }).pipe(
            catchError(err => {
              this.logger.error('User search failed', err);
              if (err?.status !== 401 && err?.status !== 403) {
                this.error.set(err?.message || 'Unable to load users.');
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

  onFiltersSearch(filters: UserFilter) {
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

  openCreateUser(): void {
    const ref = this.dialog.open(UserCreateDialogComponent, {
      width: '760px',
      maxWidth: '92vw',
      autoFocus: 'first-tabbable'
    });

    this.subs.add(ref.afterClosed().subscribe(created => {
      if (created) {
        this.snackBar.open('User created successfully.', 'Close', { duration: 3000 });
        this.trigger$.next();
      }
    }));
  }

  ngOnDestroy() {
    this.subs.unsubscribe();
  }
}
