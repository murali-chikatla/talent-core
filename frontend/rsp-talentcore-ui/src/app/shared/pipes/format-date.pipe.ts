import { Pipe, PipeTransform } from '@angular/core';
import { formatDate } from '@angular/common';

@Pipe({ name: 'formatDate' })
export class FormatDatePipe implements PipeTransform {
  transform(value: string | number | Date, fmt = 'mediumDate'): string {
    return formatDate(value, fmt, 'en-US');
  }
}
