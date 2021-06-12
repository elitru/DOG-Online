package com.tl.models.application.game.ws_messages.messages;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tl.models.application.game.ws_messages.Message;
import com.tl.models.application.game.ws_messages.message_type.MessageType;
import com.tl.models.client.responses.CardResponse;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.SneakyThrows;

@Data
@NoArgsConstructor
public class SwapCardMessage extends Message {
    @JsonProperty
    private CardResponse card;

    @SneakyThrows
    @Override
    public String serialize() {
        return new ObjectMapper().writeValueAsString(this);
    }

    public SwapCardMessage(CardResponse card) {
        super(MessageType.SwapCard);
        this.card = card;
    }
}
