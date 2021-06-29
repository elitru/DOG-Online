package com.tl.models.application.game.states;

import com.tl.models.application.game.GameBoard;
import com.tl.models.application.game.GameSessionContext;
import com.tl.models.application.game.NinePin;
import com.tl.models.application.game.cards.payloads.JokerPayload;
import com.tl.models.application.game.sub_states.DealCardsSubState;
import com.tl.models.application.game.sub_states.PlayRoundSubState;
import com.tl.models.application.game.sub_states.SwapCardsSubState;
import com.tl.models.application.game.ws_messages.messages.AskToPlayCardMessage;
import com.tl.models.application.game.ws_messages.messages.FinishGameMessage;
import com.tl.models.application.game.ws_messages.messages.StateChangedMessage;
import com.tl.models.application.game.ws_messages.messages.state_data_models.IngameStatePayload;
import com.tl.models.application.user.SessionUser;
import com.tl.models.client.requests.DropCardRequest;
import com.tl.models.client.requests.PlayCardRequest;
import com.tl.models.client.responses.NinePinResponse;
import com.tl.resources.GameSocketResource;

import java.util.*;
import java.util.stream.Collectors;

public class IngameState extends GameState {

    public IngameState(GameSessionContext context) {
        super(context);

        this.context.getGame().setBoard(new GameBoard(context));
        this.context.getGame().initNinePins();
        this.sendWSInitMessage();

        // deal the first round of cards
        this.context.getGame().setState(new DealCardsSubState(this.context, DealCardsSubState.START_WITH));
        // notify the players that they've now got to swap their cards
        var p = this.getRandomPlayer();
        this.context.getGame().setState(new SwapCardsSubState(context, p, DealCardsSubState.START_WITH));
    }

    @Override
    public void removeUserFromSession(UUID userId) {
        GameSocketResource.makeGameBroadcast(this.context, new FinishGameMessage());
    }

    private SessionUser getRandomPlayer() {
        int index = new Random().nextInt(4);
        var player = this.context.getGame().getUserForIndex(index);
        System.out.println("Selected player " + player.getUsername() + " to start the game.");
        return player;
    }

    @Override
    public void playCard(PlayCardRequest request, SessionUser user) {
        this.context.getGame().playCard(this.context, request, user);
    }

    @Override
    public void dropCard(DropCardRequest request, SessionUser user) {
        this.context.getGame().dropCard(this.context, request, user);
    }

    @Override
    public List<Integer> calculateAllMoves(UUID pinId, UUID cardId, JokerPayload payload, SessionUser player) {
        return this.context.getGame().calculateAllMoves(pinId, cardId, payload, player);
    }

    @Override
    public void sendWSInitMessage() {
        var message = new StateChangedMessage(GameStateIdentifier.Ingame,
                new IngameStatePayload(context.getGame().getBoard().toResponseList(this.context.getGame().getStartFields()),
                context.getGame().getNinepins()
                        .entrySet()
                        .stream()
                        .collect(Collectors.toMap(
                                e -> e.getKey().getId(),
                                e -> e.getValue()
                                        .stream()
                                        .map(NinePinResponse::from)
                                        .collect(Collectors.toList())
                            )
                        )));
        GameSocketResource.makeGameBroadcast(context, message);
    }
}