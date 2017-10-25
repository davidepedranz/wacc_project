import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { RouterModule } from '@angular/router';
import { StoreModule, ActionReducerMap } from '@ngrx/store';
import { EffectsModule } from '@ngrx/effects';

import { FlexLayoutModule } from '@angular/flex-layout';
import { MaterialModule } from '../material.module';

import { routes } from './users.routing';
import { reducers } from './store';

import { NewUserGuard } from './guards/new-user.guard';
import { UsersService } from './services/users.service';
import { UsersEffects } from './store/users.effects';
import { UsersComponent } from './containers/users/users.component';
import { UsersTableComponent } from './components/users-table/users-table.component';
import { UserAddComponent } from './containers/user-add/user-add.component';
import { UserFormComponent } from './components/user-form/user-form.component';
import { UserDeleteDialogComponent } from './components/user-delete-dialog/user-delete-dialog.component';

@NgModule({
  imports: [
    CommonModule,
    FormsModule,
    ReactiveFormsModule,
    RouterModule.forChild(routes),
    StoreModule.forFeature('users', reducers),
    EffectsModule.forFeature([UsersEffects]),
    FlexLayoutModule,
    MaterialModule
  ],
  declarations: [
    UsersComponent,
    UsersTableComponent,
    UserAddComponent,
    UserFormComponent,
    UserDeleteDialogComponent
  ],
  entryComponents: [
    UserDeleteDialogComponent
  ],
  providers: [
    NewUserGuard,
    UsersService
  ]
})
export class UsersModule { }
