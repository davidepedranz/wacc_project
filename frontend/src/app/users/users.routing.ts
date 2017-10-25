import { Routes } from '@angular/router';

import { UsersComponent } from './containers/users/users.component';
import { UserAddComponent } from './containers/user-add/user-add.component';
import { NewUserGuard } from './guards/new-user.guard';

export const routes: Routes = [
    {
        path: '',
        pathMatch: 'full',
        component: UsersComponent
    },
    {
        path: 'new',
        pathMatch: 'full',
        component: UserAddComponent,
        canActivate: [NewUserGuard]
    },
    {
        path: '**',
        redirectTo: '/'
    }
];
