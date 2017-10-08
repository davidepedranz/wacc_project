import { Component, ChangeDetectionStrategy, AfterViewInit, Input, Output, EventEmitter, ViewChild, ElementRef } from '@angular/core';
import { AbstractControl, FormGroup, FormBuilder, Validators } from '@angular/forms';

import { Credentials } from '../models/credentials';

@Component({
  selector: 'app-login-form',
  templateUrl: './login-form.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class LoginFormComponent implements AfterViewInit {

  @Input()
  set pending(isPending: boolean) {
    if (isPending) {
      this.form.disable();
    } else {
      this.form.enable();
    }
  }

  @Input()
  error: boolean;

  @Output()
  submitted = new EventEmitter<Credentials>();

  @ViewChild('username')
  private usernameRef: ElementRef;

  form: FormGroup;

  constructor(private fb: FormBuilder) {
    this.createForm();
  }

  ngAfterViewInit(): void {
    this.usernameRef.nativeElement.focus();
  }

  createForm() {
    this.form = this.fb.group({
      username: ['', Validators.required],
      password: ['', Validators.required]
    });
  }

  get username() {
    return this.form.get('username');
  }

  get password() {
    return this.form.get('password');
  }

  invalid(field: AbstractControl) {
    return field.invalid && (field.dirty || field.touched);
  }

  submit() {
    this.submitted.emit({
      username: this.username.value,
      password: this.password.value
    });
    this.usernameRef.nativeElement.focus();
  }
}
