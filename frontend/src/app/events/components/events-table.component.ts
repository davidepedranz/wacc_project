import { Component, ChangeDetectionStrategy, OnInit, Input, Output, EventEmitter } from '@angular/core';
import { Observable } from 'rxjs/Observable';
import { DataSource } from '@angular/cdk/collections';

import { Event } from '../models/event';

// see: https://medium.com/@LewisGJ/ngrx-and-md-table-cea1bc9673ee
class EventsDataSource extends DataSource<Event> {

  constructor(private users$: Observable<Event[]>) {
    super();
  }

  connect(): Observable<Event[]> {
    return this.users$;
  }

  disconnect(): void {
  }
}

@Component({
  selector: 'app-events-table',
  templateUrl: './events-table.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class EventsTableComponent implements OnInit {

  @Input()
  events$: Observable<Event[]>;

  readonly displayedColumns = ['id', 'timestamp', 'component', 'action', 'description'];
  dataSource: EventsDataSource | null;

  constructor() { }

  ngOnInit() {
    this.dataSource = new EventsDataSource(this.events$);
  }

}
