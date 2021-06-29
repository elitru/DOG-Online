import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Router } from '@angular/router';
import { BehaviorSubject, Observable } from 'rxjs';
import { GameState, InteractionState } from '../models/game-state';
import { Card } from '../models/game/card';
import { CardType } from '../models/game/card-type';
import { GameBoardRenderer } from '../models/game/gameboard-renderer';
import { Messages } from '../models/game/message';
import { Pin, PinColor } from '../models/game/pin';
import { PinDTO } from '../models/http/dto/pin.dto';
import { Game } from '../models/http/game';
import { PlayCardRequest, SessionCreateRequest, SessionJoinRequest, TeamJoinRequest } from '../models/http/requests';
import { SessionCreateResponse } from '../models/http/responses';
import { Team } from '../models/http/team';
import { User } from '../models/http/user';
import { UserTurnMessage } from '../models/websockets/available-card-moves';
import { DealCardsMessage } from '../models/websockets/deal-cards-message';
import { MessageType } from '../models/websockets/message-type';
import { MovePinMessage } from '../models/websockets/move-pin-message';
import { StateChangedMessage } from '../models/websockets/state-changed-message';
import { SwapCardMessage } from '../models/websockets/swap-card-message';
import { UserTeamChangeMessage } from '../models/websockets/user-team-change-message';
import { UserUpdateMessage } from '../models/websockets/user-update-message';
import { WinMessage } from '../models/websockets/win-message';
import { ApiRoutes } from '../utils/api-routes';
import { CardService } from './card.service';
import { DialogService } from './dialog.service';
import { LoaderService } from './loader.service';
import { SocketService } from './socket.service';

export class Mocker {
  public static CARDS: Card[] = [new Card('1', CardType.Eight), new Card('2', CardType.Five), new Card('3', CardType.Joker), new Card('4', CardType.Nine), new Card('5', CardType.Joker), new Card('6', CardType.Nine)];
}

type UserId = string;

interface SessionInfo {
  sessionId: string;
  sessionName: string;
  userName: string;
  userId: string;
}

@Injectable({
  providedIn: 'root'
})
export class GameService {
  public sessionInfo: SessionInfo = {
    sessionId: '',
    sessionName: '',
    userId: '',
    userName: ''
  };

  private _users$: BehaviorSubject<User[]> = new BehaviorSubject<User[]>([]);
  private _teams$: BehaviorSubject<Team[]> = new BehaviorSubject<Team[]>([]);
  private _gameState$: BehaviorSubject<GameState> = new BehaviorSubject<GameState>(GameState.Lobby);
  private _cards$: BehaviorSubject<Card[]> = new BehaviorSubject<Card[]>([]);
  private _interactionState$: BehaviorSubject<InteractionState> = new BehaviorSubject<InteractionState>(InteractionState.NoTurn);

  public infoMessage$: BehaviorSubject<string> = new BehaviorSubject<string>(Messages.SELECT_CARD_FOR_SWAP);

  private _pins: Map<UserId, Pin[]> = new Map<UserId, Pin[]>();
  private availableMoves: Map<string, string[]> = new Map<string, string[]>();

  public isOwner: boolean = false;

  constructor(private httpClient: HttpClient,
              private socketService: SocketService,
              private router: Router,
              private dialogService: DialogService,
              private loaderService: LoaderService) {
    this.socketService.userUpdate$.subscribe(msg => this.onUserUpdate(msg));
    this.socketService.stateChange$.subscribe(msg => this.onStateChanged(msg));
    this.socketService.userTeamChange$.subscribe(msg => this.onTeamChanged(msg));
    this.socketService.dealCards$.subscribe(msg => this.onCardsChanged(msg));
    this.socketService.swapCards$.subscribe(msg => this.onSwapCard(msg));
    this.socketService.userTurn$.subscribe(msg => this.onUserTurn(msg));
    this.socketService.win$.subscribe(msg => this.onWin(msg));
    this.socketService.cancel$.subscribe((msg) => {
      if(!msg || this._gameState$.getValue() === GameState.Ended) return;

      this._gameState$.next(GameState.Ended);
      this.router.navigateByUrl('/create');
      this.dialogService.show('Spielabbruch', 'Das Spiel wurde vorzeitig abgebrochen.')
    });

    //this._pins.set('1', [new Pin('2', PinColor.RED, 3)])
  }

  private onWin(message: WinMessage): void {
    if(!message) return;

    const team = this.getTeamById(message.winnerTeamId);

    this.setInteractionState(InteractionState.NoTurn);
    this._gameState$.next(GameState.Ended);

    this.router.navigateByUrl('/');
    this.dialogService.show('Spielende', `Das Team bestehend aus den Spielern ${team.members[0].username} und ${team.members[1].username} hat gewonnen!`);
  }

