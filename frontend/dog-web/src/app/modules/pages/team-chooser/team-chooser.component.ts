import { Component, OnDestroy, OnInit } from '@angular/core';
import { BehaviorSubject, Observable, Subscription } from 'rxjs';
import { Team } from 'src/app/models/http/team';
import { User } from 'src/app/models/http/user';
import { GameService } from 'src/app/provider/game.service';
import { LoaderService } from 'src/app/provider/loader.service';

@Component({
  selector: 'app-team-chooser',
  templateUrl: './team-chooser.component.html',
  styleUrls: ['./team-chooser.component.styl']
})
export class TeamChooserComponent implements OnInit, OnDestroy {
  public teams: Team[];
  public baseTeam: Team;

  private teamsSubscription: Subscription;

  constructor(public gameService: GameService,
              private loaderService: LoaderService) { }

  public ngOnInit(): void {
    this.teamsSubscription = this.gameService.teams$.subscribe(teams => {
      this.teams = teams;

      if(!this.baseTeam && teams.length > 0) {
        this.baseTeam = teams[0];
      }
    });
  }

  public ngOnDestroy(): void {
    this.teamsSubscription.unsubscribe();
  }

  public get selectableTeams(): Team[] {
    return this.teams.filter(t => t.id !== 0);
  }

  public get canStart(): boolean {
    return (this.gameService.users$ as BehaviorSubject<User[]>).getValue().length > 2;
  }

  public async onJoinTeam(team: Team): Promise<void> {
    const currentTeam = this.gameService.getTeamForPlayer(this.gameService.self.id);
    if((currentTeam.id === team.id) || (team.id !== 0 && team.members.length > 1)) return;

    this.loaderService.setLoading(true);

    try {
      await this.gameService.joinTeam(team.id);
    }catch(err) {
      alert('An error occured!');
      console.log(err);
    }

    this.loaderService.setLoading(false);
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