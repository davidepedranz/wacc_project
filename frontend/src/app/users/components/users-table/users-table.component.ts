import { Component, ChangeDetectionStrategy, OnInit, Input, Output, EventEmitter } from '@angular/core';
import { MatDialog, MatCheckboxChange } from '@angular/material';
import { DataSource } from '@angular/cdk/collections';
import { Store } from '@ngrx/store';
import { Observable } from 'rxjs/Observable';

import { User } from '../../models/user.model';
import { UserDeleteDialogComponent } from '../user-delete-dialog/user-delete-dialog.component';
import { PERMISSIONS, PERMISSION_USER_READ, PERMISSION_USER_WRITE } from '../../services/users.service';

export interface ChangePermission {
  username: string;
  permission: string;
  status: boolean;
}

// see: https://medium.com/@LewisGJ/ngrx-and-md-table-cea1bc9673ee
export class UsersDataSource extends DataSource<User> {

  constructor(private users$: Observable<User[]>) {
    super();
  }

  connect(): Observable<User[]> {
    return this.users$;
  }

  disconnect(): void { }
}

@Component({
  selector: 'app-users-table',
  templateUrl: './users-table.component.html',
  styleUrls: ['./users-table.component.css'],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class UsersTableComponent implements OnInit {

  readonly ALL_PERMISSIONS = PERMISSIONS;

  @Input()
  currentUser: User | null;

  @Input()
  fetching: boolean;

  @Input()
  error: boolean;

  @Input()
  users$: Observable<User[]>;

  @Output()
  changePermission = new EventEmitter<ChangePermission>();

  @Output()
  deleteUser = new EventEmitter<string>();

  readonly displayedColumns = ['username', 'permissions', 'actions'];
  dataSource: UsersDataSource | null;

  constructor(public dialog: MatDialog) { }

  ngOnInit() {
    this.dataSource = new UsersDataSource(this.users$);
  }

  hasPrivilege(user: User, permission: string): boolean {
    return user.permissions.indexOf(permission) !== -1;
  }

  canEditUser(user: User): boolean {
    return this.hasPrivilege(this.currentUser, PERMISSION_USER_WRITE)
      && user.username !== this.currentUser.username;
  }

  canEditPermission(user: User, permission: string): boolean {
    return user.username !== this.currentUser.username
      || (permission !== PERMISSION_USER_READ && permission !== PERMISSION_USER_WRITE);
  }

  onChangePermission(user: User, permission: string, $event: MatCheckboxChange) {
    this.changePermission.emit({
      username: user.username,
      permission: permission,
      status: $event.checked
    });
  }

  onDeleteUser(user: User) {
    const username = user.username;
    const dialogRef = this.dialog.open(UserDeleteDialogComponent, {
      data: { username }
    });
    dialogRef.afterClosed().subscribe(result => {
      if (result) {
        this.deleteUser.emit(username);
      }
    });
  }
}
