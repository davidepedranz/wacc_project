import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs/Observable';
import 'rxjs/add/observable/of';
import 'rxjs/add/operator/delay';

import { User } from '../models/user.model';

@Injectable()
export class UsersService {

  // TODO: inject from env
  private readonly BASE = '/api';

  constructor(private http: HttpClient) { }

  fetchUsers(): Observable<User[]> {
    return this.http
      .get(this.BASE + '/v1/users')
      .map(response => response as User[]);
  }
}
