package com.tl.models.application.game.states;

import com.tl.models.application.game.GameSessionContext;
import org.jboss.resteasy.spi.NotImplementedYetException;

public class IngameState extends GameState {

    public IngameState(GameSessionContext context) {
        super(context);
    }

    @Override
    public void sendWSInitMessage() {
        throw new NotImplementedYetException();
    }
}
