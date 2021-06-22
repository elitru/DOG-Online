import { Injectable } from '@angular/core';
import { InteractionState } from '../models/game-state';
import { Card } from '../models/game/card';
import { CardAction, CardType } from '../models/game/card-type';
import { GameService, Mocker } from './game.service';

@Injectable({
  providedIn: 'root'
})
export class CardService {
  private _selectable: boolean = false;
  private _selectedCard: Card = null;
  public jokerAction: CardType;
  public cardAction: CardAction;

  constructor(private gameService: GameService) {
    this.gameService.interactionState$.subscribe(state => {
      if(state === InteractionState.SelectCardForMove || state === InteractionState.SwapCardWithTeamMate || state === InteractionState.SelectCardForDrop) {
        this._selectedCard = null;
        this._selectable = true;
        return;
      }

      this._selectable = false;
    });
  }

  public get selectedCard(): Card {
    return this._selectedCard;
  }

  public set selectedCard(card: Card | null) {
    this._selectedCard = card;
  }

  public get isSelectable(): boolean {
    return this._selectable && (this.selectedCard === null || this.selectedCard === undefined);
  }

  public select(card: Card): void {
    this._selectedCard = card;

    if(!card) {
      return;
    }

    const currentState = this.gameService.interactionState$.getValue();
    
    if(currentState === InteractionState.SelectCardForMove) {
      if(card.type === CardType.StartEleven || card.type === CardType.StartThirteen) {
        this.gameService.setInteractionState(InteractionState.SelectCardAction);
      }else if(card.type === CardType.Swap) {
        this.gameService.setInteractionState(InteractionState.SelectTwoPinsForSwap);
      }else if(card.type === CardType.Joker) {
        this.gameService.setInteractionState(InteractionState.SelectJokerAction);
      }else {
        this.gameService.setInteractionState(InteractionState.SelectPlayer);
      }
    }else if(currentState === InteractionState.SelectCardForDrop) {
      this.gameService.dropCard(card);
    }
  }
}
