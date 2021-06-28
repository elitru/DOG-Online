import { Component, HostListener, OnInit } from '@angular/core';
import { Game } from 'src/app/models/http/game';
import { GameService } from 'src/app/provider/game.service';

@Component({
  selector: 'app-game-list',
  templateUrl: './game-list.component.html',
  styleUrls: ['./game-list.component.styl']
})
export class GameListComponent implements OnInit {

  public search: boolean = false;
  public searchValue: string = '';
  public games: Game[] = [];

  constructor(private gameSerivce: GameService) {
    (async () => {
      this.games = await this.gameSerivce.getGameList();
    })();
  }

  public get filteredGames(): Game[] {
    return this.games.filter(game => game.name.toLowerCase().includes(this.searchValue.toLowerCase()));
  }

  public ngOnInit(): void {
    
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