import { Component, OnInit } from '@angular/core';
import { InteractionState } from 'src/app/models/game-state';
import { Card } from 'src/app/models/game/card';
import { CardAction, CardType } from 'src/app/models/game/card-type';
import { CardService } from 'src/app/provider/card.service';
import { DialogService } from 'src/app/provider/dialog.service';
import { GameService } from 'src/app/provider/game.service';

@Component({
  selector: 'app-select-action',
  templateUrl: './select-action.component.html',
  styleUrls: ['./select-action.component.styl']
})
export class SelectActionComponent implements OnInit {

  constructor(public cardService: CardService,
              public gameService: GameService) { }

  public ngOnInit(): void {
  
  }

  private get cardType(): CardType {
    if(this.cardService.selectedCard.type !== CardType.Joker) {
      return this.cardService.selectedCard.type;
    }

    return this.cardService.jokerAction;
  }

  public async onSelectAction(action: CardAction): Promise<void> {
    this.cardService.cardAction = action;
    
    if(action === CardAction.Start) {
      try {
        await this.gameService.startPin(this.cardService.selectedCard);
      }catch(err) {
        console.log(err);
      }
      this.gameService.setInteractionState(InteractionState.NoTurn);
      this.cardService.cardAction = null;
      this.cardService.jokerAction = null;
      this.cardService.select(null);
      return;
    }

    this.cardService.cardAction = action;
    this.gameService.setInteractionState(InteractionState.SelectPin);
  }
}