  private onUserTurn(message: UserTurnMessage): void {
    if(!message) return;
    
    setTimeout(() => {
      console.log('your turn');
    
      this.availableMoves = message.cardMoves;
      
      if(!message.canMakeMove) {
        const cards = this._cards$.getValue().map(c => {
          c.isAvailable = true;
          return c;
        });
    
        this._cards$.next(cards);
        this.setInteractionState(InteractionState.SelectCardForDrop);
        return;
      }

      console.log(message.cardMoves);

      const cards = this._cards$.getValue().map(c => {      
        console.log('Check -> ' + c.id + ' - ' + message.cardMoves.has(c.id));      
        if((message.cardMoves.has(c.id) && message.cardMoves.get(c.id).length > 0) || c.type === CardType.Joker) {
          c.isAvailable = true;
        }else{
          c.isAvailable = false;
        }
        return c;
      });

      console.log('cards loaded');
      console.log(cards);

      this._cards$.next(cards);
      this.setInteractionState(InteractionState.SelectCardForMove);
    }, 200);
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
    this.resetCardsAvailable(true);
    this.setInteractionState(InteractionState.SwapCardWithTeamMate);

    setTimeout(() => {
      this.resetCardsAvailable(true);
      this.setInteractionState(InteractionState.SwapCardWithTeamMate);
      console.log(dealCardsMessage.cards);
      console.log('done');
    }, 100);
  }


  private onSwapCard(swapCardMessage: SwapCardMessage): void {
    if(!swapCardMessage) return;
    
    const cards = this._cards$.getValue();
    cards.push(swapCardMessage.card);
    this._cards$.next(cards);
    this.resetCardsAvailable();

    this.setInteractionState(InteractionState.NoTurn);
  }

  public getAvailableMovesForPin(pinId: string): number[] {
    const result = [];

    this.availableMoves.forEach((fieldIds, pin) => {
      if(pinId === pin && fieldIds.length > 0) {
        result.push(fieldIds);
      }
    });

    return result;
  }

  public setSelectablePins(cardId: string): void {
    this.pins.get(this.self.id).forEach(pin => {
      if(!this.availableMoves.has(cardId)) return;

      const pins = this.availableMoves.get(cardId);

      if(pins.includes(pin.pinId)) {
        pin.selectable = true;
      }else{
        pin.selectable = false;
      }
    });
  }

  public get userColorCode(): string {
    if(this._gameState$.getValue() !== GameState.Ingame) {
      return '#fff';
    }

    const color = this.pins.get(this.self.id)[0].color;

    switch(color) {
      case PinColor.RED:      return '#A10B07';
      case PinColor.BLUE:     return '#005695';
      case PinColor.GREEN:    return '#279500';
      case PinColor.YELLOW:   return '#F2C94C';
    }
  }

  public setInteractionState(next: InteractionState): void {
    this._interactionState$.next(next);

    switch(next) {
      case InteractionState.SwapCardWithTeamMate:
        this.infoMessage$.next(Messages.SELECT_CARD_FOR_SWAP);
        break;
      case InteractionState.NoTurn:
        this.infoMessage$.next(Messages.NOT_YOUR_TURN);
        break;
      case InteractionState.SelectCardForMove:
        this.infoMessage$.next(Messages.SELECT_CARD);
        break;
      case InteractionState.SelectPin:
        this.infoMessage$.next(Messages.SELECT_PIN);
        break;
      case InteractionState.SelectCardForDrop:
        this.infoMessage$.next(Messages.DROP_CARD);
        break;
    }
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
    return this._users$.getValue().find(u => u.id === this.sessionInfo.userId);
  }

  public get pins(): Map<UserId, Pin[]> {
    return this._pins;
  }

  public get movePins$(): Observable<MovePinMessage> {
    return this.socketService.movePin$;
  }

  public get headers(): HttpHeaders {
    return new HttpHeaders({
      'sessionId': this.sessionInfo.sessionId,
      'userId': this.self.id
    });
  }

  public getTeamById(teamId: number): Team {
    return this._teams$.getValue().find(t => t.id === teamId) || null;
  }

  public getTeamForPlayer(userId: string): Team {
    return this._teams$.getValue().find(t => t.members.map(u => u.id).includes(userId)) || null;
  }

  public removeCardFromStack(cardId: string): void {    
    const cards = this._cards$.getValue().filter(c => c.id !== cardId);
    this._cards$.next(cards);
  }

  private initializeTeams(): void {
    const users = this._users$.getValue();
    const teams: Team[] = Array.from(
      Array((users.length % 2 === 0 ? users.length / 2 : users.length) + 1)
    ).map((_, index) => new Team(index, []));

    users.forEach((user) => teams[0].members.push(user));
    this._teams$.next(teams);
  }

  private saveSession(sessionId: string, sessionName: string, userId: string, userName: string): void {
    this.sessionInfo.sessionId = sessionId;
    this.sessionInfo.sessionName = sessionName;
    this.sessionInfo.userId = userId;
    this.sessionInfo.userName = userName;
  }

  public getPinsOnHomeFields(): Pin[] {
    return this.pins.get(this.self.id).filter(pin => pin.fieldId < 0 && pin.fieldId >= -16)
  }

  public getPinsOnBoard(): Pin[] {
    return this.pins.get(this.self.id).filter(p => p.fieldId > 0);
  }

