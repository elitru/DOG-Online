package com.tl.models.application.game.cards;

import com.tl.models.application.game.Game;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.ToString;

import java.util.UUID;

@AllArgsConstructor
@Data
@ToString
public abstract class BaseCard<T> {
    protected UUID cardId;

    public BaseCard() {
        this.cardId = UUID.randomUUID();
    }

    public abstract void makeMove(Game currentGame, T payload);
}
