import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { LoaderService } from 'src/app/provider/loader.service';

@Component({
  selector: 'app-join-lobby',
  templateUrl: './join-lobby.component.html',
  styleUrls: ['./join-lobby.component.styl']
})
export class JoinLobbyComponent implements OnInit {
  public sessionId: string = '';
  public lobby: string = 'Lobby';
  public password: string = '';

  constructor(private loaderService: LoaderService,
              private route: ActivatedRoute) { }

  public ngOnInit(): void {
    this.loaderService.setLoading(true);

    this.route.params.subscribe(p => {
      this.sessionId = p['sessionId'];
      this.lobby = p['lobbyName'];
      this.loaderService.setLoading(false);
    });
  }

  // Events
  public async onCreateSession(): Promise<void> {
    this.loaderService.setLoading(true);
  }
}