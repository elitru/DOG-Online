package com.tl.models.application.game.cards;

import com.tl.models.application.game.Game;
import com.tl.models.application.game.GameSessionContext;
import com.tl.models.application.game.NinePin;
import com.tl.models.application.game.cards.payloads.BaseCardPayload;
import com.tl.models.application.game.cards.payloads.JokerPayload;
import com.tl.models.application.game.field.BaseField;
import com.tl.models.application.user.SessionUser;

import java.util.List;
import java.util.UUID;

public class SimpleCard extends BaseCard<BaseCardPayload>{

    private int value;

    public SimpleCard(int value) {
        super(value + "");
        this.value = value;
    }

    @Override
    public int makeMove(GameSessionContext currentGame, BaseCardPayload payload, UUID pinId, SessionUser user) {
        return super.makeLinearMove(currentGame, pinId, user, payload.targetField);
    }

    @Override
    public Class<BaseCardPayload> getType() {
        return BaseCardPayload.class;
    }

    @Override
    public List<Integer> getPossibleMoves(NinePin pin, Game game, SessionUser user, JokerPayload payload) {
        return game.getAllStraightWalkPositions(this.value, pin.getCurrentLocation(), user);
    }
}
