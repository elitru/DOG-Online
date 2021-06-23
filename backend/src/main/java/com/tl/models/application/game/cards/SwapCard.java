package com.tl.models.application.game.cards;

import com.tl.models.application.game.Game;
import com.tl.models.application.game.GameSessionContext;
import com.tl.models.application.game.NinePin;
import com.tl.models.application.game.cards.payloads.JokerPayload;
import com.tl.models.application.game.cards.payloads.SwapCardPayload;
import com.tl.models.application.user.SessionUser;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

public class SwapCard extends BaseCard<SwapCardPayload>{

    public SwapCard() {
        super("swap");
    }

    @Override
    public void makeMove(GameSessionContext currentGame, SwapCardPayload payload, UUID pinId, SessionUser user) {
        var fromPin = currentGame.getGame().getNinePinById(payload.firstPin);
        var toPin = currentGame.getGame().getNinePinById(payload.secondPin);

        var fromLocation = fromPin.getCurrentLocation();
        var fromLocationId = fromLocation.getNodeId();

        var toLocation = toPin.getCurrentLocation();
        var toLocationId = fromLocation.getNodeId();

        fromPin.setCurrentLocation(toLocation);
        toPin.setCurrentLocation(fromLocation);

        fromPin.broadcastMovement(currentGame, fromLocationId);
        toPin.broadcastMovement(currentGame, toLocationId);
    }

    @Override
    public Class<SwapCardPayload> getType() {
        return SwapCardPayload.class;
    }

    @Override
    public List<Integer> getPossibleMoves(NinePin pin, Game game, SessionUser user, JokerPayload payload) {
        if (game.amountOfPinsIngame(user) > 1) {
            return new ArrayList<>(Arrays.asList(-40000));
        } else {
            return new ArrayList<>(Arrays.asList(-30000));
        }
    }
}
