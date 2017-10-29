import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { DockerServiceFormComponent } from './docker-service-form.component';

describe('DockerServiceFormComponent', () => {
  let component: DockerServiceFormComponent;
  let fixture: ComponentFixture<DockerServiceFormComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ DockerServiceFormComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(DockerServiceFormComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
