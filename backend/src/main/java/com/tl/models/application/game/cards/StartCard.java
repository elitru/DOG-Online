package com.tl.models.application.game.cards;

import com.tl.models.application.game.Game;
import com.tl.models.application.game.GameSessionContext;
import com.tl.models.application.game.NinePin;
import com.tl.models.application.game.cards.payloads.JokerPayload;
import com.tl.models.application.game.cards.payloads.StartCardPayload;
import com.tl.models.application.user.SessionUser;

import javax.ws.rs.BadRequestException;
import java.util.*;

public class StartCard extends BaseCard<StartCardPayload> {

    private StartCardType type;

    public StartCard(StartCardType type) {
        super(type == StartCardType.Eleven ? "1_11" : "1_13");
        this.type = type;
    }

    @Override
    public int makeMove(GameSessionContext currentGame, StartCardPayload payload, UUID pinId, SessionUser user) {
        // user wants to move pin to board
        if (payload.moveToBoard) {
            return this.handleStart(currentGame, pinId, user);
        } else {
            return super.makeLinearMove(currentGame, pinId, user, payload.targetField);
        }
    }

    @Override
    public List<Integer> getPossibleMoves(NinePin pin, Game game, SessionUser user, JokerPayload payload) {
        if (pin.isOnHomeField()) {
            System.out.println("on home field");
            // move pin to board if not occupied
            if (!game.isStartFieldOccupiedByPlayerOfSameColor(game.getStartFields().get(user))) {
                return new ArrayList<>(Arrays.asList(game.getStartFields().get(user).getNodeId()));
            } else {
                return new ArrayList<>();
            }
        } else {
            System.out.println("not on home field");
            // pin is ingame / in target area --> calculate straight positions
            if (this.type == StartCardType.Eleven) {
                var one = game.getAllStraightWalkPositions(1, pin.getCurrentLocation(), user);
                one.addAll(game.getAllStraightWalkPositions(11, pin.getCurrentLocation(), user));
                return one;
            } else {
                return game.getAllStraightWalkPositions(13, pin.getCurrentLocation(), user);
            }
        }
    }

    @Override
    public Class<StartCardPayload> getType() {
        return StartCardPayload.class;
    }

    private int handleStart(GameSessionContext currentGame, UUID pinId, SessionUser user) {
        System.out.println("handling start");
        // get first start pin of user
        var pin = this.getPinFromHomeFieldForUser(currentGame.getGame(), user).orElseThrow(BadRequestException::new);
        // save previous location
        var previousLocation = pin.getCurrentLocation().getNodeId();
        // set the location to be the startfield of the current user
        pin.setCurrentLocation(currentGame.getGame().getStartFields().get(user));
        // broadcast movement
        pin.broadcastMovement(currentGame, previousLocation);
        // return id of start field
        return currentGame.getGame().getStartFields().get(user).getNodeId();
    }

    private Optional<NinePin> getPinFromHomeFieldForUser(Game currentGame, SessionUser user) {
        return currentGame.getNinepins().get(user).stream().filter(p -> p.getCurrentLocation().getNodeId() < 0 && p.getCurrentLocation().getNodeId() >= -16).max(Comparator.comparingInt(a -> a.getCurrentLocation().getNodeId()));
    }
}
