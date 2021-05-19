import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { CreateLobbyComponent } from './modules/pages/create-lobby/create-lobby.component';
import { GameListComponent } from './modules/pages/game-list/game-list.component';
import { GameboardComponent } from './modules/pages/gameboard/gameboard.component';
import { JoinLobbyComponent } from './modules/pages/join-lobby/join-lobby.component';
import { LobbyComponent } from './modules/pages/lobby/lobby.component';
import { TeamChooserComponent } from './modules/pages/team-chooser/team-chooser.component';

const routes: Routes = [
  {
    path: '',
    pathMatch: 'full',
    redirectTo: 'create'
  },
  {
    path: 'create',
    component: CreateLobbyComponent
  },
  {
    path: 'games',
    component: GameListComponent
  },
  {
    path: 'lobby',
    component: LobbyComponent
  },
  {
    path: 'teams',
    component: TeamChooserComponent
  },
  {
    path: 'join/:sessionId/:lobbyName',
    component: JoinLobbyComponent
  },
  {
    path: 'play',
    component: GameboardComponent
  }
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }
