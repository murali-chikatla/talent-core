import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';

@Component({
  standalone: true,
  selector: 'feature-administration',
  imports: [CommonModule],
  template: `
    <section class="placeholder-panel">
      <h1>Administration</h1>
      <p>Administration tools and system configuration controls are available here for authorized users.</p>
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
export class AdministrationComponent {}
