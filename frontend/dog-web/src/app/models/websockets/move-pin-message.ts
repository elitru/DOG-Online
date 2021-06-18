import { MovePinMessageDTO } from "./dto/move-pin-message.dto";

export class MovePinMessage {
    constructor(
        public pinId: string,
        public targetFieldId: number,
        public direction?: 'forward' | 'backward'
    ) {

    }

    public static fromApi(dto: MovePinMessageDTO): MovePinMessage {
        return new MovePinMessage(dto.pinId, dto.targetFieldId, dto.direction);
    }
}