import { Component, OnInit, ViewChild } from '@angular/core';
import {DataSource} from '@angular/cdk/collections';
import {MdPaginator} from '@angular/material';
import {BehaviorSubject} from 'rxjs/BehaviorSubject';
import {Observable} from 'rxjs/Observable';
import 'rxjs/add/operator/startWith';
import 'rxjs/add/observable/merge';
import 'rxjs/add/operator/map';


@Component({
  selector: 'app-components-table',
  templateUrl: './components-table.component.html',
  styleUrls: ['./components-table.component.css']
})
export class ComponentsTableComponent implements OnInit {
  displayedColumns = ['id', 'name', 'status', 'image'];
  componentDatabase = new ComponentDatabase();
  dataSource: ComponentDataSource | null;

  @ViewChild(MdPaginator) paginator: MdPaginator;

  ngOnInit() {
    this.dataSource = new ComponentDataSource(this.componentDatabase, this.paginator);
  }
}

/** Constants used to fill up our data base. */
const STATUS = ['Running', 'Stop', 'Error', 'Starting'];
const NAMES = ['MongoDB', 'Java Web Application', 'Cassandra', 'Consul', 'RabbitMQ'];

export interface ComponentData {
  id: string;
  name: string;
  status: string;
  image: string;
}

/** An example database that the data source uses to retrieve data for the table. */
export class ComponentDatabase {
  /** Stream that emits whenever the data has been modified. */
  dataChange: BehaviorSubject<ComponentData[]> = new BehaviorSubject<ComponentData[]>([]);
  get data(): ComponentData[] { return this.dataChange.value; }

  constructor() {
    // Fill up the database with 100 users.
    for (let i = 0; i < 100; i++) { this.addRow(); }
  }

  /** Adds a new user to the database. */
  addRow() {
    const copiedData = this.data.slice();
    copiedData.push(this.createNewRow());
    this.dataChange.next(copiedData);
  }

  /** Builds and returns a new User. */
  private createNewRow() {
    const name =
        NAMES[Math.round(Math.random() * (NAMES.length - 1))];
    const image = name + ' - ' + Math.round(Math.random()*10) + '.' + Math.round(Math.random()*10);
    const status =
        STATUS[Math.round(Math.random() * (STATUS.length - 1))];

    return {
      id: (this.data.length + 1).toString(),
      name: name,
      status: status,
      image: image
    };
  }
}

/**
 * Data source to provide what data should be rendered in the table. Note that the data source
 * can retrieve its data in any way. In this case, the data source is provided a reference
 * to a common data base, ExampleDatabase. It is not the data source's responsibility to manage
 * the underlying data. Instead, it only needs to take the data and send the table exactly what
 * should be rendered.
 */
export class ComponentDataSource extends DataSource<any> {
  constructor(private _exampleDatabase: ComponentDatabase, private _paginator: MdPaginator) {
    super();
  }

  /** Connect function called by the table to retrieve one stream containing the data to render. */
  connect(): Observable<ComponentData[]> {
    const displayDataChanges = [
      this._exampleDatabase.dataChange,
      this._paginator.page,
    ];

    return Observable.merge(...displayDataChanges).map(() => {
      const data = this._exampleDatabase.data.slice();

      // Grab the page's slice of data.
      const startIndex = this._paginator.pageIndex * this._paginator.pageSize;
      return data.splice(startIndex, this._paginator.pageSize);
    });
  }

  disconnect() {}
}
