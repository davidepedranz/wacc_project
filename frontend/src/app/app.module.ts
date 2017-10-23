import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { RouterModule } from '@angular/router';
import { StoreModule } from '@ngrx/store';
import { EffectsModule } from '@ngrx/effects';
import { StoreDevtoolsModule } from '@ngrx/store-devtools';

import { environment } from '../environments/environment';
import { routes } from './app.routing';
import { CoreModule } from './core/core.module';
import { AuthenticationModule } from './authentication/authentication.module';
// import { UsersModule } from './users/users.module';

import { AppComponent } from './core/containers/app/app.component';

import { HttpClientModule, HTTP_INTERCEPTORS } from '@angular/common/http';
import { TokenInterceptor } from './authentication/services/token.interceptor';

@NgModule({
  imports: [
    BrowserModule,
    BrowserAnimationsModule,

    HttpClientModule,

    RouterModule.forRoot(routes),
    StoreModule.forRoot({}),
    EffectsModule.forRoot([]),
    !environment.production ? StoreDevtoolsModule.instrument({ maxAge: 50 }) : [],

    CoreModule,
    AuthenticationModule.forRoot()
  ],
  bootstrap: [
    AppComponent
  ]
})
export class AppModule { }
