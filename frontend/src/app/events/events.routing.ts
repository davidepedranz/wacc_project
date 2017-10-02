import { Routes } from '@angular/router';

// import { UsersComponent } from './containers/users/users.component';
// import { UserEditComponent } from './containers/user-edit/user-edit.component';

export const routes: Routes = [
    // {
    //     path: '',
    //     pathMatch: 'full',
    //     component: UsersComponent
    // },
    // {
    //     path: ':username/edit',
    //     pathMatch: 'full',
    //     component: UserEditComponent
    // },
    {
        path: '**',
        redirectTo: '/'
    }
];
