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

@NgModule({
  declarations: [
    AppComponent,
    CreateLobbyComponent,
    InputComponent,
    ButtonComponent,
    LoaderComponent
  ],
  imports: [
    BrowserModule,
    AppRoutingModule,
    FormsModule,
    HttpClientModule
  ],
  providers: [],
  bootstrap: [AppComponent]
})
export class AppModule { }
