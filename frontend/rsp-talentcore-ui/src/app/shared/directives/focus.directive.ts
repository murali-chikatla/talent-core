import { Directive, ElementRef, HostListener } from '@angular/core';

@Directive({ selector: '[appFocus]' })
export class FocusDirective {
  constructor(private el: ElementRef<HTMLElement>) {}

  @HostListener('focus') onFocus() {
    this.el.nativeElement.classList.add('focused');
  }

  @HostListener('blur') onBlur() {
    this.el.nativeElement.classList.remove('focused');
  }
}
