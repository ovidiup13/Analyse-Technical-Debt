import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { RepositoryDashboardComponent } from './repository-dashboard.component';

describe('RepositoryDashboardComponent', () => {
  let component: RepositoryDashboardComponent;
  let fixture: ComponentFixture<RepositoryDashboardComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ RepositoryDashboardComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(RepositoryDashboardComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
