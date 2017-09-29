import { Component, OnInit, ViewChild, ChangeDetectionStrategy, Input } from '@angular/core';
import { MdPaginator } from '@angular/material';
import { DataSource } from '@angular/cdk/collections';

import 'rxjs/add/operator/startWith';
import 'rxjs/add/observable/merge';
import 'rxjs/add/operator/map';
import { Observable } from 'rxjs/Observable';
import { BehaviorSubject } from 'rxjs/BehaviorSubject';

import { ComponentUnit } from '../../models/component';
import { ComponentsService } from '../../services/components.service';


/** An example database that the data source uses to retrieve data for the table. */
export class ComponentPagingDatabase {
  /** Stream that emits whenever the data has been modified. */
  dataChange: BehaviorSubject<ComponentUnit[]> = new BehaviorSubject<ComponentUnit[]>([]);
  get data(): ComponentUnit[] { return this.dataChange.value; }

  constructor(private componentUnits$: Observable<ComponentUnit[]>) {
  }

  /** Adds a new user to the database. */
  addRow(newData) {
    const copiedData = this.data.slice();
    copiedData.push(newData);
    this.dataChange.next(copiedData);
  }

  readyCb() {
    this.componentUnits$.forEach(function(element) {
      this.addRow(element);
    });
  }
}

/**
 * Data source to provide what data should be rendered in the table. Note that the data source
 * can retrieve its data in any way. In this case, the data source is provided a reference
 * to a common data base, ExampleDatabase. It is not the data source's responsibility to manage
 * the underlying data. Instead, it only needs to take the data and send the table exactly what
 * should be rendered.
 */
export class ComponentDataSource extends DataSource<ComponentUnit> {
  constructor(private componentUnits$: Observable<ComponentUnit[]>, private _paginator: MdPaginator) {
    super();
  }

  /** Connect function called by the table to retrieve one stream containing the data to render. */
  connect(): Observable<ComponentUnit[]> {
    return this.componentUnits$;
    // const displayDataChanges = [
    //   this.componentDatabase.dataChange,
    //   this._paginator.page,
    // ];
    //
    // this.componentDatabase.readyCb();
    //
    // return Observable.merge(...displayDataChanges).map(() => {
    //   const data = this.componentDatabase.data.slice();
    //
    //   console.log(data);
    //   // Grab the page's slice of data.
    //   const startIndex = this._paginator.pageIndex * this._paginator.pageSize;
    //   return data.splice(startIndex, this._paginator.pageSize);
    // });
  }

  disconnect() { }
}

@Component({
  selector: 'app-components-table',
  templateUrl: './components-table.component.html',
  styleUrls: ['./components-table.component.css'],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class ComponentsTableComponent implements OnInit {
  @Input()
  fetching: boolean;

  @Input()
  error: boolean;

  @Input()
  componentUnits$: Observable<ComponentUnit[]>;

  displayedColumns = ['id', 'name', 'status', 'image'];
  dataSource: ComponentDataSource | null;

  @ViewChild(MdPaginator) paginator: MdPaginator;

  ngOnInit() {
    this.dataSource = new ComponentDataSource(this.componentUnits$, this.paginator);
  }
}

