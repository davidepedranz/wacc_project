import { Component, ChangeDetectionStrategy, OnInit } from '@angular/core';
import { Store } from '@ngrx/store';
import { Observable } from 'rxjs/Observable';
import 'rxjs/add/operator/map';

import { Component as MyComponent } from '../models/component';
import * as ComponentUnits from '../store/components.actions';
import * as fromComponents from '../store';

import { ComponentsService } from '../services/components.service';

@Component({
  selector: 'app-components',
  templateUrl: './components.component.html',
  styleUrls: ['./components.component.css'],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class ComponentsComponent implements OnInit {
  fetching$: Observable<boolean>;
  error$: Observable<boolean>;
  components$: Observable<MyComponent[]>;

  constructor(private store: Store<fromComponents.State>, private service: ComponentsService) {
    this.fetching$ = Observable.of(false);
    this.error$ = Observable.of(false);
    this.components$ = store.select(fromComponents.getComponents);
  }

  ngOnInit() {
    this.reloadServices();
  }

  onDeleteService(id: string) {
    this.service.deleteService(id)
      .map(_ => this.reloadServices())
      .subscribe();
  }

  private reloadServices() {
    this.store.dispatch(new ComponentUnits.FetchComponents());
  }
}
