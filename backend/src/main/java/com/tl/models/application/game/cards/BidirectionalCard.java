package com.tl.models.application.game.cards;

import com.tl.models.application.game.Game;
import com.tl.models.application.game.GameSessionContext;
import com.tl.models.application.user.SessionUser;

import java.util.UUID;

public class BidirectionalCard extends BaseCard<Void>{
    public BidirectionalCard() {
        super("4");
    }

    @Override
    public void makeMove(GameSessionContext currentGame, Void payload, UUID pinId, SessionUser user) {

    }

    @Override
    public Class<Void> getType() {
        return Void.class;
    }
}
