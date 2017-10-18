import { Component, OnInit }              from '@angular/core';
import { Validators, FormGroup, FormArray, FormBuilder }           from '@angular/forms';

@Component({
    selector: 'service-form',
    templateUrl: './service-create-form.component.html'
  })
export class ServiceDetailComponent implements OnInit {
  
  

  serviceForm : FormGroup
  
  constructor(private _fb: FormBuilder) {  }
 
  
  
  ngOnInit(): void {
    this.serviceForm = this._fb.group({

      name : ['', [Validators.required, Validators.minLength(4)]],
      taskTemplate : this.initTaskTemplate()

    })
  }
  

  initContainerSpec(){
    return this._fb.group({
      image : ['', Validators.required]
    })
  }


  initTaskTemplate() {
    return this._fb.group({
      containerSpec : this.initContainerSpec()
    })
  }

  initMode(){
    return this._fb.group({
      replicated : this.initMode()
    })
  }
  
  initReplicated(){
    return this._fb.group({
      replicas: [0,Validators.min(1)]
    })
  }

  initEndpointSpec(){
    return this._fb.array([
      this.initPorts
    ])
  }

  initPorts(){
    return this._fb.group({
      protcol : ["",Validators.required],
      PublishedPort : [0,Validators.required],
      TargetPort : [0,Validators.required]
    })
  }

}