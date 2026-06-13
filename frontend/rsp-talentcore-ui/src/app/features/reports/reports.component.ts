import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';

@Component({
  standalone: true,
  selector: 'feature-reports',
  imports: [CommonModule],
  template: `
    <section class="placeholder-panel">
      <h1>Reports</h1>
      <p>Analytics and reporting tools will be available here once the feature is enabled.</p>
    </section>
  `,
  styles: [
    `
      .placeholder-panel {
        padding: 2rem;
        background: white;
        border-radius: 24px;
        box-shadow: var(--shadow-soft);
      }
      h1 {
        margin-top: 0;
      }
    `
  ]
})
export class ReportsComponent {}
