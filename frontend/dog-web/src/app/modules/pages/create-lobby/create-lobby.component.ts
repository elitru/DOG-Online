import { Component, OnInit } from '@angular/core';
import { LoaderService } from 'src/app/provider/loader.service';

@Component({
  selector: 'app-create-lobby',
  templateUrl: './create-lobby.component.html',
  styleUrls: ['./create-lobby.component.styl']
})
export class CreateLobbyComponent implements OnInit {

  constructor(public loaderService: LoaderService) { }

  public ngOnInit(): void {
    
  }

  // Events
  public async onCreateSession(): Promise<void> {
    this.loaderService.setLoading(true);
  }
}