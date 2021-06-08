package com.tl.models.application.game.cards;

import com.tl.models.application.game.Game;

public class SimpleCard extends BaseCard<Void>{

    private int value;

    public SimpleCard(int value) {
        super(value + "");
        this.value = value;
    }

    @Override
    public void makeMove(Game currentGame, Void payload) {

    }


}
