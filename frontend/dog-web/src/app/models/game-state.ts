export enum GameState {
    Lobby = 'Lobby',
    TeamAssignment = 'TeamAssignment',
    Ingame = 'Ingame'
}

export enum InteractionState {
    NoTurn = 1,
    SelectCardForMove = 2,
    SelectPlayer = 3,
    SelectMove = 4,
    SwapCardWithTeamMate = 5
}