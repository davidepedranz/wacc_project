import { Routes } from '@angular/router';

import { AuthenticationGuard } from './authentication/guards/authentication.guard';
import { DashboardComponent } from './components/dashboard/dashboard.component';
import { ComponentsComponent } from './components/components/components.component';

const routes: Routes = [
    {
        path: '',
        pathMatch: 'full',
        component: DashboardComponent,
        canActivate: [AuthenticationGuard]
    },
    {
        path: 'components',
        pathMatch: 'full',
        component: ComponentsComponent,
        canActivate: [AuthenticationGuard]
    },
    {
        path: 'users',
        pathMatch: 'full',
        loadChildren: './users/users.module#UsersModule',
        canActivate: [AuthenticationGuard]
    },
    {
        path: 'events',
        pathMatch: 'full',
        loadChildren: './events/events.module#EventsModule',
        canActivate: [AuthenticationGuard]
    },
    {
        path: '**',
        redirectTo: '/'
    }
];

export { routes as AppRoutes };
