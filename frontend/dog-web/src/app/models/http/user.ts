import { UserDTO } from "./dto/user.dto";

export class User {
    constructor(
        public id: string, public username: string
    ) { }

    public static fromApi({id, username}: UserDTO): User {
        return new User(id, username);
    }
};