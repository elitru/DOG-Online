package com.tl.models.application.game.states;

import com.tl.models.application.game.GameSessionContext;
import com.tl.models.application.game.Team;
import com.tl.models.application.user.SessionUser;

import javax.ws.rs.BadRequestException;
import java.util.*;
import java.util.stream.Collectors;

public class TeamAssignmentState extends GameState {
    public TeamAssignmentState(GameSessionContext context) {
        Map<Integer, Team> teams = new HashMap<>();
        teams.put(0, new Team(0, new ArrayList<>(context.getClients().values())));

        addEmptyTeams(context.getClients().size() / (teamsSupported(context) ? 2 : 1), teams);

        context.getGame().setTeams(teams);
    }

    public boolean teamsSupported(GameSessionContext context) {
        return context.getClients().size() % 2 == 0;
    }

    private static void addEmptyTeams(int maxMembers, Map<Integer, Team> teams) {
        for (int i = 1; i <= maxMembers; i++) {
            teams.put(i, new Team(i, new ArrayList<>()));
        }
    }

    public void joinTeam(GameSessionContext context, int target, SessionUser user) {
        if (!teamsSupported(context)) {
            return;
        }
        var currentTeam = context.getGame().getTeamForUser(user).orElseThrow(BadRequestException::new);
        var targetTeam = Optional.ofNullable(context.getGame().getTeams().get(target)).orElseThrow(BadRequestException::new);
        if (targetTeam.isFull()) {
            throw new BadRequestException();
        }
        currentTeam.removeUserFromTeam(user);
        targetTeam.addUserToTeam(user);
    }

    private List<SessionUser> getNextUnassigned(int amount, GameSessionContext context) {

        var players = context.getGame().getTeams().get(0).getMembers().subList(0, amount);
        for (int i = 0; i < amount; i++) {
            context.getGame().getTeams().get(0).getMembers().remove(0);
        }
        
        return players;
    }

    public void finish(GameSessionContext context) {
        if (teamsSupported(context)) {
            var availableTeams = context.getGame().getTeams().values().stream().filter(t -> !t.isFull()).collect(Collectors.toList());
            for (Team t : availableTeams) {
                var nextPlayers = getNextUnassigned(t.remainingCapacity(), context);
                t.getMembers().addAll(nextPlayers);
            }
        } else {
            int currentTeam = 1;
            for (SessionUser u : context.getGame().getTeams().get(0).getMembers()) {
                context.getGame().getTeams().get(currentTeam).addUserToTeam(u);
                currentTeam++;
            }
        }
    }
}
