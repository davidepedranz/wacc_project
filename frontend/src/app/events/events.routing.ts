import { Routes } from '@angular/router';

import { EventsComponent } from './containers/events.component';

export const routes: Routes = [
    {
        path: '',
        pathMatch: 'full',
        component: EventsComponent
    },
    {
        path: '**',
        redirectTo: '/'
    }
];
