import { Injectable } from '@angular/core';

import 'rxjs/add/observable/of';
import 'rxjs/add/operator/do';
import 'rxjs/add/operator/delay';
import { Observable } from 'rxjs/Observable';

import { ComponentUnit } from '../models/component';

/** Constants used to fill up our data base. */
const STATUS = ['Running', 'Stop', 'Error', 'Starting'];
const NAMES = ['MongoDB', 'Java Web Application', 'Cassandra', 'Consul', 'RabbitMQ'];

@Injectable()
export class ComponentsService {

  constructor() {

  }

  private createNewRow(currentLength) {
    const name = NAMES[Math.round(Math.random() * (NAMES.length - 1))];
    const image = name + ' - ' + Math.round(Math.random() * 10) + '.' + Math.round(Math.random() * 10);
    const status = STATUS[Math.round(Math.random() * (STATUS.length - 1))];

    return {
      id: (currentLength + 1).toString(),
      name: name,
      status: status,
      image: image
    };
  }

  private createFakeComponents(count) {
    var data: ComponentUnit[] = [this.createNewRow(0)];
    for (var i = 1; i < count; i++) {
      data.push(this.createNewRow(data.length));
    }
    return data;
  }

  private readonly fakeComponents = this.createFakeComponents(100);

  fetchComponents(): Observable<ComponentUnit[]> {
    console.log(this.fakeComponents);
    return Observable.of(this.fakeComponents).delay(1000);
  }
}
