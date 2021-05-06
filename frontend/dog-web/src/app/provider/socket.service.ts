import { Injectable } from '@angular/core';
import { BehaviorSubject } from 'rxjs';
import { MessageTypeDTO } from '../models/websockets/dto/message-type.dto';
import { StateChangedMessageDTO } from '../models/websockets/dto/state-changed-message.dto';
import { UserTeamChangeMessageDTO } from '../models/websockets/dto/user-team-change-message.dto';
import { UserUpdateMessageDTO } from '../models/websockets/dto/user-update-message.dto';
import { StateChangedMessage } from '../models/websockets/state-changed-message';
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
    }
  }

  public close(): void {
    this.webSocket?.close();
  }
}
