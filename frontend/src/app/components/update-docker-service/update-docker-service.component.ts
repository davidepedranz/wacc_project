import { Component } from '@angular/core';
import { Router } from '@angular/router';

@Component({
  selector: 'app-update-docker-service',
  templateUrl: './update-docker-service.component.html',
})
export class UpdateDockerServiceComponent {

  constructor(private router: Router) { }

  onUpdateService() {
    this.router.navigate(['/components/update']);
  }
}
