import { GameState } from "../game-state";
import { MessageTypeMapper } from "./dto/message-type.dto";
import { StateChangedMessageDTO } from "./dto/state-changed-message.dto";
import { Message } from "./message";
import { MessageType } from "./message-type";

export class StateChangedMessage<TData> extends Message {
    constructor(
        type: MessageType,
        public data: TData,
        public next: GameState
    ) {
        super(type);
    }

    public static fromApi<TResult>({type, data, next}: StateChangedMessageDTO<TResult>): StateChangedMessage<TResult> {
        return new StateChangedMessage(MessageTypeMapper.fromApi(type), data, next as GameState);
    }
}