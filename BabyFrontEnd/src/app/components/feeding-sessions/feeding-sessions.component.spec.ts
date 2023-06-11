import { ComponentFixture, TestBed } from '@angular/core/testing';

import { FeedingSessionsComponent } from './feeding-sessions.component';

describe('FeedingSessionsComponent', () => {
  let component: FeedingSessionsComponent;
  let fixture: ComponentFixture<FeedingSessionsComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [FeedingSessionsComponent]
    });
    fixture = TestBed.createComponent(FeedingSessionsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
