import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs/Observable';
import 'rxjs/add/operator/map';

import { Credentials } from '../models/credentials';

@Injectable()
export class AuthenticationService {

  // TODO: inject from env
  private readonly BASE = '/api';

  constructor(private http: HttpClient) { }

  login(credentials: Credentials): Observable<string> {
    return this.http
      .post(this.BASE + '/v1/login', credentials)
      .map(response => response['token'] as string);
  }
}
