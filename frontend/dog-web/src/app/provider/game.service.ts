import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Router } from '@angular/router';
import { CookieService } from 'ngx-cookie-service';
import { BehaviorSubject, Observable } from 'rxjs';
import { GameState, InteractionState } from '../models/game-state';
import { Card } from '../models/game/card';
import { CardType } from '../models/game/card-type';
import { GameBoardRenderer } from '../models/game/gameboard-renderer';
import { Pin, PinColor } from '../models/game/pin';
import { PinDTO } from '../models/http/dto/pin.dto';
import { SessionCreateRequest, SessionJoinRequest, TeamJoinRequest } from '../models/http/requests';
import { SessionCreateResponse } from '../models/http/responses';
import { Team } from '../models/http/team';
import { User } from '../models/http/user';
import { DealCardsMessage } from '../models/websockets/deal-cards-message';
import { MessageType } from '../models/websockets/message-type';
import { StateChangedMessage } from '../models/websockets/state-changed-message';
import { UserTeamChangeMessage } from '../models/websockets/user-team-change-message';
import { UserUpdateMessage } from '../models/websockets/user-update-message';
import { ApiRoutes } from '../utils/api-routes';
import { CardService } from './card.service';
import { SocketService } from './socket.service';

type UserId = string;

@Injectable({
  providedIn: 'root'
})
export class GameService {
  private _users$: BehaviorSubject<User[]> = new BehaviorSubject<User[]>([]);
  private _teams$: BehaviorSubject<Team[]> = new BehaviorSubject<Team[]>([]);
  private _gameState$: BehaviorSubject<GameState> = new BehaviorSubject<GameState>(GameState.Lobby);
  private _cards$: BehaviorSubject<Card[]> = new BehaviorSubject<Card[]>(/*[new Card('1', CardType.Eight), new Card('2', CardType.Five), new Card('3', CardType.Joker), new Card('4', CardType.Nine), new Card('5', CardType.Joker), new Card('6', CardType.Nine)]*/[]);
  private _interactionState$: BehaviorSubject<InteractionState> = new BehaviorSubject<InteractionState>(InteractionState.NoTurn);

  private _pins: Map<UserId, Pin[]> = new Map<UserId, Pin[]>();

  public isOwner: boolean = false;

  constructor(private httpClient: HttpClient,
              private cookieService: CookieService,
              private socketService: SocketService,
              private router: Router) {
    this.socketService.userUpdate$.subscribe(msg => this.onUserUpdate(msg));
    this.socketService.stateChange$.subscribe(msg => this.onStateChanged(msg));
    this.socketService.userTeamChange$.subscribe(msg => this.onTeamChanged(msg));
    this.socketService.dealCards$.subscribe(msg => this.onCardsChanged(msg));
  }

  private onUserUpdate(userUpdateMessage: UserUpdateMessage): void {
    if(!userUpdateMessage) return;
    this._users$.next(userUpdateMessage.users);
  }

  private onStateChanged(stateChangedMessage: StateChangedMessage<any>): void {
    if(!stateChangedMessage) return;
        
    const { next, data } = stateChangedMessage;

    this._gameState$.next(next);
    
    if(next === GameState.TeamAssignment) {
      this.router.navigateByUrl('/teams');
      this.initializeTeams();
    }else if(next === GameState.Ingame) {      
      this._users$.getValue().forEach(({ id }: User) => {
        const userPins: PinDTO[] = data.ninepins[id];
        const gamePins: Pin[] = userPins.map(p => Pin.fromApi(p));
        this._pins.set(id, gamePins);
      });

      this.router.navigateByUrl('/play');
    }
  }

  private onTeamChanged(teamChangedMessages: UserTeamChangeMessage): void {
    if(!teamChangedMessages) return;
      
    const { userId, newTeam, oldTeam } = teamChangedMessages;

    const currentTeam = this.getTeamById(oldTeam);
    currentTeam.members.splice(currentTeam.members.map(u => u.id).indexOf(userId), 1); 

    const nextTeam = this.getTeamById(newTeam);
    nextTeam.members.push(this._users$.getValue().find(u => u.id === userId));
      this._teams$.next(this._teams$.getValue());
  }

  private onCardsChanged(dealCardsMessage: DealCardsMessage): void {
    if(!dealCardsMessage) return;
    this._cards$.next(dealCardsMessage.cards);
    this.setInteractionState(InteractionState.SwapCardWithTeamMate);
  }

  public setInteractionState(next: InteractionState): void {
    this._interactionState$.next(next);
  }

  public get users$(): Observable<User[]> {
    return this._users$;
  }

  public get teams$(): Observable<Team[]> {
    return this._teams$;
  }

  public get gameState$(): Observable<GameState> {
    return this._gameState$;
  }

  public get cards$(): Observable<Card[]> {
    return this._cards$;
  }

  public get interactionState$(): BehaviorSubject<InteractionState> {
    return this._interactionState$;
  }

  public get self(): User {
    return this._users$.getValue().find(u => u.id === this.cookieService.get('userId'));
  }

  public get pins(): Map<UserId, Pin[]> {
    return this._pins;
  }

  public get headers(): HttpHeaders {
    return new HttpHeaders({
      'sessionId': this.cookieService.get('sessionId'),
      'userId': this.self.id
    });
  }

  public getTeamById(teamId: number): Team {
    return this._teams$.getValue().find(t => t.id === teamId) || null;
  }

  public getTeamForPlayer(userId: string): Team {
    return this._teams$.getValue().find(t => t.members.map(u => u.id).includes(userId)) || null;
  }

  private initializeTeams(): void {
    const users = this._users$.getValue();
    const teams: Team[] = Array.from(
      Array((users.length % 2 === 0 ? users.length / 2 : users.length) + 1)
    ).map((_, index) => new Team(index, []));

    users.forEach((user) => teams[0].members.push(user));
    this._teams$.next(teams);
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

  public async joinTeam(teamId: number): Promise<void> {
    try {
      const request: TeamJoinRequest = {
        teamId
      };

      await this.httpClient.post<void>(ApiRoutes.Team.Join, request, { headers: this.headers }).toPromise();
    }catch(err) {
      throw err;
    }
  }

  public async advanceState(): Promise<void> {
    await this.httpClient.post(ApiRoutes.Game.Next, null, { headers: this.headers }).toPromise();
  }

  public async swapCard(cardId: string, toPlayer: string): Promise<void> {
    await this.httpClient.post(ApiRoutes.Game.SwapCard, {
      cardId, toPlayer
    }, { headers: this.headers }).toPromise();

    const cards = this._cards$.getValue();
    this._cards$.next(cards.filter(c => c.id !== cardId));

    this.setInteractionState(InteractionState.NoTurn);
  }
}
