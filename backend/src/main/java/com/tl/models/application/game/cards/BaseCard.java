package com.tl.models.application.game.cards;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tl.models.application.game.Game;
import com.tl.models.application.game.GameSessionContext;
import com.tl.models.application.game.NinePin;
import com.tl.models.application.game.cards.payloads.JokerPayload;
import com.tl.models.application.game.cards.payloads.StartCardPayload;
import com.tl.models.application.user.SessionUser;
import com.tl.models.client.responses.CardResponse;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.SneakyThrows;
import lombok.ToString;

import java.util.List;
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

    public abstract int makeMove(GameSessionContext currentGame, T payload, UUID pinId, SessionUser user);

    @SneakyThrows
    public int makeMove(GameSessionContext currentGame, String node, UUID pinId, SessionUser user) {
        return this.makeMove(currentGame, m.readValue(node, this.getType()), pinId, user);
    }

    protected int makeLinearMove(GameSessionContext currentGame, UUID pinId, SessionUser user, int to) {
        var pin = currentGame.getGame().getNinePinById(pinId);
        int prev = pin.getCurrentLocation().getNodeId();
        var targetField = currentGame.getGame().getFieldForFieldId(to, user);
        pin.setCurrentLocation(targetField);
        pin.broadcastMovement(currentGame, prev);
        return targetField.getNodeId();
    }

    public CardResponse toResponse() {
        return new CardResponse(this.cardId, this.stringRepresentation);
    }

    public abstract Class<T> getType();

    public abstract List<Integer> getPossibleMoves(NinePin pin, Game game, SessionUser user, JokerPayload payload);
}
