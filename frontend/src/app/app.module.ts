import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';

import { RouterModule } from '@angular/router';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { FlexLayoutModule } from '@angular/flex-layout';

import { StoreModule } from '@ngrx/store';
import { EffectsModule } from '@ngrx/effects';
import { StoreDevtoolsModule } from '@ngrx/store-devtools';

import { environment } from '../environments/environment';

import { reducers } from './store';
import { ComponentsService } from './services/components.service';
import { AppRoutes } from './app.routing';

import { MaterialModule } from './material.module';
import { AppComponent } from './components/app/app.component';
import { NavbarComponent } from './components/navbar/navbar.component';
import { DashboardComponent } from './components/dashboard/dashboard.component';
import { ComponentsComponent } from './components/components/components.component';
import { ComponentsTableComponent } from './components/components-table/components-table.component';
import { ButtonComponent } from './components/button/button.component';


// modules (NB: some are lazy-loaded and are thus not listed here)
import { AuthenticationModule } from './authentication/authentication.module';

@NgModule({
  declarations: [
    AppComponent,
    DashboardComponent,
    NavbarComponent,
    ComponentsComponent,
    ComponentsTableComponent,
    ButtonComponent
  ],
  imports: [
    BrowserModule,
    BrowserAnimationsModule,
    FlexLayoutModule,
    MaterialModule,
    RouterModule.forRoot(AppRoutes),
    StoreModule.forRoot(reducers),
    EffectsModule.forRoot([]),
    !environment.production ? StoreDevtoolsModule.instrument({ maxAge: 50 }) : [],

    AuthenticationModule
  ],
  providers: [
    ComponentsService
  ],
  bootstrap: [
    AppComponent
  ]
})
export class AppModule { }
