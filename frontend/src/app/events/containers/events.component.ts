import { Component, ChangeDetectionStrategy, OnInit } from '@angular/core';
import { Store } from '@ngrx/store';
import { Observable } from 'rxjs/Observable';

import * as fromEvents from '../store';
import { EventsService } from '../services/events.service';
import { Event } from '../models/event';

@Component({
  selector: 'app-events',
  templateUrl: './events.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class EventsComponent implements OnInit {
  events$: Observable<Event[]>;

  constructor(private store: Store<fromEvents.State>) {
    this.events$ = store.select(fromEvents.getEvents);
  }

  ngOnInit() {
  }
}
