import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs/Observable';
import 'rxjs/add/operator/map';

import { Credentials } from '../models/credentials';
import { Token } from '../models/token';
import { User } from '../../users/models/user.model';

@Injectable()
export class AuthenticationService {
  private readonly BASE = '/api';

  constructor(private http: HttpClient) { }

  login(credentials: Credentials): Observable<Token> {
    return this.http
      .post(this.BASE + '/v1/login', credentials)
      .map(response => response as Token);
  }

  me(): Observable<User> {
    return this.http
      .get(this.BASE + '/v1/users/@me')
      .map(response => response as User);
  }
}
