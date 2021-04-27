import { UserDTO } from "../../http/dto/user.dto";
import { MessageDTO } from "./message.dto";

export interface UserUpdateMessageDTO extends MessageDTO{
    users: UserDTO[]
}