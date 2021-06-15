export class ApiRoutes {
    private static readonly BasePath = "http://80.109.218.245:8080/api";
    
    public static readonly Session = class {
        public static readonly Base: string = "sessions";
        public static readonly Create: string = `${ApiRoutes.BasePath}/sessions/create`;
        public static readonly Join: string = `${ApiRoutes.BasePath}/sessions/join`;
    }

    public static readonly Game = class {
        public static readonly Next: string = `${ApiRoutes.BasePath}/game/next`;
        public static readonly SwapCard: string = `${ApiRoutes.BasePath}/game/swap`;
        public static readonly GetMoves: string = `${ApiRoutes.BasePath}/game/available-moves`;
        public static readonly MakeMove: string = `${ApiRoutes.BasePath}/game/move`;
    }

    public static readonly Team = class {
        public static readonly Join: string = `${ApiRoutes.BasePath}/teams/join`;
    }
}