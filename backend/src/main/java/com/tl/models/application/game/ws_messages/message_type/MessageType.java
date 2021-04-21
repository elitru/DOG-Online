package com.tl.models.application.game.ws_messages.message_type;

public enum MessageType {
    UserUpdate(0),
    StateChanged(1),
    UserTeamChangedUpdate(2);

    private final int id;

    private MessageType(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }
}
