import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { CookieService } from 'ngx-cookie-service';
import { BehaviorSubject } from 'rxjs';
import { User } from 'src/app/models/http/user';
import { GameService } from 'src/app/provider/game.service';
import { LoaderService } from 'src/app/provider/loader.service';

@Component({
  selector: 'app-lobby',
  templateUrl: './lobby.component.html',
  styleUrls: ['./lobby.component.styl']
})
export class LobbyComponent implements OnInit {

  constructor(public gameService: GameService,
              private loaderService: LoaderService,
              private router: Router,
              private cookieService: CookieService) { }

  public ngOnInit(): void {
  
  }

  public get lobbyName(): string {
    return this.cookieService.get('sessionName');
  }

  public get sessionId(): string {
    return this.cookieService.get('sessionId');
  }

  public get joinLink(): string {
    return `${'http://192.168.43.75:4200'}/join/${this.sessionId}/${this.lobbyName}`;
  }

  public get canStart(): boolean {
    return (this.gameService.users$ as BehaviorSubject<User[]>).getValue().length == 4;
  }

  public copyLink(): void {
    const selBox = document.createElement('textarea');
    selBox.style.position = 'fixed';
    selBox.style.left = '0';
    selBox.style.top = '0';
    selBox.style.opacity = '0';
    selBox.value = this.joinLink;
    document.body.appendChild(selBox);
    selBox.focus();
    selBox.select();
    document.execCommand('copy');
    document.body.removeChild(selBox);
  }

  public async onStartGame(): Promise<void> {
    this.loaderService.setLoading(true);

    try {
      await this.gameService.advanceState();
    }catch(err) {
      console.log(err);
      alert('An error occured');
    }

    this.loaderService.setLoading(false);
  }
}
