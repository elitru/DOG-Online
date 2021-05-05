import { GameDTO } from "./dto/game.dto";

export class Game {
    constructor(
        public name: string,
        public sessionId: string,
        public hasPassword: boolean
    ) { }

    public static fromApi({ name, sessionId, hasPassword }: GameDTO): Game {
        return new Game(name, sessionId, hasPassword);
    }
}