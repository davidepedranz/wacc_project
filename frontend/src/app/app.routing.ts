import { Routes } from '@angular/router';

import { AuthenticationGuard } from './guards/authentication.guard';
import { DashboardComponent } from './components/dashboard/dashboard.component';
import { UsersComponent } from './components/users/users.component';
import { ComponentsComponent } from './components/components/components.component';
import { EventsComponent } from './components/events/events.component';
import { LoginComponent } from './components/login/login.component';

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
        path: 'components',
        component: ComponentsComponent,
        pathMatch: 'full',
        canActivate: [AuthenticationGuard]
    },
    {
        path: 'events',
        component: EventsComponent,
        pathMatch: 'full',
        canActivate: [AuthenticationGuard]
    },
    {
        path: 'login',
        component: LoginComponent
    },
    {
        path: '**',
        redirectTo: '/'
    }
];

export { routes as AppRoutes };
