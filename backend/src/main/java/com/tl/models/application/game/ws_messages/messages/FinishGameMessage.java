package com.tl.models.application.game.ws_messages.messages;

import com.tl.models.application.game.ws_messages.Message;
import com.tl.models.application.game.ws_messages.message_type.MessageType;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
public class FinishGameMessage extends Message {
    public FinishGameMessage() {
        super(MessageType.FinishGame);
    }
}
