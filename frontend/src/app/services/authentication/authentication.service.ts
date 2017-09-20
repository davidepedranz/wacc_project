import 'rxjs/add/observable/of';
import 'rxjs/add/operator/do';
import 'rxjs/add/operator/delay';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs/Observable';
import { Credentials } from '../../models/credentials';

@Injectable()
export class AuthenticationService {

  login(credentials: Credentials): Observable<string> {
    return Observable.of('fake-token').delay(1000);
  }

}
