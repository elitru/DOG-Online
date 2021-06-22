package com.tl.models.application.game.cards;

import com.tl.models.application.game.Game;
import com.tl.models.application.game.GameSessionContext;
import com.tl.models.application.game.NinePin;
import com.tl.models.application.game.cards.payloads.StartCardPayload;
import com.tl.models.application.user.SessionUser;

import javax.ws.rs.BadRequestException;
import java.util.Comparator;
import java.util.Optional;
import java.util.UUID;

public class StartCard extends BaseCard<StartCardPayload> {

    private StartCardType type;

    public StartCard(StartCardType type) {
        super(type == StartCardType.Eleven ? "1_11" : "1_13");
        this.type = type;
    }

    @Override
    public void makeMove(GameSessionContext currentGame, StartCardPayload payload, UUID pinId, SessionUser user) {
        switch (payload.action) {
            case -1:
                this.handleStart(currentGame, pinId, user);
                break;
            case 11:
            case 13:
                this.handleNumber(currentGame, pinId, payload.action);
                break;
            default:
                throw new BadRequestException("Received invalid amount for start card");
        }
    }

    private void handleNumber(GameSessionContext currentGame, UUID pinId, int number) {

    }

    private void handleStart(GameSessionContext currentGame, UUID pinId, SessionUser user) {
        // get first start pin of user
        var pin = this.getPinFromHomeFieldForUser(currentGame.getGame(), user).orElseThrow(BadRequestException::new);
        // save previous location
        var previousLocation = pin.getCurrentLocation().getNodeId();
        // set the location to be the startfield of the current user
        pin.setCurrentLocation(currentGame.getGame().getStartFields().get(user));
        // broadcast movement
        pin.broadcastMovement(currentGame, previousLocation);
    }

    private Optional<NinePin> getPinFromHomeFieldForUser(Game currentGame, SessionUser user) {
        return currentGame.getNinepins().get(user).stream().filter(p -> p.getCurrentLocation().getNodeId() < 0 && p.getCurrentLocation().getNodeId() >= -16).max(Comparator.comparingInt(a -> a.getCurrentLocation().getNodeId()));
    }

    @Override
    public Class<StartCardPayload> getType() {
        return StartCardPayload.class;
    }

    @Override
    public boolean isMovePossible(NinePin pin, Game game, SessionUser user) {
        System.out.println("Startfield occupied: " + game.isStartFieldOccupiedByPlayerOfSameColor(game.getStartFields().get(user)));
        return !game.isStartFieldOccupiedByPlayerOfSameColor(game.getStartFields().get(user)) && pin.getCurrentLocation().getNodeId() < 0 && pin.getCurrentLocation().getNodeId() >= -16;
    }
}
