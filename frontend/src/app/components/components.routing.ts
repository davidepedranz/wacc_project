import { Routes } from '@angular/router';

import { ComponentsComponent } from './containers/components.component';

export const routes: Routes = [
    {
        path: '',
        pathMatch: 'full',
        component: ComponentsComponent
    },
    {
        path: '**',
        redirectTo: '/'
    }
];
