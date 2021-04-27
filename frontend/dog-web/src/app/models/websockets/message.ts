import { MessageTypeDTO, MessageTypeMapper } from "./dto/message-type.dto";
import { MessageType } from "./message-type";

export class Message {
    constructor(public type: MessageType) {}
}