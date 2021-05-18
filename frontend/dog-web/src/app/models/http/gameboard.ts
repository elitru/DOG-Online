import { StartField } from "./field";
import { User } from "./user";

export class GameBoard {
    public startFields: Map<User, StartField>;

    constructor() {
        this.startFields = new Map<User, StartField>();
    }
}