import { Component, ChangeDetectionStrategy, OnInit, Input, Output, EventEmitter } from '@angular/core';
import { MdDialog } from '@angular/material';
import { DataSource } from '@angular/cdk/collections';
import { Store } from '@ngrx/store';
import { Observable } from 'rxjs/Observable';

import { User } from '../../models/user.model';
import { UserDeleteDialogComponent } from '../user-delete-dialog/user-delete-dialog.component';

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

  @Input()
  fetching: boolean;

  @Input()
  error: boolean;

  @Input()
  users$: Observable<User[]>;

  readonly displayedColumns = ['username', 'permissions', 'actions'];
  dataSource: UsersDataSource | null;

  @Output()
  deleteUser = new EventEmitter<string>();

  constructor(public dialog: MdDialog) { }

  ngOnInit() {
    this.dataSource = new UsersDataSource(this.users$);
  }

  onDeleteUser(username: string) {
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
