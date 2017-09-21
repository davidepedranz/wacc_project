import { Routes } from '@angular/router';

import { DashboardComponent } from './components/dashboard/dashboard.component';
import { UsersComponent } from './components/users/users.component';
import { LoginComponent } from './components/login/login.component';

import { AuthenticationGuard } from './guards/authentication/authentication.guard';

const routes: Routes = [
    {
        path: '',
        pathMatch: 'full',
        component: DashboardComponent,
        canActivate: [AuthenticationGuard]
    },
    {
        path: 'users',
        pathMatch: 'full',
        component: UsersComponent,
        canActivate: [AuthenticationGuard]
    },
    {
        path: 'login',
        component: LoginComponent,
        data: {
            hidNavbar: true
        }
    }
];

export { routes as AppRoutes };
