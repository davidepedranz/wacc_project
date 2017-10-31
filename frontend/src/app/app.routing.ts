import { Routes } from '@angular/router';

import { AuthenticationGuard } from './authentication/guards/authentication.guard';
import { DashboardComponent } from './core/containers/dashboard/dashboard.component';

export const routes: Routes = [
    {
        path: '',
        pathMatch: 'full',
        component: DashboardComponent,
        canActivate: [AuthenticationGuard]
    },
    {
        path: 'services',
        loadChildren: './components/components.module#ComponentsModule',
        canActivate: [AuthenticationGuard]
    },
    {
        path: 'users',
        loadChildren: './users/users.module#UsersModule',
        canActivate: [AuthenticationGuard]
    },
    {
        path: 'events',
        loadChildren: './events/events.module#EventsModule',
        canActivate: [AuthenticationGuard]
    },
    {
        path: '**',
        redirectTo: '/'
    }
];
