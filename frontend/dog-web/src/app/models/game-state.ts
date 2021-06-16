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
    SwapCardWithTeamMate = 5,
    SelectPin = 6,
    SelectDirection = 7,
    SwapPins = 8,
    SelectJokerAction = 9,
    SelectTwoPinsForSwap = 10,
    SelectCardAction = 11,
    StartPin = 12
}