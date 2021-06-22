package com.tl.models.application.game.sub_states;

import com.tl.models.application.game.GameSessionContext;
import com.tl.models.application.game.ws_messages.messages.AskToPlayCardMessage;
import com.tl.models.application.user.SessionUser;

public class PlayRoundSubState extends IngameSubState {

    private int amountCards;
    private SessionUser startedRound;

    /**
     * One round --> until no player has any cards left
     *
     * @param context
     * @param startUser   The user who is supposed to start the round
     * @param amountCards The amount of cards at the beginning of the round
     */
    public PlayRoundSubState(GameSessionContext context, SessionUser startUser, int amountCards) {
        super(context);

        this.amountCards = amountCards;
        this.startedRound = startUser;

        this.announcePlayerIsToPlay(startUser);
    }

    @Override
    public void announcePlayerIsToPlay(SessionUser user) {
        System.out.printf("User %s is now to play!\n", user.getUsername());
        this.context.getClients().get(user.getId()).getWebsocketSession().getAsyncRemote().sendObject(new AskToPlayCardMessage(context.getGame().getCardMovesForUser(user)));
    }



}
