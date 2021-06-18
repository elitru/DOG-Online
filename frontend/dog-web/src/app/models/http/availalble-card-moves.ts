import { AvailableCardMovesDTO } from "./dto/availalble-card-moves.dto";

export class AvailableCardMoves {
    constructor(
        public cardMoves: Map<string, string[]>
    ) {

    }

    public static fromApi({ cardMoves }: AvailableCardMovesDTO): AvailableCardMoves {
        const moves = new Map<string, string[]>();

        for(const property in cardMoves) {
            moves.set(property, cardMoves[property]);
        }

        return new AvailableCardMoves(moves);
    }
}