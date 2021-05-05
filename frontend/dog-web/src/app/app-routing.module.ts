import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { CreateLobbyComponent } from './modules/pages/create-lobby/create-lobby.component';
import { GameListComponent } from './modules/pages/game-list/game-list.component';
import { JoinLobbyComponent } from './modules/pages/join-lobby/join-lobby.component';

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
    path: 'join/:sessionId/:lobbyName',
    component: JoinLobbyComponent
  }
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }
