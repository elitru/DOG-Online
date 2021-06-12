import { Injectable } from '@angular/core';
import { BehaviorSubject } from 'rxjs';
import { DealCardsMessage } from '../models/websockets/deal-cards-message';
import { DealCardsMessageDTO } from '../models/websockets/dto/deal-cards-message.dto';
import { MessageTypeDTO } from '../models/websockets/dto/message-type.dto';
import { StateChangedMessageDTO } from '../models/websockets/dto/state-changed-message.dto';
import { SwapCardMessageDTO } from '../models/websockets/dto/swap-card-message.dto';
import { UserTeamChangeMessageDTO } from '../models/websockets/dto/user-team-change-message.dto';
import { UserUpdateMessageDTO } from '../models/websockets/dto/user-update-message.dto';
import { StateChangedMessage } from '../models/websockets/state-changed-message';
import { SwapCardMessage } from '../models/websockets/swap-card-message';
import { UserTeamChangeMessage } from '../models/websockets/user-team-change-message';
import { UserUpdateMessage } from '../models/websockets/user-update-message';

@Injectable({
  providedIn: 'root'
})
export class SocketService {

  private webSocket: WebSocket;

  public userUpdate$: BehaviorSubject<UserUpdateMessage> = new BehaviorSubject<UserUpdateMessage>(null);
  public userTeamChange$: BehaviorSubject<UserTeamChangeMessage> = new BehaviorSubject<UserTeamChangeMessage>(null);
  public stateChange$: BehaviorSubject<StateChangedMessage<any>> = new BehaviorSubject<StateChangedMessage<any>>(null);
  public dealCards$: BehaviorSubject<DealCardsMessage> = new BehaviorSubject<DealCardsMessage>(null);
  public swapCards$: BehaviorSubject<SwapCardMessage> = new BehaviorSubject<SwapCardMessage>(null);
  public userTurn$: BehaviorSubject<boolean> = new BehaviorSubject<boolean>(false);

  constructor() { }

  public connect(url: string) : void {
    this.webSocket = new WebSocket(url);
    this.webSocket.addEventListener('message', (event: MessageEvent<string>) => this.onMessage(event.data));
  }

  private onMessage(data: string): void {
    // parse
    const wsData: any = JSON.parse(data);
    
    switch(wsData.type as MessageTypeDTO) {
      case MessageTypeDTO.UserUpdate:
        {
          const message: UserUpdateMessage = UserUpdateMessage.fromApi(wsData as UserUpdateMessageDTO);
          this.userUpdate$.next(message);
          break;
        }
      case MessageTypeDTO.UserTeamChangedUpdate:
        {
          const message: UserTeamChangeMessage = UserTeamChangeMessage.fromApi(wsData as UserTeamChangeMessageDTO);
          this.userTeamChange$.next(message);
          break;
        }
      case MessageTypeDTO.StateChanged:
        {
          const message: StateChangedMessage<any> = StateChangedMessage.fromApi(wsData as StateChangedMessageDTO<any>);
          this.stateChange$.next(message);
          break;
        }
      case MessageTypeDTO.DealCards:
        {
          const message: DealCardsMessage = DealCardsMessage.fromApi(wsData as DealCardsMessageDTO);
          this.dealCards$.next(message);
          break;
        }
      case MessageTypeDTO.SwapCard:
        {
          const message: SwapCardMessage = SwapCardMessage.fromApi(wsData as SwapCardMessageDTO);
          this.swapCards$.next(message);
          break;
        }

      case MessageTypeDTO.UserTurn:
        {
          this.userTurn$.next(true);
          break;
        }
    }
  }

  public close(): void {
    this.webSocket?.close();
  }
}