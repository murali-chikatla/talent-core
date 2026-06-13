import { CommonModule } from '@angular/common';
import { Component, Inject } from '@angular/core';
import { MAT_DIALOG_DATA, MatDialogModule, MatDialogRef } from '@angular/material/dialog';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { Resource } from '../models/resource.model';

@Component({
  standalone: true,
  selector: 'app-resource-delete-dialog',
  imports: [CommonModule, MatButtonModule, MatDialogModule, MatIconModule],
  template: `
    <h2 mat-dialog-title>Delete Resource</h2>

    <mat-dialog-content class="dialog-content">
      <div class="dialog-icon">
        <span class="material-symbols-outlined">delete</span>
      </div>
      <div>
        <p class="dialog-title">{{ resourceName }}</p>
        <p class="dialog-copy">This resource will be removed from active results.</p>
      </div>
    </mat-dialog-content>

    <mat-dialog-actions align="end">
      <button mat-button mat-dialog-close type="button">Cancel</button>
      <button mat-raised-button color="warn" type="button" (click)="confirm()">Delete</button>
    </mat-dialog-actions>
  `,
  styles: [
    `
      .dialog-content {
        display: grid;
        grid-template-columns: auto 1fr;
        gap: 1rem;
        width: min(440px, 82vw);
        align-items: center;
      }

      .dialog-icon {
        width: 48px;
        height: 48px;
        display: grid;
        place-items: center;
        border-radius: 14px;
        background: #fff2f3;
        color: var(--tc-red);
      }

      .dialog-title {
        margin: 0;
        font-weight: 800;
        color: var(--tc-slate);
      }

      .dialog-copy {
        margin: 0.3rem 0 0;
        color: var(--tc-grey);
      }

      button[color='warn'] {
        background: var(--tc-red);
        color: #ffffff;
      }
    `
  ]
})
export class ResourceDeleteDialogComponent {
  constructor(
    @Inject(MAT_DIALOG_DATA) public resource: Resource,
    private dialogRef: MatDialogRef<ResourceDeleteDialogComponent, boolean>
  ) {}

  get resourceName(): string {
    const name = [this.resource.firstName, this.resource.lastName].filter(Boolean).join(' ');
    return name ? `${name} (${this.resource.employeeId})` : this.resource.employeeId;
  }

  confirm(): void {
    this.dialogRef.close(true);
  }
}
