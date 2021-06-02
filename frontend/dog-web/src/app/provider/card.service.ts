import { Injectable } from '@angular/core';
import { Card } from '../models/game/card';

@Injectable({
  providedIn: 'root'
})
export class CardService {
  private _selectedCard: Card;

  constructor() { }

  public get selectedCard(): Card {
    return this._selectedCard;
  }

  public get isSelectable(): boolean {
    return this.selectedCard === null || this.selectedCard === undefined;
  }

  public select(card: Card): void {
    this._selectedCard = card;
  }
}
