import { Component } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Router } from '@angular/router';

import { Service } from '../../models/service.model';

@Component({
  selector: 'app-docker-service-form',
  templateUrl: './docker-service-form.component.html',
})
export class DockerServiceFormComponent {

  constructor(private http: HttpClient, private router: Router) { }

  onServiceSubmit(data): void {
    const service = new Service();
    service.Name = data.name;
    service.TaskTemplate.ContainerSpec.Image = data.image;
    service.Mode.Replicated.Replicas = data.replicas;
    console.log(service);
    this.http.post('/api/v1/services', service, { responseType: 'text' })
      .subscribe(res => this.router.navigate(['/services']));
  }
}
