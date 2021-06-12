package com.tl.models.application.game.ws_messages.messages;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tl.models.application.game.ws_messages.Message;
import com.tl.models.application.game.ws_messages.message_type.MessageType;
import com.tl.models.application.user.User;
import lombok.*;

import java.util.List;

public class UserUpdateMessage extends Message {
    @JsonProperty
    private List<User> users;

    public UserUpdateMessage(List<User> users) {
        super(MessageType.UserUpdate);
        this.users = users;
    }

    @Override
    @SneakyThrows
    public String serialize() {
        return new ObjectMapper().writeValueAsString(this);
    }
}