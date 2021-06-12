package com.tl.models.application.game.ws_messages.messages;

import com.tl.models.application.game.ws_messages.Message;
import com.tl.models.application.game.ws_messages.message_type.MessageType;

public class AskToPlayCardMessage extends Message {
    public AskToPlayCardMessage() {
        super(MessageType.AskToPlay);
    }
}
