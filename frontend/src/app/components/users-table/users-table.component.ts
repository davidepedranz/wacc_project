import { Component, ChangeDetectionStrategy, OnInit, Input, Output, EventEmitter } from '@angular/core';
import { DataSource } from '@angular/cdk/collections';
import { Store } from '@ngrx/store';
import { Observable } from 'rxjs/Observable';

import { User } from '../../models/user';

// see: https://medium.com/@LewisGJ/ngrx-and-md-table-cea1bc9673ee
export class UsersDataSource extends DataSource<User> {

  constructor(private users$: Observable<User[]>) {
    super();
  }

  connect(): Observable<User[]> {
    return this.users$;
  }

  disconnect(): void {
  }
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

  readonly displayedColumns = ['id', 'username', 'name', 'permissions'];
  dataSource: UsersDataSource | null;

  // @Output()
  // submitted = new EventEmitter<Credentials>();

  constructor() { }

  ngOnInit() {
    this.dataSource = new UsersDataSource(this.users$);
  }

}
