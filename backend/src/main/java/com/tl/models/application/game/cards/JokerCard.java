package com.tl.models.application.game.cards;

import com.tl.models.application.game.Game;
import com.tl.models.application.game.GameSessionContext;
import com.tl.models.application.game.NinePin;
import com.tl.models.application.game.cards.payloads.JokerPayload;
import com.tl.models.application.user.SessionUser;

import javax.ws.rs.BadRequestException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

public class JokerCard extends BaseCard<JokerPayload>{

    public JokerCard() {
        super("joker");
    }

    @Override
    public void makeMove(GameSessionContext currentGame, JokerPayload payload, UUID pinId, SessionUser user) {
        var card = this.fromString(payload.cardType);
        card.makeMove(currentGame, payload.cardPayload, pinId, user);
    }

    @Override
    public Class<JokerPayload> getType() {
        return JokerPayload.class;
    }

    @Override
    public List<Integer> getPossibleMoves(NinePin pin, Game game, SessionUser user, JokerPayload payload) {
        // payload can only be null if the method was called when trying to determine whether or not
        // the card can be played if a new turn is announced
        if (payload == null)
            return new ArrayList<>(Arrays.asList(-20000));

        // here, the availableMoves endpoint was called
        var card = this.fromString(payload.cardType);

        // can never be recursive since we're never constructing a joker!
        return card.getPossibleMoves(pin, game, user, null);
    }

    private BaseCard fromString(String input) {
        //    private static int[] normalCards = {2, 3, 5, 6, 8, 9, 10, 12};
        switch (input) {
            case "2":
            case "3":
            case "5":
            case "6":
            case "8":
            case "9":
            case "10":
            case "12":
                return new SimpleCard(Integer.parseInt(input));
            case "swap":
                return new SwapCard();
            case "1_11":
                    return new StartCard(StartCardType.Eleven);
            case "1_13":
                return new StartCard(StartCardType.Thirteen);
            case "4":
                return new BidirectionalCard();
            default:
                throw new BadRequestException("Invalid card string");
        }
    }
}