import { Component, OnInit } from '@angular/core';
import { GameService } from 'src/app/provider/game.service';

@Component({
  selector: 'app-info-box',
  templateUrl: './info-box.component.html',
  styleUrls: ['./info-box.component.styl']
})
export class InfoBoxComponent implements OnInit {

  constructor(public gameService: GameService) { }

  public ngOnInit(): void {
  
  }
}
