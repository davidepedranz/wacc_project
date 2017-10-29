import { Component, OnInit } from '@angular/core';
import {Http } from '@angular/http';
import { Service,Mode } from './service.model';
import {Router} from '@angular/router'

@Component({
  selector: 'app-docker-service-form',
  templateUrl: './docker-service-form.component.html',
  styleUrls: ['./docker-service-form.component.css']
})
export class DockerServiceFormComponent implements OnInit {

  public service : Service ;

  constructor(private http : Http , private router : Router) {
      this.service = new Service();
  }

  ngOnInit() {
  }



  onServiceSubmit(data) : void {
    this.service.Name= data.name;
    this.service.TaskTemplate.ContainerSpec.Image = data.image
    this.service.Mode.Replicated.Replicas = data.replicas
    console.log(this.service)
    this.http.post("/api/services/create",this.service)
      .subscribe(res => this.router.navigate(["/components"]) )
  }

}
