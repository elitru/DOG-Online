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

  constructor() { }

  public ngOnInit(): void {
  
  }
}
