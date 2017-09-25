import { Component, OnInit } from '@angular/core';
import { Store } from '@ngrx/store';
import { Observable } from 'rxjs/Observable';

import { User } from '../../models/user';
import * as fromRoot from '../../store/reducers';

@Component({
  selector: 'app-users',
  templateUrl: './users.component.html',
  styleUrls: ['./users.component.css']
})
export class UsersComponent implements OnInit {
  fetching$: Observable<boolean>;
  error$: Observable<boolean>;
  users$: Observable<User[]>;

  constructor(private store: Store<fromRoot.State>) {
    this.fetching$ = store.select(fromRoot.isFetchingUsers);
    this.error$ = store.select(fromRoot.isFetchingUsersError);
    this.users$ = store.select(fromRoot.selectUsers);
  }

  ngOnInit() { }
}
