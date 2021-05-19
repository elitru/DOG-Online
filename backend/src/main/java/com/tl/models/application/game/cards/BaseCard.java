package com.tl.models.application.game.cards;

import com.tl.models.application.game.Game;

public abstract class BaseCard<T> {
    public abstract void makeMove(Game currentGame, T payload);
}
