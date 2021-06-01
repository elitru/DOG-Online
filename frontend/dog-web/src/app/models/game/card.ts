import { CardType } from "./card-type";

export class Card {
    constructor(
        public readonly id: string,
        public readonly type: CardType
    ) {

    }
}