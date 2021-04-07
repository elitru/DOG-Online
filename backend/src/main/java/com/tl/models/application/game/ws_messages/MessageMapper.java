package com.tl.models.application.game.ws_messages;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tl.models.application.game.ws_messages.message_type.MessageType;
import com.tl.models.application.game.ws_messages.messages.UserJoinedMessage;
import lombok.SneakyThrows;

import java.util.Arrays;

public class MessageMapper {

    @SneakyThrows
    public static Message fromJsonString(String json) {
        ObjectMapper mapper = new ObjectMapper();int messageTypeId = mapper.readTree(json).get("type").asInt();
        MessageType messageType = Arrays.stream(MessageType.values()).filter(type -> type.getId() == messageTypeId).findFirst().orElseThrow();

        switch(messageType) {
            case UserJoined:
                var parsed = mapper.readValue(json, UserJoinedMessage.class);
                System.out.println(parsed);
                return parsed;

            default:
                return null;
        }
    }
}
