import { Injectable } from '@angular/core';
import { BehaviorSubject } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class DialogService {

  public title$: BehaviorSubject<string> = new BehaviorSubject<string>('Title');
  public text$: BehaviorSubject<string> = new BehaviorSubject<string>('This is a long text message destined for the user.');
  public visible$: BehaviorSubject<boolean> = new BehaviorSubject<boolean>(false);

  constructor() { }

  public show(title: string, text: string): void {
    this.title$.next(title);
    this.text$.next(text);
    this.visible$.next(true);
  }

  public hide(): void {
    this.visible$.next(false);
  }
}
