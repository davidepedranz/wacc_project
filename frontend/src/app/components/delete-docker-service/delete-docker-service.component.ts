import { Component, Input } from '@angular/core';
import { HttpClient } from '@angular/common/http';

@Component({
  selector: 'app-delete-docker-service',
  templateUrl: './delete-docker-service.component.html',
})
export class DeleteDockerServiceComponent {

  constructor(private http: HttpClient) { }

  @Input() service: string;

  onDeleteService(service: string) {
    this.http.delete('/api/v1/services/' + service)
      .map(res => console.log(res))
      .subscribe();
    location.reload();
  }
}
