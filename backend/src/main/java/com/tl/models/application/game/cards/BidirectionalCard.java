package com.tl.models.application.game.cards;

import com.tl.models.application.game.Game;

public class BidirectionalCard extends BaseCard<Void>{
    public BidirectionalCard() {
        super("4");
    }

    @Override
    public void makeMove(Game currentGame, Void payload) {

    }
}
