import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';

import { FlexLayoutModule } from '@angular/flex-layout';
import { MaterialModule } from '../material.module';

import { NavbarComponent } from './components/navbar/navbar.component';
import { AppComponent } from './containers/app/app.component';
import { DashboardComponent } from './containers/dashboard/dashboard.component';

// TODO!
import { ButtonComponent } from './components/button/button.component';

@NgModule({
  imports: [
    CommonModule,
    RouterModule,
    FlexLayoutModule,
    MaterialModule
  ],
  declarations: [
    NavbarComponent,
    AppComponent,
    DashboardComponent,

    ButtonComponent
  ],
  exports: [
    AppComponent,
    DashboardComponent
  ]
})
export class CoreModule { }
