export interface SessionCreateRequest {
    userName: string,
    sessionName: string,
    password: string | null,
    publicSession: boolean,
}

export interface SessionJoinRequest {
    userName: string,
    sessionId: string,
    password: string | null,
}

export interface TeamJoinRequest {
    teamId: number;
}