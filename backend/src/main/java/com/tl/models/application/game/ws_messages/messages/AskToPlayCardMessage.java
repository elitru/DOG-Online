package com.tl.models.application.game.ws_messages.messages;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.tl.models.application.game.ws_messages.Message;
import com.tl.models.application.game.ws_messages.message_type.MessageType;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public class AskToPlayCardMessage extends Message {

    @JsonProperty
    private Map<UUID, List<UUID>> cardMoves;

    public AskToPlayCardMessage(Map<UUID, List<UUID>> cardMoves) {
        super(MessageType.AskToPlay);
        this.cardMoves = cardMoves;
    }
}
