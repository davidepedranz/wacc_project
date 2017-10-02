import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { StoreModule, ActionReducerMap } from '@ngrx/store';
import { EffectsModule } from '@ngrx/effects';

import { FlexLayoutModule } from '@angular/flex-layout';
import { MaterialModule } from '../material.module';

import { routes } from './events.routing';
import { reducers } from './store';

import { EventsEffects } from './store/events.effects';
import { EventsService } from './services/events.service';
import { EventsComponent } from './containers/events.component';
import { EventsTableComponent } from './components/events-table.component';

@NgModule({
  imports: [
    CommonModule,
    RouterModule.forChild(routes),
    StoreModule.forFeature('events', reducers),
    EffectsModule.forFeature([EventsEffects]),
    FlexLayoutModule,
    MaterialModule
  ],
  declarations: [
    EventsComponent,
    EventsTableComponent
  ],
  providers: [
    EventsService
  ]
})
export class EventsModule { }
