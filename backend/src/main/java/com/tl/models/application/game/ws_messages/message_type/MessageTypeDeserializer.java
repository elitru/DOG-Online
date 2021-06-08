package com.tl.models.application.game.ws_messages.message_type;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;

import java.io.IOException;
import java.util.Arrays;

public class MessageTypeDeserializer extends StdDeserializer<MessageType> {

    protected MessageTypeDeserializer() {
        super(Integer.class);
    }

    @Override
    public MessageType deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException, JsonProcessingException {
        int number = jsonParser.getIntValue();
        return Arrays.stream(MessageType.values()).filter(type -> type.getId() == number).findFirst().orElseThrow();
    }
}
