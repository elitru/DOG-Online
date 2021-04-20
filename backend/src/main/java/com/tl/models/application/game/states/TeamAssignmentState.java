package com.tl.models.application.game.states;

import com.tl.models.application.game.GameSessionContext;
import com.tl.models.application.game.Team;
import com.tl.models.application.game.ws_messages.messages.StateChangedMessage;
import com.tl.models.application.user.SessionUser;
import com.tl.resources.GameSocketResource;

import javax.websocket.Session;
import javax.ws.rs.BadRequestException;
import java.util.*;
import java.util.stream.Collectors;

public class TeamAssignmentState extends GameState {
    
    public TeamAssignmentState(GameSessionContext context) {
        super(context);

        Map<Integer, Team> teams = new HashMap<>();
        teams.put(0, new Team(0, new ArrayList<>(this.context.getClients().values())));

        addEmptyTeams(this.context.getClients().size() / (teamsSupported() ? 2 : 1), teams);

        this.context.getGame().setTeams(teams);
    }

    public void sendWSInitMessage() {
        var message = new StateChangedMessage(GameStateIdentifier.TeamAssignment, null);
        GameSocketResource.makeGameBroadcast(context, message);
    }

    private List<SessionUser> getNextUnassigned(int amount) {
        var players = context.getGame().getTeams().get(0).getMembers().subList(0, amount);
        for (int i = 0; i < amount; i++) {
            context.getGame().getTeams().get(0).getMembers().remove(0);
        }

        return players;
    }

    private static void addEmptyTeams(int maxMembers, Map<Integer, Team> teams) {
        for (int i = 1; i <= maxMembers; i++) {
            teams.put(i, new Team(i, new ArrayList<>()));
        }
    }

    public boolean teamsSupported() {
        return this.context.getClients().size() % 2 == 0;
    }

    public void joinTeam(int target, SessionUser user) {
        if (!teamsSupported()) {
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

    public void removeUserFromSession(UUID userId) {
        var lobbyState = new LobbyState(context);
        lobbyState.removeUserFromSession(userId);
        context.setState(lobbyState);
    }

    public void finish() {
        if (teamsSupported()) {
            var availableTeams = context.getGame().getTeams().values().stream().filter(t -> !t.isFull()).collect(Collectors.toList());
            for (Team t : availableTeams) {
                var nextPlayers = getNextUnassigned(t.remainingCapacity());
                nextPlayers.forEach(t::addUserToTeam);
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
