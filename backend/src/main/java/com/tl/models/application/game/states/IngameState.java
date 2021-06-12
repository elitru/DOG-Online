package com.tl.models.application.game.states;

import com.tl.models.application.game.GameBoard;
import com.tl.models.application.game.GameSessionContext;
import com.tl.models.application.game.NinePin;
import com.tl.models.application.game.sub_states.DealCardsSubState;
import com.tl.models.application.game.sub_states.PlayRoundSubState;
import com.tl.models.application.game.sub_states.SwapCardsSubState;
import com.tl.models.application.game.ws_messages.messages.AskToPlayCardMessage;
import com.tl.models.application.game.ws_messages.messages.StateChangedMessage;
import com.tl.models.application.game.ws_messages.messages.state_data_models.IngameStatePayload;
import com.tl.models.application.user.SessionUser;
import com.tl.models.client.responses.NinePinResponse;
import com.tl.resources.GameSocketResource;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.stream.Collectors;

public class IngameState extends GameState {

    public IngameState(GameSessionContext context) {
        super(context);

        this.context.getGame().setBoard(new GameBoard(context));
        this.context.getGame().initNinePins();
        this.sendWSInitMessage();

        // deal the first round of cards
        this.context.getGame().setState(new DealCardsSubState(this.context, 6));
        // notify the players that they've now got to swap their cards
        var p = this.getRandomPlayer();
        this.context.getGame().setState(new SwapCardsSubState(context, p));
    }

    private SessionUser getRandomPlayer() {
        int index = new Random().nextInt(4);
        var player = this.context.getGame().getUserForIndex(index);
        return player;
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