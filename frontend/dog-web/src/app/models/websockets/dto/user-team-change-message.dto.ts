import { MessageDTO } from "./message.dto";

export interface UserTeamChangeMessageDTO extends MessageDTO {
    userId: string,
    newTeam: number,
    oldTeam: number
}