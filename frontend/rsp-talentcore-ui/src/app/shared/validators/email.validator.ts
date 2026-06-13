import { AbstractControl, ValidationErrors } from '@angular/forms';

export function emailValidator(control: AbstractControl): ValidationErrors | null {
  const value = control.value;
  if (!value) return null;
  const valid = /^[^@\s]+@[^@\s]+\.[^@\s]+$/.test(value);
  return valid ? null : { email: true };
}
