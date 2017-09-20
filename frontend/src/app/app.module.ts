import { BrowserModule } from '@angular/platform-browser';
import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';

// application status
import { StoreModule } from '@ngrx/store';
import { EffectsModule } from '@ngrx/effects';
import { StoreDevtoolsModule } from '@ngrx/store-devtools';
import { reducers } from './store/reducers';
import { AuthenticationEffects } from './store/authentication/authentication.effects';

// forms
import { FormsModule, ReactiveFormsModule } from '@angular/forms';

// Angular Material
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { MdInputModule, MdButtonModule, MdCardModule } from '@angular/material';

import { AuthenticationGuard } from './guards/authentication/authentication.guard';
import { AuthenticationService } from './services/authentication/authentication.service';

import { AppComponent } from './components/app/app.component';
import { DashboardComponent } from './components/dashboard/dashboard.component';
import { LoginComponent } from './components/login/login.component';

@NgModule({
  declarations: [
    AppComponent,
    DashboardComponent,
    LoginComponent
  ],
  imports: [

    BrowserModule,
    BrowserAnimationsModule,

    FormsModule,
    ReactiveFormsModule,

    // TODO: extract router to separate file
    RouterModule.forRoot([
      {
        path: '',
        component: DashboardComponent,
        canActivate: [AuthenticationGuard]
      },
      {
        path: 'login',
        component: LoginComponent
      }
    ], {
        enableTracing: true
      }),

    // application status, using ngrx
    StoreModule.forRoot(reducers),
    EffectsModule.forRoot([AuthenticationEffects]),
    StoreDevtoolsModule.instrument({
      maxAge: 25
    }),

    // UI elements
    MdInputModule,
    MdButtonModule,
    MdCardModule
  ],
  providers: [
    AuthenticationGuard,
    AuthenticationService
  ],
  bootstrap: [AppComponent]
})
export class AppModule { }
