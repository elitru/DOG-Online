import { MessageType } from "../message-type";

export enum MessageTypeDTO {
    UserUpdate = 0,
    StateChanged = 1,
    UserTeamChangedUpdate = 2,
    DealCards = 3
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

            case MessageTypeDTO.DealCards:
                return MessageType.DealCards;

            default:
                throw new Error('Invalid Message Type');
        }
    }
}