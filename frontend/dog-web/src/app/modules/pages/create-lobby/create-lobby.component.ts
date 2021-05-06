import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { GameService } from 'src/app/provider/game.service';
import { LoaderService } from 'src/app/provider/loader.service';
import { SocketService } from 'src/app/provider/socket.service';

@Component({
  selector: 'app-create-lobby',
  templateUrl: './create-lobby.component.html',
  styleUrls: ['./create-lobby.component.styl']
})
export class CreateLobbyComponent implements OnInit {
  public userName: string = '';
  public lobbyName: string = '';
  public password: string = '';
  public type: string = 'Öffentlich';

  constructor(public loaderService: LoaderService,
              private gameService: GameService,
              private socketService: SocketService,
              private router: Router) { }

  public ngOnInit(): void {
    
  }

  // Events
  public async onCreateSession(): Promise<void> {    
    if(!this.userName || !this.lobbyName) return;
    
    this.loaderService.setLoading(true);
    try {
      const { url } = await this.gameService.createSession(this.userName, this.lobbyName, this.password, this.type === 'Öffentlich');
      this.socketService.connect(url);
      this.router.navigateByUrl('/lobby');
    }catch(err) {
      console.log('err -> ' + err);
      alert('An error occured')!
    }
    this.loaderService.setLoading(false);
  }
}