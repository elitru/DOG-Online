package com.tl.models.application.game.ws_messages;

import javax.websocket.*;

public class MessageEncoder implements Encoder.Text<Message> {

    @Override
    public void init(EndpointConfig endpointConfig) {

    }

    @Override
    public void destroy() {

    }

    @Override
    public String encode(Message message) throws EncodeException {
        return message.serialize();
    }
}
