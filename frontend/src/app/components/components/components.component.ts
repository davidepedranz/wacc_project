import { Component, OnInit } from '@angular/core';
import { Store } from '@ngrx/store';

import { State } from '../../store/reducers';
import * as Users from '../../store/users/users.actions';

@Component({
  selector: 'app-components',
  templateUrl: './components.component.html',
  styleUrls: ['./components.component.css']
})
export class ComponentsComponent implements OnInit {

  ngOnInit() { }

}
