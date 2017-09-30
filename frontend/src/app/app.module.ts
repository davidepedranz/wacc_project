import { BrowserModule } from '@angular/platform-browser';
import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { FlexLayoutModule } from '@angular/flex-layout';

import { StoreModule } from '@ngrx/store';
import { EffectsModule } from '@ngrx/effects';
import { StoreDevtoolsModule } from '@ngrx/store-devtools';

import { environment } from '../environments/environment';

import { reducers } from './store/reducers';
import { AuthenticationEffects } from './store/authentication/authentication.effects';
import { UsersEffects } from './store/users/users.effects';
import { EventsEffects } from './store/events/events.effects';
import { AuthenticationGuard } from './guards/authentication.guard';
import { AuthenticationService } from './services/authentication.service';
import { TokenService } from './services/token.service';
import { UsersService } from './services/users.service';
import { ComponentsService } from './services/components.service';
import { EventsService } from './services/events.service';
import { AppRoutes } from './app.routing';

import { MaterialModule } from './material.module';
import { AppComponent } from './components/app/app.component';
import { LoginComponent } from './components/login/login.component';
import { LoginFormComponent } from './components/login-form/login-form.component';
import { NavbarComponent } from './components/navbar/navbar.component';
import { DashboardComponent } from './components/dashboard/dashboard.component';
import { ComponentsComponent } from './components/components/components.component';
import { ComponentsTableComponent } from './components/components-table/components-table.component';
import { UsersComponent } from './components/users/users.component';
import { UsersTableComponent } from './components/users-table/users-table.component';
import { EventsTableComponent } from './components/events-table/events-table.component';
import { EventsComponent } from './components/events/events.component';
import {ButtonComponent} from './components/button/button.component';

@NgModule({
  declarations: [
    AppComponent,
    DashboardComponent,
    LoginComponent,
    NavbarComponent,
    LoginFormComponent,
    ComponentsComponent,
    ComponentsTableComponent,
    UsersComponent,
    UsersTableComponent,
    EventsTableComponent,
    EventsComponent,
    ButtonComponent
  ],
  imports: [
    BrowserModule,
    BrowserAnimationsModule,
    FormsModule,
    ReactiveFormsModule,
    FlexLayoutModule,
    MaterialModule,
    RouterModule.forRoot(AppRoutes),
    StoreModule.forRoot(reducers),
    EffectsModule.forRoot([
      AuthenticationEffects,
      UsersEffects,
      EventsEffects
    ]),
    !environment.production ? StoreDevtoolsModule.instrument({ maxAge: 50 }) : []
  ],
  providers: [
    AuthenticationGuard,
    AuthenticationService,
    TokenService,
    UsersService,
    ComponentsService,
    EventsService
  ],
  bootstrap: [
    AppComponent
  ]
})
export class AppModule { }
