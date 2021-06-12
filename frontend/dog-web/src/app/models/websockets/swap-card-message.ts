import { Card } from "../game/card";
import { SwapCardMessageDTO } from "./dto/swap-card-message.dto";

export class SwapCardMessage {
    constructor(
        public card: Card
    ) { }

    public static fromApi(dto: SwapCardMessageDTO): SwapCardMessage {
        return new SwapCardMessage(Card.fromApi(dto.card));
    }
}