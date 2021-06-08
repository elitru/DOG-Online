export class ApiRoutes {
    private static readonly BasePath = "http://192.168.43.108:8080/api";
    
    public static readonly Session = class {
        public static readonly Base: string = "sessions";
        public static readonly Create: string = `${ApiRoutes.BasePath}/sessions/create`;
        public static readonly Join: string = `${ApiRoutes.BasePath}/sessions/join`;
    }

    public static readonly Game = class {
        public static readonly Next: string = `${ApiRoutes.BasePath}/game/next`;
    }

    public static readonly Team = class {
        public static readonly Join: string = `${ApiRoutes.BasePath}/teams/join`;
    }

    public static readonly Cards = class {
        public static readonly SelectCard: string = `${ApiRoutes.BasePath}/cards/select`;
    }
}