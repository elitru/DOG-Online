import { ComponentFixture, TestBed } from '@angular/core/testing';

import { SelectCardTypeComponent } from './select-card-type.component';

describe('SelectCardTypeComponent', () => {
  let component: SelectCardTypeComponent;
  let fixture: ComponentFixture<SelectCardTypeComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ SelectCardTypeComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(SelectCardTypeComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
