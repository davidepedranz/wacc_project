import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { StoreModule, ActionReducerMap } from '@ngrx/store';
import { EffectsModule } from '@ngrx/effects';

import { FlexLayoutModule } from '@angular/flex-layout';
import { MaterialModule } from '../material.module';

import { routes } from './components.routing';
import { reducers } from './store';
import { FormsModule } from '@angular/forms';

import { ComponentsEffects } from './store/components.effects';
import { ComponentsService } from './services/components.service';
import { ComponentsComponent } from './containers/components.component';
import { ComponentsTableComponent } from './components/components-table.component';
import { HttpModule } from '@angular/http';
import { DockerServiceFormComponent } from './docker-service-form/docker-service-form.component'
import { DeleteDockerServiceComponent } from './delete-docker-service/delete-docker-service.component';
import { AddServiceComponent } from './add-service/add-service.component';
import { UpdateDockerServiceComponent } from './update-docker-service/update-docker-service.component';
import { UpdateDockerFormComponent } from './update-docker-form/update-docker-form.component';

@NgModule({
  imports: [
    HttpModule,
    CommonModule,
    RouterModule.forChild(routes),
    StoreModule.forFeature('components', reducers),
    EffectsModule.forFeature([ComponentsEffects]),
    FlexLayoutModule,
    MaterialModule,
    FormsModule
  ],
  declarations: [
    ComponentsComponent,
    ComponentsTableComponent,
    DockerServiceFormComponent,
    DeleteDockerServiceComponent,
    AddServiceComponent,
    UpdateDockerServiceComponent,
    UpdateDockerFormComponent,

  ],
  providers: [
    ComponentsService
  ]
})
export class ComponentsModule { }
