package com.tl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.tl.models.application.game.Game;
import com.tl.models.application.game.GameBoard;
import com.tl.models.application.game.GameSessionContext;
import com.tl.models.application.game.Team;
import com.tl.models.application.user.SessionUser;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.HashMap;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.is;

public class ExampleResourceTest {

    @Test
    public void testGameBoard() throws JsonProcessingException {
        var teams = new HashMap<Integer, Team>() {{
            //put(0, new Team(0, Arrays.asList(new SessionUser("1"), new SessionUser("2"), new SessionUser("3"), new SessionUser("4"))));
            put(0, new Team(0, Arrays.asList(new SessionUser("1"))));
        }};
        System.out.println(teams.get(0));

        var ctx = new GameSessionContext();
        var game = new Game(ctx, teams);

        var board = game.getField();

        var start = board.getReference();
        Assertions.assertSame(start, start.getPrevious().get().getNext().get());
        Assertions.assertEquals(board.toResponseList().size(), 24);
    }
}