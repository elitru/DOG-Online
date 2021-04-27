export interface SessionCreateRequest {
    userName: string,
    sessionName: string,
    password: string | null,
    publicSession: boolean,
}