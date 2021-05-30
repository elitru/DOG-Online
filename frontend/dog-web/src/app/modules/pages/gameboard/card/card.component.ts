import { Component, EventEmitter, Input, OnInit, Output } from '@angular/core';
import { CardType } from 'src/app/models/game/card-type';

@Component({
  selector: 'app-card',
  templateUrl: './card.component.html',
  styleUrls: ['./card.component.styl']
})
export class CardComponent implements OnInit {

  @Input()
  private cardType: CardType;

  constructor() { }

  ngOnInit(): void {
  }

  public get imageUrl(): string {
    return '/assets/cards/1_11.svg'
  }

}
