package com.tl.models.application.game;

import com.tl.models.application.user.SessionUser;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@AllArgsConstructor
@ToString
@Data
@EqualsAndHashCode
public class Game {
    private Map<SessionUser, List<NinePin>> ninepins;
    private Map<Integer, Team> teams;
    private GameField field;

    public static final int MIN_PLAYERS = 3;
    public static final int MAX_PLAYERS = 8;

    public Optional<Team> getTeamForUser(SessionUser user) {
        return this.teams.values().stream().filter(t -> t.getMembers().contains(user)).findFirst();
    }
}
