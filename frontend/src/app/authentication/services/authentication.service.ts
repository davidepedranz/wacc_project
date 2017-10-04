import { Injectable } from '@angular/core';

import 'rxjs/add/observable/of';
import 'rxjs/add/operator/do';
import 'rxjs/add/operator/delay';
import { Observable } from 'rxjs/Observable';

import { Credentials } from '../models/credentials';

@Injectable()
export class AuthenticationService {

  login({ username, password }: Credentials): Observable<string> {
    if (username === 'admin') {
      return Observable.of('fake-token').delay(1000);
    } else {
      return Observable.of('fake-token').delay(1000).map(_ => { throw Error('Some error'); });
    }
  }
}
