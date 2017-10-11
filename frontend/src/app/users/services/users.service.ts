import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs/Observable';
import 'rxjs/add/observable/of';
import 'rxjs/add/operator/map';
import 'rxjs/add/operator/switchMap';

import { User } from '../models/user.model';
import { UserWithPassword } from '../models/user-with-password.model';

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

  createUser(user: UserWithPassword): Observable<{}> {
    return this.http
      .post(this.BASE + '/v1/users', user)
      .switchMap(_ => Observable.of());
  }

  deleteUser(username: string): Observable<{}> {
    return this.http
      .delete(this.BASE + `/v1/users/${username}`)
      .switchMap(_ => Observable.of());
  }

  addPermission(username: string, permission: string): Observable<{}> {
    return this.http
      .post(this.BASE + `/v1/users/${username}/permissions/${permission}`, null)
      .switchMap(_ => Observable.of());
  }

  removePermission(username: string, permission: string): Observable<{}> {
    return this.http
      .delete(this.BASE + `/v1/users/${username}/permissions/${permission}`)
      .switchMap(_ => Observable.of());
  }
}
