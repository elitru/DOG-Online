import { Component, OnInit } from '@angular/core';
import { InteractionState } from 'src/app/models/game-state';
import { CardAction } from 'src/app/models/game/card-type';
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

  public async onSelectAction(action: CardAction): Promise<void> {
    this.cardService.cardAction = action;
    
    if(action === CardAction.Start) {
      await this.gameService.startPin(this.cardService.selectedCard.id);
      this.gameService.setInteractionState(InteractionState.NoTurn);
      return;
    }

    this.cardService.cardAction = action;
    this.gameService.setInteractionState(InteractionState.SelectPin);
  }
}
