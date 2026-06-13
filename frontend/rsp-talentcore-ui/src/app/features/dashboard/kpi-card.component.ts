import { Component, Input } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MatCardModule } from '@angular/material/card';
import { MatIconModule } from '@angular/material/icon';

@Component({
  standalone: true,
  selector: 'app-kpi-card',
  imports: [CommonModule, MatCardModule, MatIconModule],
  template: `
    <mat-card class="kpi-card" [class.positive]="trendType === 'positive'" [class.negative]="trendType === 'negative'">
      <div class="card-head">
        <span class="material-symbols-outlined card-icon">{{ icon }}</span>
        <span class="card-label">{{ title }}</span>
      </div>
      <div class="card-value">{{ value }}</div>
      <div class="card-meta">{{ subtext }}</div>
      <div class="card-trend">
        <span class="material-symbols-outlined">trending_up</span>
        <span>{{ trend }}</span>
      </div>
    </mat-card>
  `,
  styles: [
    `
      .kpi-card {
        min-height: 142px;
        display: grid;
        gap: 0.75rem;
        border-radius: 18px;
        padding: 1rem;
        background: white;
        color: var(--tc-slate);
        box-shadow: 0 18px 48px rgba(23, 33, 43, 0.08);
        transition: transform 0.2s ease, box-shadow 0.2s ease;
      }

      .kpi-card:hover {
        transform: translateY(-2px);
        box-shadow: 0 22px 62px rgba(23, 33, 43, 0.12);
      }

      .card-head {
        display: flex;
        align-items: center;
        justify-content: space-between;
        gap: 0.75rem;
      }

      .card-icon {
        width: 40px;
        height: 40px;
        display: grid;
        place-items: center;
        border-radius: 12px;
        background: var(--tc-red-soft);
        color: var(--tc-red);
        box-shadow: inset 0 0 0 1px rgba(201, 32, 47, 0.08);
      }

      .card-label {
        font-size: 0.86rem;
        font-weight: 700;
        color: var(--tc-slate);
        text-transform: uppercase;
        letter-spacing: 0.04em;
      }

      .card-value {
        font-size: 2.1rem;
        font-weight: 700;
        line-height: 1;
      }

      .card-meta {
        color: var(--tc-grey);
        font-size: 0.92rem;
      }

      .card-trend {
        display: inline-flex;
        align-items: center;
        gap: 0.35rem;
        color: var(--tc-red);
        font-weight: 700;
      }

      .card-trend span.material-symbols-outlined {
        font-size: 1rem;
      }

      .positive .card-trend {
        color: #128a3d;
      }

      .negative .card-trend {
        color: #c9202f;
      }
    `
  ]
})
export class KpiCardComponent {
  @Input() title = '';
  @Input() value = '';
  @Input() subtext = '';
  @Input() icon = 'analytics';
  @Input() trend = '';
  @Input() trendType: 'positive' | 'negative' = 'positive';
}
