import { Component, EventEmitter, Input, OnInit, Output } from '@angular/core';
import { Card } from 'src/app/models/game/card';
import { CardType } from 'src/app/models/game/card-type';
import { CardService } from 'src/app/provider/card.service';
import { GameService } from 'src/app/provider/game.service';

@Component({
  selector: 'app-card',
  templateUrl: './card.component.html',
  styleUrls: ['./card.component.styl']
})
export class CardComponent implements OnInit {

  @Input()
  public card: Card;

  constructor(public cardService: CardService,
              public gameService: GameService) { }

  public ngOnInit(): void {
    
  }

  public get imageUrl(): string {
    return `/assets/cards/${this.card.type}.svg`;
  }

  public async onSelect(): Promise<void> {
    if(!this.cardService.isSelectable) return;

    this.cardService.select(this.card);
    return;
    await this.gameService.selectCard(this.card);
  }
}
