import { Component, OnInit, ChangeDetectionStrategy } from '@angular/core';
import { Store } from '@ngrx/store';
import { Observable } from 'rxjs/Observable';
import 'rxjs/add/operator/map';
import 'rxjs/add/operator/switchMap';

import { User } from '../../models/user.model';
import * as UsersActions from '../../store/users.actions';
import * as fromUsers from '../../store';

@Component({
  selector: 'app-user-add',
  templateUrl: './user-add.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class UserAddComponent implements OnInit {

  constructor() { }

  ngOnInit() { }
}
