import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { DialogService } from 'src/app/provider/dialog.service';
import { GameService } from 'src/app/provider/game.service';
import { LoaderService } from 'src/app/provider/loader.service';
import { SocketService } from 'src/app/provider/socket.service';

@Component({
  selector: 'app-join-lobby',
  templateUrl: './join-lobby.component.html',
  styleUrls: ['./join-lobby.component.styl']
})
export class JoinLobbyComponent implements OnInit {
  public sessionId: string = '';
  public lobby: string = '';
  public password: string = '';
  public userName: string = '';

  constructor(private loaderService: LoaderService,
              private router: Router,
              private socketService: SocketService,
              private dialogService: DialogService,
              private gameService: GameService,
              private route: ActivatedRoute) {
    this.loaderService.setLoading(true);

    this.route.params.subscribe(params => {
      this.sessionId = params['sessionId'];
      this.lobby = params['lobbyName'];

      if(!this.lobby || !this.sessionId) {
        this.router.navigateByUrl('/create');
        return;
      }

      this.loaderService.setLoading(true);
    });
  }

  public ngOnInit(): void {
    this.loaderService.setLoading(true);

    this.route.params.subscribe(p => {
      this.sessionId = p['sessionId'];
      this.lobby = p['lobbyName'];
      this.loaderService.setLoading(false);
    });
  }

  // Events
  public async onJoinSession(): Promise<void> {
    this.loaderService.setLoading(true);

    try {
      const { url } = await this.gameService.joinSession(this.userName, this.sessionId, this.password, this.lobby);
      this.socketService.connect(url);
      this.router.navigateByUrl('/lobby');
    }catch(err) {
      console.log(err);
      this.dialogService.show('Fehler', 'Ungültiges Passwort oder doppelter Username...');
    }

    this.loaderService.setLoading(false);
  }
}