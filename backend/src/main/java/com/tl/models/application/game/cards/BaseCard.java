package com.tl.models.application.game.cards;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tl.models.application.game.Game;
import com.tl.models.application.game.GameSessionContext;
import com.tl.models.application.game.cards.payloads.StartCardPayload;
import com.tl.models.application.user.SessionUser;
import com.tl.models.client.responses.CardResponse;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.SneakyThrows;
import lombok.ToString;

import java.util.UUID;

@AllArgsConstructor
@Data
@ToString
public abstract class BaseCard<T> {
    protected UUID cardId;
    protected String stringRepresentation;

    private static ObjectMapper m = new ObjectMapper();

    public BaseCard(String stringRepresentation) {
        this.cardId = UUID.randomUUID();
        this.stringRepresentation = stringRepresentation;
    }

    public abstract void makeMove(GameSessionContext currentGame, T payload, UUID pinId, SessionUser user);

    @SneakyThrows
    public void makeMove(GameSessionContext currentGame, JsonNode node, UUID pinId, SessionUser user) {
        this.makeMove(currentGame, m.treeToValue(node, this.getType()), pinId, user);
    }

    @SneakyThrows
    public void makeMove(GameSessionContext currentGame, String node, UUID pinId, SessionUser user) {
        this.makeMove(currentGame, m.readValue(node, this.getType()), pinId, user);
    }

    public CardResponse toResponse() {
        return new CardResponse(this.cardId, this.stringRepresentation);
    }

    public abstract Class<T> getType();
}
