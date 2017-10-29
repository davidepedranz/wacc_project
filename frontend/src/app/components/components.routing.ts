import { Routes } from '@angular/router';

import { ComponentsComponent } from './containers/components.component';
import {AddServiceComponent} from './add-service/add-service.component';


export const routes: Routes = [
    {
        path: '',
        pathMatch: 'full',
        component: ComponentsComponent
    },
    {
        path: 'new',
        pathMatch: 'full',
        component : AddServiceComponent
    },
    {
        path: '**',
        redirectTo: '/'
    }

];
