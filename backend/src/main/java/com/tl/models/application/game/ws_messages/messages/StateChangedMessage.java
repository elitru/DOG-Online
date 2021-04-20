package com.tl.models.application.game.ws_messages.messages;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tl.models.application.game.states.GameStateIdentifier;
import com.tl.models.application.game.ws_messages.Message;
import com.tl.models.application.game.ws_messages.message_type.MessageType;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.SneakyThrows;

public class StateChangedMessage<T> extends Message {
    @JsonProperty
    private GameStateIdentifier next;
    @JsonProperty
    private T data;

    public StateChangedMessage(GameStateIdentifier next, T data) {
        super(MessageType.StateChanged);
        this.next = next;
        this.data = data;
    }

    @SneakyThrows
    @Override
    public String serialize() {
        return new ObjectMapper().writeValueAsString(this);
    }
}
