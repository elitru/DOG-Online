import { Card } from "../game/card";
import { CardType } from "../game/card-type";
import { DealCardsMessageDTO } from "./dto/deal-cards-message.dto";

export class DealCardsMessage {
    constructor(
        public cards: Card[]
    ) { }

    public static fromApi(dto: DealCardsMessageDTO): DealCardsMessage {
        return new DealCardsMessage(dto.cards.map(card => Card.fromApi(card)));
    }
}

interface CardResponse {
    readonly cardId: string;
    readonly stringRepresentation: string;
}