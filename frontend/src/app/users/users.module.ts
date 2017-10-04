import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { StoreModule, ActionReducerMap } from '@ngrx/store';
import { EffectsModule } from '@ngrx/effects';

import { FlexLayoutModule } from '@angular/flex-layout';
import { MaterialModule } from '../material.module';

import { routes } from './users.routing';
import { reducers } from './store';

import { UsersService } from './services/users.service';
import { UsersEffects } from './store/users.effects';
import { UsersComponent } from './containers/users/users.component';
import { UsersTableComponent } from './components/users-table/users-table.component';
import { UserEditComponent } from './containers/user-edit/user-edit.component';
import { UserFormComponent } from './components/user-form/user-form.component';
import { UserDeleteDialogComponent } from './components/user-delete-dialog/user-delete-dialog.component';

@NgModule({
  imports: [
    CommonModule,
    RouterModule.forChild(routes),
    StoreModule.forFeature('users', reducers),
    EffectsModule.forFeature([UsersEffects]),
    FlexLayoutModule,
    MaterialModule
  ],
  declarations: [
    UsersComponent,
    UsersTableComponent,
    UserEditComponent,
    UserFormComponent,
    UserDeleteDialogComponent
  ],
  entryComponents: [
    UserDeleteDialogComponent
  ],
  providers: [
    UsersService
  ]
})
export class UsersModule { }
