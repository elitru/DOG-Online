import { Injectable } from '@angular/core';
import { Card } from '../models/game/card';

@Injectable({
  providedIn: 'root'
})
export class CardService {
  public selectedCard: Card;

  constructor() { }
}
