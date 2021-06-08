package com.tl.models.application.game.cards;

import com.tl.models.application.game.Game;

public class StartCard extends BaseCard<Void>{

    private StartCardType type;

    public StartCard(StartCardType type) {
        super(type == StartCardType.Eleven ? "1_11" : "1_13");
        this.type = type;
    }

    @Override
    public void makeMove(Game currentGame, Void payload) {

    }
}
