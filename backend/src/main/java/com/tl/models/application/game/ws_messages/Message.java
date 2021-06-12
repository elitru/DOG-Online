package com.tl.models.application.game.ws_messages;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.tl.models.application.game.ws_messages.message_type.MessageType;
import com.tl.models.application.game.ws_messages.message_type.MessageTypeDeserializer;
import com.tl.models.application.game.ws_messages.message_type.MessageTypeSerializer;
import lombok.SneakyThrows;
import lombok.ToString;

public abstract class Message {
    @JsonProperty
    @JsonDeserialize(using = MessageTypeDeserializer.class)
    @JsonSerialize(using = MessageTypeSerializer.class)
    protected MessageType type;

    public Message(MessageType type) {
        this.type = type;
    }

    public Message() {

    }

    @SneakyThrows
    public String serialize() {
        return new ObjectMapper().writeValueAsString(this);
    }

    public MessageType getType() {
        return type;
    }
}