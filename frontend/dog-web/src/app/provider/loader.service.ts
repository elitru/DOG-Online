import { Injectable } from '@angular/core';
import { BehaviorSubject, Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class LoaderService {
  private _isLoading$: BehaviorSubject<boolean> = new BehaviorSubject<boolean>(false);

  constructor() { }

  public get isLoading$(): Observable<boolean> {
    return this._isLoading$;
  }

  public setLoading(loading: boolean): void {
    this._isLoading$.next(loading);
  }
}