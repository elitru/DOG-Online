import { Component, EventEmitter, Input, OnInit, Output } from '@angular/core';

@Component({
  selector: 'app-input',
  templateUrl: './input.component.html',
  styleUrls: ['./input.component.styl']
})
export class InputComponent implements OnInit {

  @Input()
  public title: string = 'Title';

  @Input()
  public placeholder: string = 'Placeholder';

  @Input()
  public type: 'text' | 'password' = 'text';

  @Input()
  public width: string = '400px';

  @Input()
  public value:string = '';

  @Input()
  public disabled: boolean = false;

  @Output()
  public valueChange = new EventEmitter<string>();

  constructor() { }

  public ngOnInit(): void {
  
  }

  public onChange(): void {
    this.valueChange.emit(this.value);
  }
}