  public get isHomeFieldOccupiedBySelf(): boolean {
    const pins = this.pins.get(this.self.id);

    switch(pins[0].color) {
      case PinColor.RED:
        return pins.some(pin => pin.fieldId === 1);
      case PinColor.BLUE:
        return pins.some(pin => pin.fieldId === 15);
      case PinColor.GREEN:
        return pins.some(pin => pin.fieldId === 29);
      case PinColor.YELLOW:
        return pins.some(pin => pin.fieldId === 43);
    }

    return false;
  }

  public get canStart(): boolean {
    return this.getPinsOnHomeFields().length > 0 && !this.isHomeFieldOccupiedBySelf;
  }

  private resetCardsAvailable(state: boolean = false): void {
    const cards = this._cards$.getValue().map(card => {
      card.isAvailable = state;
      return card;
    });
    this._cards$.next(cards);
  }

  public async createSession(userName: string, sessionName: string, password: string | null, publicSession: boolean): Promise<SessionCreateResponse> {
    const payload: SessionCreateRequest = {
      userName,
      sessionName,
      password,
      publicSession
    };

    const response = await this.httpClient.post<SessionCreateResponse>(ApiRoutes.Session.Create, payload).toPromise();
    this.saveSession(response.sessionId, sessionName, response.userId, userName);
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
      this.saveSession(response.sessionId, sessionName, response.userId, userName);
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
    this.removeCardFromStack(cardId);
    
    await this.httpClient.post(ApiRoutes.Game.SwapCard, {
      cardId, toPlayer
    }, { headers: this.headers }).toPromise();

    this.resetCardsAvailable();
    this.setInteractionState(InteractionState.NoTurn);
  }

  public async makeSimpleMove(cardId: string, pinId: string, fieldId: number, jokerAction?: CardType): Promise<void> {
    this.loaderService.setLoading(true);

    const request: PlayCardRequest<string> = {
      cardId: cardId,
      pinId: pinId,
      payload: ''
    };

    if(jokerAction) {
      request.payload = JSON.stringify({
        cardType: jokerAction,
        cardPayload: JSON.stringify(
          {
            targetField: fieldId
          }
        )
      });
    }else{
      request.payload = JSON.stringify({
        targetField: fieldId
      });
    }

    await this.playCard<string, void>(ApiRoutes.Game.MakeMove, request);
    this.loaderService.setLoading(false);
    
    this.resetCardsAvailable();
  }

  public async swapPins(card: Card, pinId: string, swapPinId: string): Promise<void> {
    const request: PlayCardRequest<string> = {
      cardId: card.id,
      pinId: pinId,
      payload: ''
    };

    if(card.type === CardType.Joker) {
      request.payload = JSON.stringify(
        {
          cardType: CardType.Swap,
          cardPayload: JSON.stringify({
            otherPin: swapPinId
          })
        }
      );
    }else{
      request.payload = JSON.stringify(
        {
          otherPin: swapPinId
        }
      );
    }

    await this.playCard<string, void>(ApiRoutes.Game.MakeMove, request);
    this.resetCardsAvailable();
  }

  public async startPin(card: Card): Promise<void> {
    const request: PlayCardRequest<string> = {
      cardId: card.id,
      pinId: this.getPinsOnHomeFields()[0].pinId,
      payload: JSON.stringify({
        moveToBoard: true
      })
    };

    if(card.type === CardType.Joker) {
      request.payload = JSON.stringify({
        cardPayload: JSON.stringify(
          {
            moveToBoard: true
          }
        ),
        cardType: CardType.StartEleven
      });
    }

    this.removeCardFromStack(card.id);

    await this.playCard<string, void>(ApiRoutes.Game.MakeMove, request);
    this.resetCardsAvailable();
  }

  public async dropCard(card: Card): Promise<void> {
    this.loaderService.setLoading(true);
    await this.httpClient.post<void>(ApiRoutes.Game.DropCard, { cardId: card.id }, { headers: this.headers }).toPromise();
    this.setInteractionState(InteractionState.NoTurn);
    this.loaderService.setLoading(false);
    this.removeCardFromStack(card.id)
    this.resetCardsAvailable();
  }

  public async getMoves(cardId: string, pinId: string, jokerAction?: CardType): Promise<number[]> {
    this.loaderService.setLoading(true);

    const request: any = {
      pinId,
      cardId
    };

    if(jokerAction) {
      request.payload = {
        cardType: jokerAction
      }
    }

    const result = (await this.httpClient.post<{ positions: number[] }>(ApiRoutes.Game.GetMoves, request, { headers: this.headers }).toPromise()).positions;
    this.loaderService.setLoading(false);
    return result;
  }

  private async playCard<T, R>(route: string, request: PlayCardRequest<T>): Promise<R> {
    return this.httpClient.post<R>(route, request, { headers: this.headers }).toPromise();
  }

  public async getGameList(): Promise<Game[]> {
    const games = await this.httpClient.get<Game[]>(ApiRoutes.Session.GameList).toPromise();
    console.log(games);
    return games;
  }
}