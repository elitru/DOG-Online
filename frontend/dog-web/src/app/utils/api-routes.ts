export class ApiRoutes {
    private static readonly BasePath = "http://localhost:8080/api";
    
    public static Session = class {
        public static readonly Create: string = `${ApiRoutes.BasePath}/create`;
    }
}