import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { IssueStatsComponent } from './issue-stats.component';

describe('IssueStatsComponent', () => {
  let component: IssueStatsComponent;
  let fixture: ComponentFixture<IssueStatsComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ IssueStatsComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(IssueStatsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
