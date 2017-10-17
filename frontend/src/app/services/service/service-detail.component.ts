import { Component }              from '@angular/core';
import {FormBuilder, FormGroup }            from '@angular/forms';

@Component({
    selector: 'service-detail',
    templateUrl: './service-detail.component.html'
  })
export class ServiceDetailComponent {
  serviceForm : FormGroup
  
  constructor(private fb: FormBuilder) { // <--- inject FormBuilder
    this.createForm();
  }
  
  
  createForm() {
    this.serviceForm = this.fb.group({
      name: '', // <--- the FormControl called "name"
    });
  }
  
}