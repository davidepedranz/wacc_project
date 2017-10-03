import { Component, ChangeDetectionStrategy, OnInit } from '@angular/core';
import { Store } from '@ngrx/store';
import { Observable } from 'rxjs/Observable';

import { Component as MyComponent } from '../models/component';
import * as ComponentUnits from '../store/components.actions';
import * as fromComponents from '../store';

@Component({
  selector: 'app-components',
  templateUrl: './components.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class ComponentsComponent implements OnInit {
  fetching$: Observable<boolean>;
  error$: Observable<boolean>;
  components$: Observable<MyComponent[]>;

  // TODO!!!
  constructor(private store: Store<fromComponents.State>) {
    this.fetching$ = Observable.of(false);
    this.error$ = Observable.of(false);
    this.components$ = store.select(fromComponents.getComponents);
  }

  ngOnInit() {
    this.store.dispatch(new ComponentUnits.FetchComponents());
  }
}
