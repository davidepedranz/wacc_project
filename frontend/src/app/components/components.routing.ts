import { Routes } from '@angular/router';

import { ComponentsComponent } from './containers/components.component';
import {DockerServiceFormComponent} from './docker-service-form/docker-service-form.component';


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
    ,{
        path: 'new',
        pathMatch: 'full',
        component : DockerServiceFormComponent
    }
];
