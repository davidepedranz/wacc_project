import { Injectable } from '@angular/core';
import { Http } from '@angular/http';

import { Observable } from 'rxjs/Observable';
import 'rxjs/add/operator/map';

import { Credentials } from '../models/credentials';

@Injectable()
export class AuthenticationService {
  private readonly BASE = '/api';

  constructor(private http: Http) { }

  login(credentials: Credentials): Observable<string> {
    return this.http
      .post(this.BASE + '/v1/login', credentials)
      .map(response => response.json().token as string);
  }
}
