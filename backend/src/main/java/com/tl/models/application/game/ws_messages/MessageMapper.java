package com.tl.models.application.game.ws_messages;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tl.models.application.game.ws_messages.message_type.MessageType;
import com.tl.models.application.game.ws_messages.messages.StateChangedMessage;
import com.tl.models.application.game.ws_messages.messages.UserUpdateMessage;
import lombok.SneakyThrows;

import java.util.Arrays;

public class MessageMapper {

    @SneakyThrows
    public static Message fromJsonString(String json) {
        ObjectMapper mapper = new ObjectMapper();int messageTypeId = mapper.readTree(json).get("type").asInt();
        MessageType messageType = Arrays.stream(MessageType.values()).filter(type -> type.getId() == messageTypeId).findFirst().orElseThrow();

        switch(messageType) {
            case UserUpdate: {
                var parsed = mapper.readValue(json, UserUpdateMessage.class);
                return parsed;
            }

            case StateChanged: {
                var parsed = mapper.readValue(json, StateChangedMessage.class);
                return parsed;
            }

            default:
                return null;
        }
    }
}

