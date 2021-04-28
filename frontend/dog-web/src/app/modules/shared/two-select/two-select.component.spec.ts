import { ComponentFixture, TestBed } from '@angular/core/testing';

import { TwoSelectComponent } from './two-select.component';

describe('TwoSelectComponent', () => {
  let component: TwoSelectComponent;
  let fixture: ComponentFixture<TwoSelectComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ TwoSelectComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(TwoSelectComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
