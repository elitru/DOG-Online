import { Injectable } from '@angular/core';
import { InteractionState } from '../models/game-state';
import { Card } from '../models/game/card';
import { GameService } from './game.service';

@Injectable({
  providedIn: 'root'
})
export class CardService {
  private _selectable: boolean = false;
  private _selectedCard: Card;

  constructor(private gameService: GameService) {
    this.gameService.interactionState$.subscribe(state => {
      if(state === InteractionState.SelectCardForMove || state === InteractionState.SwapCardWithTeamMate) {
        this._selectable = true;
        return;
      }

      this._selectable = false;
    });
  }

  public get selectedCard(): Card {
    return this._selectedCard;
  }

  public get isSelectable(): boolean {
    return this._selectable && (this.selectedCard === null || this.selectedCard === undefined);
  }

  public select(card: Card): void {
    this._selectedCard = card;
    
    const currentState = this.gameService.interactionState$.getValue();
    console.log(currentState);
    
    if(currentState === InteractionState.SelectCardForMove) {
      this.gameService.setInteractionState(InteractionState.SelectPlayer);
    }
  }
}
