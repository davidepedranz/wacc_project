import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { UpdateDockerServiceComponent } from './update-docker-service.component';

describe('UpdateDockerServiceComponent', () => {
  let component: UpdateDockerServiceComponent;
  let fixture: ComponentFixture<UpdateDockerServiceComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ UpdateDockerServiceComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(UpdateDockerServiceComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
