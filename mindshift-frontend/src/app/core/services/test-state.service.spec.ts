import { TestBed } from '@angular/core/testing';

import { TestStateService } from './test-state.service';

describe('TestStateService', () => {
  let service: TestStateService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(TestStateService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
