import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { DeleteDockerServiceComponent } from './delete-docker-service.component';

describe('DeleteDockerServiceComponent', () => {
  let component: DeleteDockerServiceComponent;
  let fixture: ComponentFixture<DeleteDockerServiceComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ DeleteDockerServiceComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(DeleteDockerServiceComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
