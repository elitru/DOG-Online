package com.tl.models.application.game.ws_messages.message_type;

public enum MessageType {
    UserUpdate(0),
    StateChanged(1),
    UserTeamChangedUpdate(2),
    DealCards(3),
    SwapCard(4),
    AskToPlay(5),
    MovePin(6);

    private final int id;

    private MessageType(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }
}
