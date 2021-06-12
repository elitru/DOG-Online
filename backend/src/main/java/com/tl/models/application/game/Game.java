package com.tl.models.application.game;

import com.tl.models.application.game.field.HomeField;
import com.tl.models.application.game.field.BaseField;
import com.tl.models.application.game.field.StartField;
import com.tl.models.application.game.sub_states.IngameSubState;
import com.tl.models.application.user.SessionUser;
import lombok.*;

import java.util.*;

@AllArgsConstructor
@NoArgsConstructor
@ToString
@Data
@EqualsAndHashCode
public class Game {
    private Map<SessionUser, List<NinePin>> ninepins = new HashMap<>();
    private Map<SessionUser, StartField> startFields = new HashMap<>();
    private Map<Integer, Team> teams = new HashMap<>();
    private CardStack stack;
    private GameBoard board;
    private IngameSubState state;

    public static final int MIN_PLAYERS = 3;
    public static final int MAX_PLAYERS = 8;

    public void setState(IngameSubState state) {
        this.state = state;
    }

    public Game(GameSessionContext ctx, Map<Integer, Team> teams) {
        this.teams = teams;
        ctx.setGame(this);
        this.stack = new CardStack();
    }

    public void initField(GameSessionContext ctx) {
        this.board = new GameBoard(ctx);
    }

    public Optional<Team> getTeamForUser(SessionUser user) {
        return this.teams.values().stream().filter(t -> t.getMembers().contains(user)).findFirst();
    }

    public void initNinePins() {
        var color = NinePinColor.Red;
        for (int i = 0; i < 4; i++) {
            int team = 0, player = 0;
            switch (i) {
                case 0:
                    player = 0;
                    team = 1;
                    break;
                case 1:
                    player = 0;
                    team = 2;
                    break;
                case 2:
                    team = 1;
                    player = 1;
                    break;
                case 3:
                    team = 2;
                    player = 1;
                    break;
            }
            var p = this.teams.get(team).getMembers().get(player);
            this.initNinePinsForUser(p, this.startFields.get(p).getFirstHomeField(), color);
            color = color.next();
        }
    }

    private void initNinePinsForUser(SessionUser user, HomeField field, NinePinColor color) {
        List<NinePin> pins = new ArrayList<>();
        BaseField next = field;

        while (next != null) {

            pins.add(new NinePin(next, color));

            next = next.getNext().orElse(null);
        }

        this.ninepins.put(user, pins);
    }
}
