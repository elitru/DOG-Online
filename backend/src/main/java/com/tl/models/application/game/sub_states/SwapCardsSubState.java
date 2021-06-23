package com.tl.models.application.game.sub_states;

import com.tl.models.application.game.GameSessionContext;
import com.tl.models.application.game.ws_messages.messages.SwapCardMessage;
import com.tl.models.application.user.SessionUser;
import com.tl.models.client.responses.CardResponse;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

public class SwapCardsSubState extends IngameSubState {
    // keeps track of just how many have exchanged their cards
    private int counter = 0;

    private Map<SessionUser, CardResponse> swapped = new HashMap<>();

    private SessionUser startPlayer;

    public SwapCardsSubState(GameSessionContext ctx, SessionUser startPlayer) {
        super(ctx);
        this.startPlayer = startPlayer;
    }

    private boolean isSwapValid(UUID fromPlayer, UUID toPlayer, UUID cardId) {
        // return true if valid
        return (this.context.getClients().containsKey(fromPlayer) &&
                this.context.getClients().containsKey(toPlayer) &&
                this.context.getGame().getStack().getAllCards().containsKey(cardId));
    }

    @Override
    public void swapCard(UUID fromPlayer, UUID toPlayer, UUID cardId) {
        // make sure that everything is present
        if (!this.isSwapValid(fromPlayer, toPlayer, cardId))
            return;

        // add card to temporary buffer
        this.swapped.put(this.context.getClients().get(toPlayer), this.context.getGame().getStack().getAllCards().get(cardId).toResponse());

        // remove from player
        this.context.getGame().getCards().get(this.context.getClients().get(fromPlayer)).removeIf(c -> c.getCardId().equals(cardId));

        this.counter++;

        if (counter == 4) {
            // notify players of the cards they received
            this.sendBufferedCards();
            // notify the first player that he is now supposed to start
            this.context.getGame().setState(new PlayRoundSubState(context, this.startPlayer, 6));
        }
    }


    private void sendBufferedCards() {
        for (Map.Entry<SessionUser, CardResponse> entry : this.swapped.entrySet()) {
            // add card back
            this.context.getGame().getCards().get(this.context.getClients().get(entry.getKey().getId()))
                    .add(this.context.getGame().getStack().getAllCards().get(entry.getValue().getCardId()));

            this.context.getClients().get(entry.getKey().getId()).getWebsocketSession().getAsyncRemote().sendObject(new SwapCardMessage(entry.getValue()));
        }
    }
}
