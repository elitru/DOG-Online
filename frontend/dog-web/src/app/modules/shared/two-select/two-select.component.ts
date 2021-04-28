import { Component, EventEmitter, Input, OnInit, Output } from '@angular/core';

@Component({
  selector: 'app-two-select',
  templateUrl: './two-select.component.html',
  styleUrls: ['./two-select.component.styl']
})
export class TwoSelectComponent implements OnInit {

  @Input()
  public values: string[] = ['Test 1', 'Test 2'];

  @Input()
  public value: string = this.values[0];

  @Input()
  public width: string = '400px';

  @Output()
  public valueChange = new EventEmitter<string>();

  constructor() { }

  public ngOnInit(): void {
  
  }

  public onSelect(value: string): void {
    if(value === this.value) return;

    this.value = value;
    this.valueChange.emit(value);
  }
}