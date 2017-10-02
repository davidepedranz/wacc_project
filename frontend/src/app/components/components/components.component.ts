import { Component, ChangeDetectionStrategy, OnInit } from '@angular/core';
import { Store } from '@ngrx/store';
import { Observable } from 'rxjs/Observable';

import { ComponentUnit } from '../../models/component';
import * as fromRoot from '../../store';
import * as ComponentUnits from '../../store/components/components.actions';

@Component({
  selector: 'app-components',
  templateUrl: './components.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class ComponentsComponent implements OnInit {
  fetching$: Observable<boolean>;
  error$: Observable<boolean>;
  componentUnits$: Observable<ComponentUnit[]>;

  constructor(private store: Store<fromRoot.State>) {

    // TODO!!!
    this.fetching$ = store.select(fromRoot.isLoginPending);
    this.error$ = store.select(fromRoot.isLoginError);
    this.componentUnits$ = store.select(fromRoot.selectComponents);
  }

  ngOnInit() {
    this.store.dispatch(new ComponentUnits.FetchComponents());
  }
}
