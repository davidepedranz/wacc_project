import { Component, OnInit } from '@angular/core';
import {Router} from '@angular/router'

@Component({
  selector: 'app-update-docker-service',
  templateUrl: './update-docker-service.component.html',
})
export class UpdateDockerServiceComponent implements OnInit {

  constructor(private router : Router) { }

  ngOnInit() {
  }

  onUpdateService(){
    this.router.navigate(["/components/update"])
  }

}
