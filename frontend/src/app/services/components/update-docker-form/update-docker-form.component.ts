import { Component, Input } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Router } from '@angular/router';

import { Service } from '../../models/service.model';

@Component({
  selector: 'app-update-docker-form',
  templateUrl: './update-docker-form.component.html'
})
export class UpdateDockerFormComponent {

  @Input()
  id: string;

  constructor(private http: HttpClient, private router: Router) { }

  onServiceUpdateSubmit(data): void {
    const service = new Service();
    service.Name = this.id;
    service.TaskTemplate.ContainerSpec.Image = data.image;
    service.Mode.Replicated.Replicas = data.replicas;

    this.http.put('/api/v1/services/' + this.id, service)
      .subscribe(res => this.router.navigate(['/services']));
  }
}
