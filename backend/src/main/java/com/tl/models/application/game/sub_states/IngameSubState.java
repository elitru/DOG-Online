package com.tl.models.application.game.sub_states;

import com.tl.models.application.game.GameSessionContext;
import com.tl.models.application.user.SessionUser;

import javax.ws.rs.BadRequestException;

public abstract class IngameSubState {
    private static final String BAD_STATE = "This operation is not possible in the current game state.";

    public void dealCards(GameSessionContext context, int amount) {
        throw new BadRequestException(BAD_STATE);
    }

}
