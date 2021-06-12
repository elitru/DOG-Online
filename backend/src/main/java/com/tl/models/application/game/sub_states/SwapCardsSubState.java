package com.tl.models.application.game.sub_states;

import com.tl.models.application.game.GameSessionContext;
import com.tl.models.application.user.SessionUser;
import com.tl.models.client.responses.CardResponse;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class SwapCardsSubState extends IngameSubState {
    // keeps track of just how many have exchanged their cards
    private int counter = 0;

    private Map<SessionUser, CardResponse> swapped = new HashMap<>();

    public SwapCardsSubState(GameSessionContext ctx) {
        super(ctx);
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
        this.counter++;

        System.out.println("Added card to buffer: " + this.context.getGame().getStack().getAllCards().get(cardId).toResponse());
        System.out.println("Sent to user: " + this.context.getClients().get(toPlayer).getUsername());

        if (counter == 4) {
            // TODO advance state
        }
    }
}
