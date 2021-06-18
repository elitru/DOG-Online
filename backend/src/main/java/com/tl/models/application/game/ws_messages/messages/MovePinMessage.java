package com.tl.models.application.game.ws_messages.messages;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.tl.models.application.game.PinDirection;
import com.tl.models.application.game.PinDirectionSerializer;
import com.tl.models.application.game.ws_messages.Message;
import com.tl.models.application.game.ws_messages.message_type.MessageType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.SneakyThrows;

import java.util.UUID;

@Data
public class MovePinMessage extends Message {

    @JsonProperty
    private UUID pinId;

    @JsonProperty
    private int targetFieldId;

    @JsonProperty
    @JsonSerialize(using = PinDirectionSerializer.class)
    private PinDirection pinDirection;

    @SneakyThrows
    @Override
    public String serialize() {
        return new ObjectMapper().writeValueAsString(this);
    }

    public MovePinMessage(UUID pinId, int targetFieldId, PinDirection pinDirection) {
        super(MessageType.MovePin);
        this.pinId = pinId;
        this.targetFieldId = targetFieldId;
        this.pinDirection = pinDirection;
    }
}
