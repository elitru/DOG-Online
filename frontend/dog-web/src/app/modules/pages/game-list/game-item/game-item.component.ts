import { Component, Input, OnInit } from '@angular/core';
import { Game } from 'src/app/models/http/game';

@Component({
  selector: 'app-game-item',
  templateUrl: './game-item.component.html',
  styleUrls: ['./game-item.component.styl']
})
export class GameItemComponent implements OnInit {

  @Input()
  public game: Game;

  constructor() { }

  public ngOnInit(): void {
    
  }
}