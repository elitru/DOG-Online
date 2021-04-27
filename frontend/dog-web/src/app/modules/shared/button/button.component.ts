import { Component, EventEmitter, Input, OnInit, Output } from '@angular/core';

@Component({
  selector: 'app-button',
  templateUrl: './button.component.html',
  styleUrls: ['./button.component.styl']
})
export class ButtonComponent implements OnInit {
  @Input()
  public title: string = 'Title';

  @Input()
  public disabled: boolean = false;

  @Input()
  public width: string = '100%';

  @Output()
  public click = new EventEmitter<void>();

  constructor() { }

  public ngOnInit(): void {
  
  }
}
