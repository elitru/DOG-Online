import { Component, OnInit } from '@angular/core';
import { BehaviorSubject } from 'rxjs';
import { Team } from 'src/app/models/http/team';
import { User } from 'src/app/models/http/user';
import { GameService } from 'src/app/provider/game.service';

@Component({
  selector: 'app-team-chooser',
  templateUrl: './team-chooser.component.html',
  styleUrls: ['./team-chooser.component.styl']
})
export class TeamChooserComponent implements OnInit {
  public teams: Team[] = [];
  public baseTeam: Team = this.teams[0];

  constructor(private gameService: GameService) { }

  public ngOnInit(): void {
  
  }

  public get selectableTeams(): Team[] {
    return this.teams.filter(t => t.id !== 0);
  }

  public get canStart(): boolean {
    return (this.gameService.users$ as BehaviorSubject<User[]>).getValue().length > 2;
  }

  public getTeamForPlayer(userId: string): Team | null {
    return this.teams.find(t => t.members.map(u => u.id).includes(userId)) || null;
  }

  public onJoinTeam(team: Team): void {
    const currentTeam = this.getTeamForPlayer('1');
    if(!currentTeam || currentTeam.id === team.id) return;

    const user = currentTeam.members.find(u => u.id);
    team.members.push(user);
    currentTeam.members.splice(currentTeam.members.map(u => u.id).indexOf(user.id), 1);
  }
}
