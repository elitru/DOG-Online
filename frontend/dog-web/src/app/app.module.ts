import { NgModule } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { BrowserModule } from '@angular/platform-browser';
import { HttpClientModule } from '@angular/common/http';
import { AppRoutingModule } from './app-routing.module';
import { AppComponent } from './app.component';
import { CreateLobbyComponent } from './modules/pages/create-lobby/create-lobby.component';
import { InputComponent } from './modules/shared/input/input.component';
import { ButtonComponent } from './modules/shared/button/button.component';
import { LoaderComponent } from './modules/shared/loader/loader.component';
import { TwoSelectComponent } from './modules/shared/two-select/two-select.component';
import { JoinLobbyComponent } from './modules/pages/join-lobby/join-lobby.component';
import { GameListComponent } from './modules/pages/game-list/game-list.component';
import { GameItemComponent } from './modules/pages/game-list/game-item/game-item.component';
import { LobbyComponent } from './modules/pages/lobby/lobby.component';
import { CookieService } from 'ngx-cookie-service';
import { TeamChooserComponent } from './modules/pages/team-chooser/team-chooser.component';
import { GameboardComponent } from './modules/pages/gameboard/gameboard.component';
import { CardComponent } from './modules/pages/gameboard/card/card.component';
import { CardDeckComponent } from './modules/pages/gameboard/card-deck/card-deck.component';
import { SelectCardTypeComponent } from './modules/shared/select-card-type/select-card-type.component';
import { InfoBoxComponent } from './modules/shared/info-box/info-box.component';
import { DialogComponent } from './modules/shared/dialog/dialog.component';
import { SelectActionComponent } from './modules/shared/select-action/select-action.component';

@NgModule({
  declarations: [
    AppComponent,
    CreateLobbyComponent,
    InputComponent,
    ButtonComponent,
    LoaderComponent,
    TwoSelectComponent,
    JoinLobbyComponent,
    GameListComponent,
    GameItemComponent,
    LobbyComponent,
    TeamChooserComponent,
    GameboardComponent,
    CardComponent,
    CardDeckComponent,
    SelectCardTypeComponent,
    InfoBoxComponent,
    DialogComponent,
    SelectActionComponent
  ],
  imports: [
    BrowserModule,
    AppRoutingModule,
    FormsModule,
    HttpClientModule
  ],
  providers: [ CookieService ],
  bootstrap: [AppComponent]
})
export class AppModule { }