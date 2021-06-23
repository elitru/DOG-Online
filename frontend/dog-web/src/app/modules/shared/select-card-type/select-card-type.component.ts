import { Component, OnInit } from '@angular/core';
import { InteractionState } from 'src/app/models/game-state';
import { CardType, MoveCards } from 'src/app/models/game/card-type';
import { CardService } from 'src/app/provider/card.service';
import { GameService } from 'src/app/provider/game.service';

@Component({
  selector: 'app-select-card-type',
  templateUrl: './select-card-type.component.html',
  styleUrls: ['./select-card-type.component.styl']
})
export class SelectCardTypeComponent implements OnInit {

  public cards: CardType[] = MoveCards;

  constructor(public gameService: GameService,
              public cardService: CardService) { }

  public ngOnInit(): void {
  
  }

  public onSelectCard(type: CardType): void {
    this.cardService.jokerAction = type;

    if(type === CardType.StartEleven || type === CardType.StartThirteen) {      
      this.gameService.setInteractionState(InteractionState.SelectCardAction);
      return;
    }

    this.gameService.setInteractionState(InteractionState.SelectPin);
  }
}
