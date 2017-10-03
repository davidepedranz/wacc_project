import { Injectable } from '@angular/core';

import 'rxjs/add/observable/of';
import 'rxjs/add/operator/delay';
import { Observable } from 'rxjs/Observable';

import { Component } from '../models/component';

/** Constants used to fill up our data base. */
const STATUS = ['Running', 'Stop', 'Error', 'Starting'];
const NAMES = ['MongoDB', 'Java Web Application', 'Cassandra', 'Consul', 'RabbitMQ'];

@Injectable()
export class ComponentsService {
  private readonly fakeComponents: Component[];

  constructor() {
    this.fakeComponents = ComponentsService.createFakeComponents(10);
  }

  private static createFakeComponents(count) {
    return Array.from(Array(count).keys())
      .map(ComponentsService.createFakeComponent);
  }

  private static createFakeComponent(id: number) {
    const name = ComponentsService.randomElement(NAMES);
    // const image = name + ' - ' + Math.round(Math.random() * 10) + '.' + Math.round(Math.random() * 10);
    const image = 'a';
    const status = ComponentsService.randomElement(STATUS);

    return {
      id: (id + 1).toString(),
      name: name,
      status: status,
      image: image
    };
  }

  private static randomElement(array: string[]): string {
    return array[Math.floor(Math.random() * array.length)];
  }

  fetchComponents(): Observable<Component[]> {
    return Observable.of(this.fakeComponents).delay(1000);
  }
}
