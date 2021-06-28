import { GameDTO } from "./dto/game.dto";

export interface Game {
    name: string;
    sessionId: string;
    hasPassword: boolean;
}