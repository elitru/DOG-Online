import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Router } from '@angular/router';
import { CookieService } from 'ngx-cookie-service';
import { BehaviorSubject, Observable } from 'rxjs';
import { GameState } from '../models/game-state';
import { SessionCreateRequest, SessionJoinRequest } from '../models/http/requests';
import { SessionCreateResponse } from '../models/http/responses';
import { User } from '../models/http/user';
import { MessageType } from '../models/websockets/message-type';
import { StateChangedMessage } from '../models/websockets/state-changed-message';
import { ApiRoutes } from '../utils/api-routes';
import { SocketService } from './socket.service';

@Injectable({
  providedIn: 'root'
})
export class GameService {
  private _users$: BehaviorSubject<User[]> = new BehaviorSubject<User[]>([]);
  private _gameState$: BehaviorSubject<GameState> = new BehaviorSubject<GameState>(GameState.Lobby);
  public isOwner: boolean = false;

  constructor(private httpClient: HttpClient,
              private cookieService: CookieService,
              private socketService: SocketService,
              private router: Router) {    
    this.socketService.userUpdate$.subscribe(userUpdateMessage => {      
      if(!userUpdateMessage) return;
      this._users$.next(userUpdateMessage.users);
    });

    this.socketService.stateChange$.subscribe(stateChangedMessage => {
      if(!stateChangedMessage) return;
      
      this._gameState$.next(stateChangedMessage.next);
      
      if(stateChangedMessage.next === GameState.TeamAssignment) {        
        this.router.navigateByUrl('/teams');
      }
    });
  }
  
  public get users$(): Observable<User[]> {
    return this._users$;
  }

  public get gameState$(): Observable<GameState> {
    return this._gameState$;
  }

  public get headers(): HttpHeaders {
    return new HttpHeaders({
      'sessionId': this.cookieService.get('sessionId'),
      'userId': this.cookieService.get('userId')
    });
  }

  private setCookies(sessionId: string, userId: string, sessionName: string): void {
    this.cookieService.deleteAll();
    document.cookie = '';
    this.cookieService.set('sessionId', sessionId);
    this.cookieService.set('userId', userId);
    this.cookieService.set('sessionName', sessionName);
  }

  public async createSession(userName: string, sessionName: string, password: string | null, publicSession: boolean): Promise<SessionCreateResponse> {
    const payload: SessionCreateRequest = {
      userName,
      sessionName,
      password,
      publicSession
    };

    const response = await this.httpClient.post<SessionCreateResponse>(ApiRoutes.Session.Create, payload).toPromise();
    this.setCookies(response.sessionId, response.userId, sessionName);
    this.isOwner = true;

    return response;
  }

  public async joinSession(userName: string, sessionId: string, password: string | null, sessionName: string): Promise<SessionCreateResponse> {
    const payload: SessionJoinRequest = {
      userName,
      sessionId,
      password
    }

    try {
      const response = await this.httpClient.post<SessionCreateResponse>(ApiRoutes.Session.Join, payload).toPromise();
      this.setCookies(response.sessionId, response.userId, sessionName);
      return response;
    }catch(err) {
      throw err;
    }
  }

  public async advanceState(): Promise<void> {
    await this.httpClient.post(ApiRoutes.Game.Next, null, { headers: this.headers }).toPromise();
  }
}
