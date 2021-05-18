import { Component, HostListener, OnInit } from '@angular/core';
import { buildGameBoard } from 'src/app/models/http/field';
import { Game } from 'src/app/models/http/game';

@Component({
  selector: 'app-game-list',
  templateUrl: './game-list.component.html',
  styleUrls: ['./game-list.component.styl']
})
export class GameListComponent implements OnInit {

  public search: boolean = false;
  public searchValue: string = '';
  public games: Game[] = [
    new Game('Lobby 1', 'test1', !true),
    new Game('Lobby 2', 'test1', true)
  ];

  constructor() { }

  public get filteredGames(): Game[] {
    return this.games.filter(game => game.name.toLowerCase().includes(this.searchValue.toLowerCase()));
  }

  public ngOnInit(): void {
    buildGameBoard([]);
  }

  @HostListener('document:keydown', ['$event']) 
  public onKeydownHandler(event: KeyboardEvent) {
    if (event.key === 'Escape' && this.search) {
        this.onToggleSearch();
    }
  }

  public onToggleSearch(): void {
    if(this.search) {
      this.search = false;
      this.searchValue = '';
      return;
    }

    this.search = true;
  }
}