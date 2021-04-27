import { MessageTypeMapper } from "./dto/message-type.dto";
import { UserTeamChangeMessageDTO } from "./dto/user-team-change-message.dto";
import { Message } from "./message";
import { MessageType } from "./message-type";

export class UserTeamChangeMessage extends Message {
    constructor(
        type: MessageType,
        public userId: string,
        public newTeam: number,
        public oldTeam: number
    ) {
        super(type);
    }

    public static fromApi({type, userId, newTeam, oldTeam}: UserTeamChangeMessageDTO) : UserTeamChangeMessage {
        return new UserTeamChangeMessage(MessageTypeMapper.fromApi(type), userId, newTeam, oldTeam);
    }
}