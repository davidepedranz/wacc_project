import { Component, OnInit, ChangeDetectionStrategy } from '@angular/core';
import { ActivatedRoute, ParamMap } from '@angular/router';
import { Store } from '@ngrx/store';
import { Observable } from 'rxjs/Observable';
import 'rxjs/add/operator/map';
import 'rxjs/add/operator/switchMap';

import { User } from '../../models/user';
import * as fromRoot from '../../store/reducers';
import * as UsersActions from '../../store/users/users.actions';

@Component({
  selector: 'app-user-edit',
  templateUrl: './user-edit.component.html',
  styleUrls: ['./user-edit.component.css'],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class UserEditComponent implements OnInit {
  user$: Observable<User>;

  constructor(
    private route: ActivatedRoute,
    private store: Store<fromRoot.State>
  ) {
    this.user$ = this.route.paramMap
      .map((params: ParamMap) => params.get('username'))
      .switchMap((username: string) => store.select(fromRoot.selectUserByUsername(username)));
  }

  ngOnInit() {
    this.store.dispatch(new UsersActions.FetchUsers());
  }

}
