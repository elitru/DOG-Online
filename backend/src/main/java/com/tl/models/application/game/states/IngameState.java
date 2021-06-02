package com.tl.models.application.game.states;

import com.tl.models.application.game.GameSessionContext;
import com.tl.models.application.game.ws_messages.messages.StateChangedMessage;
import com.tl.models.application.game.ws_messages.messages.state_data_models.IngameStatePayload;
import com.tl.models.client.responses.NinePinResponse;
import com.tl.resources.GameSocketResource;

import java.util.stream.Collectors;

public class IngameState extends GameState {

    public IngameState(GameSessionContext context) {
        super(context);

        this.sendWSInitMessage();
    }

    @Override
    public void sendWSInitMessage() {
        System.out.println("sending game init message");
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