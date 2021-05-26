package com.tl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tl.models.application.game.*;
import com.tl.models.application.user.SessionUser;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.is;

public class ExampleResourceTest {

    @Test
    public void testGameBoard() throws JsonProcessingException {
        var teams = new HashMap<Integer, Team>() {{
            //put(0, new Team(0, Arrays.asList(new SessionUser("1"), new SessionUser("2"), new SessionUser("3"), new SessionUser("4"))));
            put(1, new Team(1, Arrays.asList(new SessionUser("1"), new SessionUser("2"))));
            put(2, new Team(2, Arrays.asList(new SessionUser("3"), new SessionUser("4"))));
        }};

        var ctx = new GameSessionContext();
        var game = new Game(ctx, teams);
        game.initField(ctx);

        var board = game.getField();

        var start = board.getReference();

        System.out.println(board.toResponseList(game.getStartFields()));

        //Assertions.assertSame(start, start.getPrevious().get().getNext().get());
        //Assertions.assertEquals(board.toResponseList(game.getStartFields()).size(), 24);
        //System.out.println(new ObjectMapper().writeValueAsString(board.toResponseList(game.getStartFields())));
    }

    @Test
    public void testCardStackBelowLimit() {
        var cards = new CardStack();
        Assertions.assertEquals(cards.getCardStack().size(), 110);
        for (int i = 0; i < CardStack.MAX_BUFFER_CAP - 1; i++) {
            var card = cards.drawCard();
            cards.playCard(card.getCardId());
        }
        Assertions.assertEquals(cards.getCardStack().size(), cards.getAllCards().size() - CardStack.MAX_BUFFER_CAP + 1);
    }

    @Test
    public void testCardStackLimit() {
        var cards = new CardStack();
        Assertions.assertEquals(cards.getCardStack().size(), 110);
        for (int i = 0; i < CardStack.MAX_BUFFER_CAP; i++) {
            var card = cards.drawCard();
            cards.playCard(card.getCardId());
        }
        Assertions.assertEquals(cards.getCardStack().size(), cards.getAllCards().size());
    }
}