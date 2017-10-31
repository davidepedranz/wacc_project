import { Component, ChangeDetectionStrategy, OnInit, OnDestroy } from '@angular/core';
import { Store } from '@ngrx/store';
import { TimerObservable } from 'rxjs/observable/TimerObservable';
import { Subscription } from 'rxjs/Subscription';
import { Observable } from 'rxjs/Observable';
import 'rxjs/add/operator/map';

import { Component as MyComponent } from '../../models/component';
import * as ServicesActions from '../../store/services.actions';
import * as fromComponents from '../../store';

import { ServicesService } from '../../services/services.service';

@Component({
  selector: 'app-services',
  templateUrl: './services.component.html',
  styleUrls: ['./services.component.css'],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class ServicesComponent implements OnInit, OnDestroy {

  private subscription: Subscription;
  fetching$: Observable<boolean>;
  error$: Observable<boolean>;
  components$: Observable<MyComponent[]>;

  constructor(private store: Store<fromComponents.State>, private service: ServicesService) {
    this.fetching$ = Observable.of(false);
    this.error$ = Observable.of(false);
    this.components$ = store.select(fromComponents.getServices);
  }

  ngOnInit() {
    const timer = TimerObservable.create(0, 60 * 1000);
    this.subscription = timer.subscribe(t => this.loadServices());
  }

  ngOnDestroy() {
    this.subscription.unsubscribe();
  }

  onDeleteService(id: string) {
    this.service.deleteService(id)
      .map(_ => this.loadServices())
      .subscribe();
  }

  private loadServices() {
    this.store.dispatch(new ServicesActions.FetchComponents());
  }
}
