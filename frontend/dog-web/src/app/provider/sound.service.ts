import { Injectable } from '@angular/core';

@Injectable({
  providedIn: 'root'
})
export class SoundService {
  private audio?: HTMLAudioElement;

  constructor() {
    this.init();
  }

  private init(): void {
    this.audio = new Audio();
    this.audio.src = '/assets/bg_music.mp3';
    this.audio.load();
    this.audio.volume = .25;
    this.audio.loop = true;
    this.audio.play();
  }
}