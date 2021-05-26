package com.tl.models.application.game.cards;

import com.tl.models.application.game.Game;

public class StartCard extends BaseCard<Void>{

    private StartCardType type;

    public StartCard(StartCardType type) {
        super();
        this.type = type;
    }

    @Override
    public void makeMove(Game currentGame, Void payload) {

    }
}
