import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs/Observable';
import 'rxjs/add/operator/map';

import { Component } from '../models/component';

@Injectable()
export class ComponentsService {

  // TODO: inject from env
  private readonly BASE = '/api';

  constructor(private http: HttpClient) { }

  fetchComponents(): Observable<Component[]> {
    return this.http
      .get(this.BASE + '/v1/services')
      .map(response => response as Component[]);
  }

  deleteService(id: string): Observable<any> {
    return this.http
      .delete(this.BASE + '/v1/services/' + id);
  }
}
