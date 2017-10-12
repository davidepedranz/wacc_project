import { Component, ChangeDetectionStrategy, AfterViewInit, Input, Output, EventEmitter, ViewChild, ElementRef } from '@angular/core';
import { AbstractControl, FormGroup, FormBuilder, Validators } from '@angular/forms';

import { UserWithPassword } from '../../models/user-with-password.model';
import { PERMISSIONS } from '../../services/users.service';

@Component({
  selector: 'app-user-form',
  templateUrl: './user-form.component.html',
  styleUrls: ['./user-form-component.css'],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class UserFormComponent implements AfterViewInit {

  readonly ALL_PERMISSIONS = PERMISSIONS;

  @Input()
  set pending(isPending: boolean) {
    if (isPending) {
      this.form.disable();
    } else {
      this.form.enable();
    }
  }

  @Input()
  error: string;

  @Output()
  submitted = new EventEmitter<UserWithPassword>();

  @ViewChild('usernameRef')
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
      username: ['', Validators.compose([Validators.required, Validators.minLength(5), Validators.maxLength(30)])],
      passwords: this.fb.group({
        password: ['', Validators.compose([Validators.required, Validators.minLength(8), Validators.maxLength(100)])],
        confirmPassword: ['', Validators.required]
      }, { validator: this.checkPasswords }),
      permissions: this.buildPermissions()
    });
  }

  buildPermissions() {
    const arr = this.ALL_PERMISSIONS.map(permission => {
      return this.fb.control(false);
    });
    return this.fb.array(arr);
  }

  checkPasswords(group: FormGroup) {
    const password = group.controls.password.value;
    const confirmPassword = group.controls.confirmPassword.value;
    return password === confirmPassword ? null : { notSame: true };
  }

  get username() {
    return this.form.get('username');
  }

  get passwords() {
    return this.form.get('passwords');
  }

  get password() {
    return this.passwords.get('password');
  }

  get confirmPassword() {
    return this.passwords.get('confirmPassword');
  }

  get permissions() {
    return this.form.get('permissions');
  }

  invalid(field: AbstractControl) {
    return field.invalid && (field.dirty || field.touched);
  }

  submit() {
    this.submitted.emit({
      username: this.username.value,
      password: this.password.value,
      permissions: this.permissions.value.reduce((acc, selected, i) => selected ? [...acc, this.ALL_PERMISSIONS[i]] : acc, [])
    });
    this.usernameRef.nativeElement.focus();
  }
}
