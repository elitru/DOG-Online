package com.tl.models.application.game;

import com.tl.models.application.game.field.HomeField;
import com.tl.models.application.game.field.StartField;
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
    private GameBoard field;

    public static final int MIN_PLAYERS = 3;
    public static final int MAX_PLAYERS = 8;

    public Game(GameSessionContext ctx, Map<Integer, Team> teams) {
        this.teams = teams;
        ctx.setGame(this);
        this.initNinePins();
        this.initField(ctx);
    }

    public void initField(GameSessionContext ctx) {
        this.field = new GameBoard(ctx);
    }

    public Optional<Team> getTeamForUser(SessionUser user) {
        return this.teams.values().stream().filter(t -> t.getMembers().contains(user)).findFirst();
    }

    private void initNinePins() {
        startFields.forEach((k, v) -> this.initNinePinsForUser(k, v.getFirstHomeField()));
    }

    private void initNinePinsForUser(SessionUser user, HomeField field) {
        List<NinePin> pins = new ArrayList<>();
        var next = field;

        do {
            pins.add(new NinePin(next));
        }while(next.hasNext());

        this.ninepins.put(user, pins);
    }
}
