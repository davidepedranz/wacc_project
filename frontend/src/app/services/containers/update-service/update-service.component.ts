import { Component, ChangeDetectionStrategy, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs/Observable';

@Component({
  selector: 'app-update-service',
  templateUrl: './update-service.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class UpdateServiceComponent implements OnInit {

  id$: Observable<string>;

  constructor(private route: ActivatedRoute) { }

  ngOnInit() {
    this.id$ = this.route.params.map(params => params['id']);
  }
}
