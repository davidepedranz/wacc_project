import { Component, OnInit, Input } from '@angular/core';
import { Http , Headers, RequestOptions} from '@angular/http';



@Component({
  selector: 'app-delete-docker-service',
  templateUrl: './delete-docker-service.component.html',
})
export class DeleteDockerServiceComponent implements OnInit {

  constructor(private http:Http) { }

  ngOnInit() {
  }

  @Input() service:string;

  onDeleteService(service : string)  {
    var backendHost = "/api";
    this.http.delete(backendHost + "/services/" + service ).map(res =>
      console.log(res)
    ).subscribe();
    location.reload()
  }

}
