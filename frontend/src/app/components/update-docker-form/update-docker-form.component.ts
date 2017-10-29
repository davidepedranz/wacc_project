import { Component, OnInit } from '@angular/core';
import {Http } from '@angular/http';
import { Service  } from '../models/service.model';
import {Router} from '@angular/router'


@Component({
  selector: 'app-update-docker-form',
  templateUrl: './update-docker-form.component.html'
})
export class UpdateDockerFormComponent implements OnInit {

  private service : Service ;

  constructor(private http : Http , private router : Router) {
      this.service = new Service();
  }

  ngOnInit() {
  }

  onServiceUpdateSubmit(data) : void {
    this.service.Name= data.name;
    this.service.TaskTemplate.ContainerSpec.Image = data.image
    this.service.Mode.Replicated.Replicas = data.replicas
    console.log(this.service)
    this.http.post("/api/services/"+data.name+"/update",this.service)
      .subscribe(res => this.router.navigate(["/components"]) )
  }

}
