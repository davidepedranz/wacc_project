import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { StoreModule, ActionReducerMap } from '@ngrx/store';
import { EffectsModule } from '@ngrx/effects';

import { FlexLayoutModule } from '@angular/flex-layout';
import { MaterialModule } from '../material.module';

import { routes } from './components.routing';
import { reducers } from './store';

import { ComponentsEffects } from './store/components.effects';
import { ComponentsService } from './services/components.service';
import { ComponentsComponent } from './containers/components.component';
import { ComponentsTableComponent } from './components/components-table.component';

@NgModule({
  imports: [
    CommonModule,
    RouterModule.forChild(routes),
    StoreModule.forFeature('components', reducers),
    EffectsModule.forFeature([ComponentsEffects]),
    FlexLayoutModule,
    MaterialModule
  ],
  declarations: [
    ComponentsComponent,
    ComponentsTableComponent
  ],
  providers: [
    ComponentsService
  ]
})
export class ComponentsModule { }
