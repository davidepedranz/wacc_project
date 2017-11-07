import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { StoreModule, ActionReducerMap } from '@ngrx/store';
import { EffectsModule } from '@ngrx/effects';

import { FlexLayoutModule } from '@angular/flex-layout';
import { MaterialModule } from '../material.module';

import { routes } from './services.routing';
import { reducers } from './store';
import { FormsModule } from '@angular/forms';

import { ServicesEffects } from './store/services.effects';
import { ServicesService } from './services/services.service';
import { ServicesComponent } from './containers/services/services.component';
import { AddServiceComponent } from './containers/add-service/add-service.component';
import { UpdateServiceComponent } from './containers/update-service/update-service.component';
import { ComponentsTableComponent } from './components/services-table/services-table.component';
import { DockerServiceFormComponent } from './components/docker-service-form/docker-service-form.component';
import { UpdateDockerFormComponent } from './components/update-docker-form/update-docker-form.component';

@NgModule({
  imports: [
    CommonModule,
    RouterModule.forChild(routes),
    StoreModule.forFeature('services', reducers),
    EffectsModule.forFeature([ServicesEffects]),
    FlexLayoutModule,
    MaterialModule,
    FormsModule
  ],
  declarations: [
    ServicesComponent,
    AddServiceComponent,
    UpdateServiceComponent,
    ComponentsTableComponent,
    DockerServiceFormComponent,
    UpdateDockerFormComponent
  ],
  providers: [
    ServicesService
  ]
})
export class ServicesModule { }
