import { Component, OnInit } from '@angular/core';
import { Store } from '@ngrx/store';
import { Observable } from 'rxjs/Observable';

import { ComponentUnit } from '../../models/component';
import * as fromRoot from '../../store/reducers';
import * as ComponentUnits from '../../store/components/components.actions';

@Component({
  selector: 'app-components',
  templateUrl: './components.component.html',
  styleUrls: ['./components.component.css']
})
export class ComponentsComponent implements OnInit {
  fetching$: Observable<boolean>;
  error$: Observable<boolean>;
  componentUnits$: Observable<ComponentUnit[]>;


  constructor(private store: Store<fromRoot.State>) {
    this.fetching$ = store.select(fromRoot.isFetchingUsers);
    this.error$ = store.select(fromRoot.isFetchingUsersError);
    this.componentUnits$ = store.select(fromRoot.selectComponents);
  }

  ngOnInit() {
    this.store.dispatch(new ComponentUnits.FetchComponents());
  }
}
