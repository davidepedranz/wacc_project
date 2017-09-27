import { Injectable } from '@angular/core';

import 'rxjs/add/observable/of';
import 'rxjs/add/operator/do';
import 'rxjs/add/operator/delay';
import { Observable } from 'rxjs/Observable';

import { User } from '../models/user';

@Injectable()
export class UsersService {

  private readonly fakeUsers = [
    {
      id: '03234-xx',
      name: 'Mario Rossi',
      username: 'mario',
      privileges: ['read-a', 'write-b']
    },
    {
      id: '03214-tt',
      name: 'John Bon',
      username: 'john',
      privileges: ['read-b', 'write-b']
    },
    {
      id: '00000-zt',
      name: 'Mr. G',
      username: 'gg',
      privileges: ['read-b', 'write-b']
    }
  ];

  fetchUsers(): Observable<User[]> {
    return Observable.of(this.fakeUsers).delay(1000);
  }
}
