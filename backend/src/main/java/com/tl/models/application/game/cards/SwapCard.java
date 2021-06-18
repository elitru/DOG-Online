package com.tl.models.application.game.cards;

import com.tl.models.application.game.Game;
import com.tl.models.application.game.GameSessionContext;
import com.tl.models.application.game.NinePin;
import com.tl.models.application.user.SessionUser;

import java.util.UUID;

public class SwapCard extends BaseCard<Void>{

    public SwapCard() {
        super("swap");
    }

    @Override
    public void makeMove(GameSessionContext currentGame, Void payload, UUID pinId, SessionUser user) {

    }
    @Override
    public Class<Void> getType() {
        return Void.class;
    }

    @Override
    public boolean isMovePossible(NinePin pin, Game game, SessionUser user) {
        return game.amountOfPinsIngame(user) > 0;
    }
}
