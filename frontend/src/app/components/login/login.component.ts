import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { Observable } from 'rxjs/Observable';
import { Store } from '@ngrx/store';
import * as Authentication from '../../store/authentication/authentication.actions';
import * as fromRoot from '../../store/reducers';

@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.css']
})
export class LoginComponent implements OnInit {
  loginForm: FormGroup;
  token: Observable<string>;

  constructor(
    private fb: FormBuilder,
    private router: Router,
    private store: Store<fromRoot.State>
  ) {
    this.createForm();
    this.token = store.select(fromRoot.selectToken);
  }

  ngOnInit() {
    this.token.subscribe((value) => {
      console.log(`Login component got token ${value}`)
    });
  }

  createForm() {
    this.loginForm = this.fb.group({
      username: ['', Validators.required],
      password: ['', Validators.required]
    });
  }

  // TODO: separate presentation from logic
  // see: https://github.com/ngrx/platform/blob/master/example-app/app/auth/containers/login-page.component.ts
  onSubmit() {
    // request the application to perform the login action
    this.store.dispatch(new Authentication.Login({
      username: this.username.value,
      password: this.password.value
    }));
  }

  get username() {
    return this.loginForm.get('username');
  }

  get password() {
    return this.loginForm.get('password');
  }
}
