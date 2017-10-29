import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { UpdateDockerFormComponent } from './update-docker-form.component';

describe('UpdateDockerFormComponent', () => {
  let component: UpdateDockerFormComponent;
  let fixture: ComponentFixture<UpdateDockerFormComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ UpdateDockerFormComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(UpdateDockerFormComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
