import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { CreateLobbyComponent } from './modules/pages/create-lobby/create-lobby.component';

const routes: Routes = [
  {
    path: '',
    pathMatch: 'full',
    redirectTo: 'create'
  },
  {
    path: 'create',
    component: CreateLobbyComponent
  }
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }
