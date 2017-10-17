import { Component, ChangeDetectionStrategy } from '@angular/core';
import { Store } from '@ngrx/store';
import { Observable } from 'rxjs/Observable';
import 'rxjs/add/operator/map';
import 'rxjs/add/operator/switchMap';

import { UserWithPassword } from '../../models/user-with-password.model';
import * as UsersActions from '../../store/users.actions';
import * as fromUsers from '../../store';

@Component({
  selector: 'app-user-add',
  templateUrl: './user-add.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class UserAddComponent {
  pending$: Observable<boolean>;
  error$: Observable<string | null>;

  constructor(private usersStore: Store<fromUsers.State>) {
    this.pending$ = usersStore.select(fromUsers.isCreatingUser);
    this.error$ = usersStore.select(fromUsers.isCreatingUserError);
  }

  onSubmit($event: UserWithPassword): void {
    this.usersStore.dispatch(new UsersActions.CreateUser($event));
  }
}
