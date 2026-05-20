import { ComponentFixture, TestBed } from '@angular/core/testing';

import { BuscadorExpertosComponent } from './buscador-expertos.component';

describe('BuscadorExpertosComponent', () => {
  let component: BuscadorExpertosComponent;
  let fixture: ComponentFixture<BuscadorExpertosComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [BuscadorExpertosComponent]
    })
    .compileComponents();
    
    fixture = TestBed.createComponent(BuscadorExpertosComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
