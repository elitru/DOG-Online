package com.tl.models.application.game.sub_states;

import com.tl.models.application.game.GameSessionContext;
import com.tl.models.application.game.cards.BaseCard;
import com.tl.models.application.game.ws_messages.messages.DealCardsMessage;
import com.tl.models.application.user.SessionUser;

import java.util.stream.Collectors;

public class DealCardsSubState extends IngameSubState {
    public DealCardsSubState(GameSessionContext context, int amount) {
        super(context);
        this.dealCards(amount);
    }

    @Override
    public void dealCards(int amount) {
        var stack = context.getGame().getStack();
        for (SessionUser s : context.getClients().values()) {
            var cards = stack.drawNCards(amount);
            context.getGame().getCards().put(s, cards);
            var msg = new DealCardsMessage(cards.stream().map(BaseCard::toResponse).collect(Collectors.toList()));
            s.getWebsocketSession().getAsyncRemote().sendObject(msg);
        }
    }
}
