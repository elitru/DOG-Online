import { Component } from '@angular/core';
import { LoaderService } from './provider/loader.service';
import { SoundService } from './provider/sound.service';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.styl']
})
export class AppComponent{
  public title = 'dog-web';

  constructor(private soundService: SoundService,
              public loaderService: LoaderService) {
    
  }
}