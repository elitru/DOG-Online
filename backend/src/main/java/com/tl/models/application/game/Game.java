package com.tl.models.application.game;

import com.tl.models.application.game.field.HomeField;
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

    public Game(GameSessionContext ctx, Map<Integer, Team> teams) {
        this.teams = teams;
        ctx.setGame(this);
        this.initNinePins();
        this.stack = new CardStack();
    }

    public void initField(GameSessionContext ctx) {
        this.board = new GameBoard(ctx);
    }

    public Optional<Team> getTeamForUser(SessionUser user) {
        return this.teams.values().stream().filter(t -> t.getMembers().contains(user)).findFirst();
    }

    private void initNinePins() {
        var color = NinePinColor.Red;

        for (Map.Entry<SessionUser, StartField> entry : this.startFields.entrySet()) {
            this.initNinePinsForUser(entry.getKey(), entry.getValue().getFirstHomeField(), color);
            color = color.next();
        }
    }

    private void initNinePinsForUser(SessionUser user, HomeField field, NinePinColor color) {
        List<NinePin> pins = new ArrayList<>();
        var next = field;

        do {
            pins.add(new NinePin(next, color));
        } while (next.hasNext());

        this.ninepins.put(user, pins);
    }
}
