import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { RouterModule } from '@angular/router';
import { StoreModule, ActionReducerMap } from '@ngrx/store';
import { EffectsModule } from '@ngrx/effects';

import { FlexLayoutModule } from '@angular/flex-layout';
import { MaterialModule } from '../material.module';

import { routes } from './authentication.routing';
import { reducers } from './store';

import { AuthenticationEffects } from './store/authentication.effects';
import { AuthenticationGuard } from './guards/authentication.guard';
import { AuthenticationService } from './services/authentication.service';
import { TokenService } from './services/token.service';
import { LoginComponent } from './containers/login.component';
import { LoginFormComponent } from './components/login-form.component';

@NgModule({
  imports: [
    CommonModule,
    FormsModule,
    ReactiveFormsModule,
    RouterModule.forChild(routes),
    StoreModule.forFeature('authentication', reducers),
    EffectsModule.forFeature([AuthenticationEffects]),
    FlexLayoutModule,
    MaterialModule
  ],
  declarations: [
    LoginComponent,
    LoginFormComponent,
  ],
  providers: [
    AuthenticationGuard,
    AuthenticationService,
    TokenService
  ]
})
export class AuthenticationModule { }
