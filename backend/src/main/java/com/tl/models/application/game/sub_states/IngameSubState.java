package com.tl.models.application.game.sub_states;

import com.tl.models.application.game.GameSessionContext;
import com.tl.models.application.game.states.IngameState;
import com.tl.models.application.user.SessionUser;

import javax.ws.rs.BadRequestException;
import java.util.UUID;

public abstract class IngameSubState {
    protected GameSessionContext context;
    private static final String BAD_STATE = "This operation is not possible in the current sub game state.";

    public IngameSubState(GameSessionContext context) {
        this.context = context;
    }

    public void dealCards(int amount) {
        throw new BadRequestException(BAD_STATE);
    }

    public void swapCard(UUID fromPlayer, UUID toPlayer, UUID cardId) {
        throw new BadRequestException(BAD_STATE);
    }

}
