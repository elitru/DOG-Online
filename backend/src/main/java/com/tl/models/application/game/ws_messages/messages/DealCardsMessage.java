package com.tl.models.application.game.ws_messages.messages;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tl.models.application.game.ws_messages.Message;
import com.tl.models.application.game.ws_messages.message_type.MessageType;
import com.tl.models.client.responses.CardResponse;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.SneakyThrows;

import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor
public class DealCardsMessage extends Message {

    @JsonProperty
    private List<CardResponse> cards;

    @SneakyThrows
    @Override
    public String serialize() {
        return new ObjectMapper().writeValueAsString(this);
    }

    public DealCardsMessage(List<CardResponse> cards) {
        super(MessageType.DealCards);
        this.cards = cards;
    }
}

