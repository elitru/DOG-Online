package com.tl.models.application.game.ws_messages;

import javax.websocket.*;

public class MessageDecoder implements Decoder.Text<Message> {
    @Override
    public void init(EndpointConfig ec) { }
    @Override
    public void destroy() { }

    @Override
    public Message decode(String s) throws DecodeException {
        return MessageMapper.fromJsonString(s);
    }

    @Override
    public boolean willDecode(String s) {
        return true;
    }
}
