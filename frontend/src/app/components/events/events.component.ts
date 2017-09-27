import { Component, ChangeDetectionStrategy, OnInit } from '@angular/core';
import { Store } from '@ngrx/store';
import { Observable } from 'rxjs/Observable';

import * as fromRoot from '../../store/reducers';
import { Event } from '../../models/event';
import { EventsService } from '../../services/events.service';

@Component({
  selector: 'app-events',
  templateUrl: './events.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class EventsComponent implements OnInit {
  events$: Observable<Event[]>;

  constructor(private store: Store<fromRoot.State>) {
    this.events$ = store.select(state => state.events.events);
  }

  ngOnInit() {
  }

}
