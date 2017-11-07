import { Routes } from '@angular/router';

import { ServicesComponent } from './containers/services/services.component';
import { AddServiceComponent } from './containers/add-service/add-service.component';
import { UpdateServiceComponent } from './containers/update-service/update-service.component';

export const routes: Routes = [
    {
        path: '',
        pathMatch: 'full',
        component: ServicesComponent
    },
    {
        path: 'new',
        pathMatch: 'full',
        component: AddServiceComponent
    },
    {
        path: 'update/:id',
        pathMatch: 'full',
        component: UpdateServiceComponent,
    },
    {
        path: '**',
        redirectTo: '/'
    }
];
