package com.tl.models.application.game.ws_messages.message_type;

public enum MessageType {
    UserJoined(0);

    private final int id;

    private MessageType(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }
}
