import { WinMessageDTO } from "./dto/win-message.dto";

export class WinMessage {
    constructor(
        public winnerTeamId: number
    ) { }
    
    public static fromApi(dto: WinMessageDTO): WinMessage {
        return new WinMessage(dto.winner);
    }
}