import { Component, OnInit, Input } from '@angular/core';
import { Http , Headers, RequestOptions} from '@angular/http';



@Component({
  selector: 'app-delete-docker-service',
  templateUrl: './delete-docker-service.component.html',
  styleUrls: ['./delete-docker-service.component.css']
})
export class DeleteDockerServiceComponent implements OnInit {

  constructor(private http:Http) { }

  ngOnInit() {
  }

  @Input() service:string;

//TODO how to import the url of the backend?
  onDeleteService(service : string)  {
    var backendHost = "http://localhost:9000";
    this.http.delete(backendHost + "/services/" + service ).map(res =>
      console.log(res)
    ).subscribe();
    location.reload()
  }

}
