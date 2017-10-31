import { Component } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Router } from '@angular/router';

import { Service } from '../models/service.model';

@Component({
  selector: 'app-update-docker-form',
  templateUrl: './update-docker-form.component.html'
})
export class UpdateDockerFormComponent {

  constructor(private http: HttpClient, private router: Router) { }

  onServiceUpdateSubmit(data): void {
    const service = new Service();
    service.Name = data.name;
    service.TaskTemplate.ContainerSpec.Image = data.image;
    service.Mode.Replicated.Replicas = data.replicas;
    console.log(service);
    this.http.put('/api/v1/services/' + data.name, service)
      .subscribe(res => this.router.navigate(['/components']));
  }
}
