import { MessageTypeMapper } from "./dto/message-type.dto";
import { StateChangedMessageDTO } from "./dto/state-changed-message.dto";
import { Message } from "./message";
import { MessageType } from "./message-type";

export class StateChangedMessage<TData> extends Message {
    constructor(
        type: MessageType,
        public data: TData
    ) {
        super(type);
    }

    public static fromApi<TResult>({type, data}: StateChangedMessageDTO<TResult>): StateChangedMessage<TResult> {
        return new StateChangedMessage(MessageTypeMapper.fromApi(type), data);
    }
}