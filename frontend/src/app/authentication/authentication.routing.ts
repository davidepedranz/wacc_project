import { Routes } from '@angular/router';

import { LoginComponent } from './containers/login.component';

export const routes: Routes = [
    {
        path: 'login',
        pathMatch: 'full',
        component: LoginComponent
    }
];
