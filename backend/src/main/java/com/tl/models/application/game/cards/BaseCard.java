package com.tl.models.application.game.cards;

import com.tl.models.application.game.Game;
import com.tl.models.client.responses.CardResponse;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.ToString;

import java.util.UUID;

@AllArgsConstructor
@Data
@ToString
public abstract class BaseCard<T> {
    protected UUID cardId;
    protected String stringRepresentation;

    public BaseCard(String stringRepresentation) {
        this.cardId = UUID.randomUUID();
        this.stringRepresentation = stringRepresentation;
    }

    public abstract void makeMove(Game currentGame, T payload);

    public CardResponse toResponse() {
        return new CardResponse(this.cardId, this.stringRepresentation);
    }
}
