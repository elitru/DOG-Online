package com.tl.models.application.game.ws_messages.messages;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tl.models.application.game.ws_messages.Message;
import com.tl.models.application.game.ws_messages.message_type.MessageType;
import lombok.*;

import java.util.UUID;

@Data
@ToString
public class UserJoinedMessage extends Message {
    private UUID userId;
    private String userName;

    public UserJoinedMessage(UUID userId, String userName) {
        super(MessageType.UserJoined);
        this.userId = userId;
        this.userName = userName;
    }

    public UserJoinedMessage() {
        super(MessageType.UserJoined);
    }

    @SneakyThrows
    public String serialize() {
        return new ObjectMapper().writeValueAsString(this);
    }
}
