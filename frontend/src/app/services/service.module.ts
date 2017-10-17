import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import {ServiceDetailComponent} from './service/service-detail.component';

@NgModule({
    imports: [
      CommonModule,
      FormsModule,
      ReactiveFormsModule,
     ],
     declarations :[
        ServiceDetailComponent,
    ]
  })
  export class serviceModule {

  }