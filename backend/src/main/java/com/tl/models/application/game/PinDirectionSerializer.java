package com.tl.models.application.game;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import com.tl.models.application.game.ws_messages.message_type.MessageType;

import java.io.IOException;

public class PinDirectionSerializer extends StdSerializer<PinDirection> {
    public PinDirectionSerializer() {
        super(PinDirection.class);
    }

    @Override
    public void serialize(PinDirection dir, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
        jsonGenerator.writeString(dir.getDirectionStr());
    }
}
