import { User } from "../http/user";
import { MessageTypeMapper } from "./dto/message-type.dto";
import { UserUpdateMessageDTO } from "./dto/user-update-message.dto";
import { Message } from "./message";
import { MessageType } from "./message-type";

export class UserUpdateMessage extends Message {
    constructor(
        type: MessageType, 
        public users: User[]
    ) {
        super(type);
    }

    public static fromApi({type, users}: UserUpdateMessageDTO): UserUpdateMessage {
        return new UserUpdateMessage(MessageTypeMapper.fromApi(type), users.map(User.fromApi))
    }

}