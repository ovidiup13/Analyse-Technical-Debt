import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { TdStatsComponent } from './td-stats.component';

describe('TdStatsComponent', () => {
  let component: TdStatsComponent;
  let fixture: ComponentFixture<TdStatsComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ TdStatsComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(TdStatsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
