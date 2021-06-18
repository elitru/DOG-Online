import { CardDTO } from "../websockets/dto/card.dto";
import { CardType } from "./card-type";

export class Card {
    constructor(
        public readonly id: string,
        public readonly type: CardType,
        public isAvailable: boolean = true
    ) { }

    public static fromApi(dto: CardDTO): Card {
        return new Card(dto.cardId, dto.stringRepresentation as CardType);
    }
}