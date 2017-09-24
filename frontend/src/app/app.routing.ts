import { Routes } from '@angular/router';

import { DashboardComponent } from './components/dashboard/dashboard.component';
import { UsersComponent } from './components/users/users.component';
import { LoginComponent } from './components/login/login.component';
import { ComponentsTableComponent } from './components/components-table/components-table.component';

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
    },
    {
        path: 'components-table',
        component: ComponentsTableComponent,
        pathMatch: 'full',
        canActivate: [AuthenticationGuard]
    }

];

export { routes as AppRoutes };
