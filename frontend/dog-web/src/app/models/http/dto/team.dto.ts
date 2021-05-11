import { UserDTO } from "./user.dto";

export interface TeamDTO {
    readonly id: number;
    members: UserDTO[];
}