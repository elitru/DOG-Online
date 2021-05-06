export class ApiRoutes {
    private static readonly BasePath = "http://80.109.218.245:8080/api";
    
    public static readonly Session = class {
        public static readonly Base: string = "sessions";
        public static readonly Create: string = `${ApiRoutes.BasePath}/sessions/create`;
        public static readonly Join: string = `${ApiRoutes.BasePath}/sessions/join`;
    }

    public static readonly Game = class {
        public static readonly Next: string = `${ApiRoutes.BasePath}/game/next`;
    }
}