import { BrowserModule } from '@angular/platform-browser';
import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';

// forms
import { FormsModule, ReactiveFormsModule } from '@angular/forms';

// Angular Material
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { MdInputModule, MdButtonModule, MdCardModule } from '@angular/material';

import { AuthenticationGuard } from './authentication.guard';
import { AuthenticationService } from './authentication.service';

import { AppComponent } from './app.component';
import { LoginComponent } from './login/login.component';
import { DashboardComponent } from './dashboard/dashboard.component';

@NgModule({
  declarations: [
    AppComponent,
    LoginComponent,
    DashboardComponent
  ],
  imports: [

    BrowserModule,
    BrowserAnimationsModule,

    FormsModule,
    ReactiveFormsModule,

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
