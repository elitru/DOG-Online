import { Component, OnInit } from '@angular/core';
import { BehaviorSubject } from 'rxjs';
import { Card } from 'src/app/models/game/card';
import { CardService } from 'src/app/provider/card.service';
import { GameService } from 'src/app/provider/game.service';

@Component({
  selector: 'app-card-deck',
  templateUrl: './card-deck.component.html',
  styleUrls: ['./card-deck.component.styl']
})
export class CardDeckComponent implements OnInit {

  constructor(public gameService: GameService,
              public cardService: CardService) {
    //this.cardService.selectedCard = (this.gameService.cards$ as BehaviorSubject<Card[]>).getValue()[0];
  }

  public ngOnInit(): void {
  
  }
}
