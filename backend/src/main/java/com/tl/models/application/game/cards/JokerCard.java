package com.tl.models.application.game.cards;

import com.tl.models.application.game.Game;

public class JokerCard extends BaseCard<Void>{

    public JokerCard() {
        super("joker");
    }

    @Override
    public void makeMove(Game currentGame, Void payload) {

    }
}