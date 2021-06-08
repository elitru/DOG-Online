package com.tl.models.application.game.ws_messages.message_type;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

import java.io.IOException;

public class MessageTypeSerializer extends StdSerializer<MessageType> {
    public MessageTypeSerializer() {
        super(MessageType.class);
    }

    @Override
    public void serialize(MessageType messageType, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
        jsonGenerator.writeNumber(messageType.getId());
    }
}
