import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs/Observable';
import 'rxjs/add/observable/of';
import 'rxjs/add/observable/throw';
import 'rxjs/add/operator/map';
import 'rxjs/add/operator/catch';
import 'rxjs/add/operator/switchMap';

import { User } from '../models/user.model';
import { UserWithPassword } from '../models/user-with-password.model';

// list of permissions available for the users
export const PERMISSION_USER_READ = 'users.read';
export const PERMISSION_USER_WRITE = 'users.write';
export const PERMISSIONS = [PERMISSION_USER_READ, PERMISSION_USER_WRITE];

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
      .post(this.BASE + '/v1/users', user, { responseType: 'text' })
      .catch(error => Observable.throw('code-' + error.status));
  }

  deleteUser(username: string): Observable<{}> {
    return this.http
      .delete(this.BASE + `/v1/users/${username}`);
  }

  addPermission(username: string, permission: string): Observable<{}> {
    return this.http
      .post(this.BASE + `/v1/users/${username}/permissions/${permission}`, null, { responseType: 'text' });
  }

  removePermission(username: string, permission: string): Observable<{}> {
    return this.http
      .delete(this.BASE + `/v1/users/${username}/permissions/${permission}`);
  }
}
