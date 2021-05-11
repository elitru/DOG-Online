import { TeamDTO } from "./dto/team.dto";
import { User } from "./user";

export class Team {
    constructor(
        public readonly id: number,
        public members: User[] = []
    ) { }

    public static fromApi({ id, members }: TeamDTO) {
        return new Team(id, members?.map(u => User.fromApi(u)) || []);
    }
}