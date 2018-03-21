import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { RepoStatsComponent } from './repo-stats.component';

describe('RepoStatsComponent', () => {
  let component: RepoStatsComponent;
  let fixture: ComponentFixture<RepoStatsComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ RepoStatsComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(RepoStatsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
