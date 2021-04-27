import { MessageType } from "../message-type";

export enum MessageTypeDTO {
    UserUpdate = 0,
    StateChanged = 1,
    UserTeamChangedUpdate = 2
};

export class MessageTypeMapper {
    public static fromApi(dto: MessageTypeDTO): MessageType {
        switch(dto) {
            case MessageTypeDTO.StateChanged:
                return MessageType.StateChanged;

            case MessageTypeDTO.UserTeamChangedUpdate:
                return MessageType.UserTeamChangedUpdate;

            case MessageTypeDTO.UserUpdate:
                return MessageType.UserUpdate;

            default:
                throw new Error('Invalid Message Type');
        }
    }
}