import { Component, OnInit, ViewChild, ChangeDetectionStrategy, Input } from '@angular/core';
import { DataSource } from '@angular/cdk/collections';
import { Store } from '@ngrx/store';
import { Observable } from 'rxjs/Observable';

import { Component as MyComponent } from '../models/component';

// see: https://medium.com/@LewisGJ/ngrx-and-md-table-cea1bc9673ee
export class ComponentDataSource extends DataSource<MyComponent> {

  constructor(private components$: Observable<MyComponent[]>) {
    super();
  }

  connect(): Observable<MyComponent[]> {
    return this.components$;
  }

  disconnect(): void { }
}

@Component({
  selector: 'app-components-table',
  templateUrl: './components-table.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class ComponentsTableComponent implements OnInit {

  @Input()
  fetching: boolean;

  @Input()
  error: boolean;

  @Input()
  components$: Observable<MyComponent[]>;

  readonly displayedColumns = ['id', 'name', 'mode', 'image', 'status', 'actions'];
  dataSource: ComponentDataSource | null;

  ngOnInit() {
    this.dataSource = new ComponentDataSource(this.components$);
  }
}
