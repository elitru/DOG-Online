package com.tl.models.application.game.ws_messages.messages;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.tl.models.application.game.ws_messages.Message;
import com.tl.models.application.game.ws_messages.message_type.MessageType;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class AnnounceWinnerMessage extends Message {
    @JsonProperty
    public int winner;

    public AnnounceWinnerMessage(int winner) {
        super(MessageType.AnnounceWinner);
        this.winner = winner;
    }
}
