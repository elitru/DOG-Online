package com.tl.models.application.game.states;

import com.tl.models.application.game.Game;
import com.tl.models.application.game.GameBoard;
import com.tl.models.application.game.GameSessionContext;
import com.tl.models.application.game.Team;
import com.tl.models.application.game.ws_messages.messages.StateChangedMessage;
import com.tl.models.application.game.ws_messages.messages.UserChangeTeamMessage;
import com.tl.models.application.user.SessionUser;
import com.tl.resources.GameSocketResource;
import com.tl.services.SessionService;

import javax.inject.Inject;
import javax.ws.rs.BadRequestException;
import java.util.*;
import java.util.stream.Collectors;

public class TeamAssignmentState extends GameState {
    @Inject
    SessionService sessionService;
    
    public TeamAssignmentState(GameSessionContext context) {
        super(context);

        Map<Integer, Team> teams = new HashMap<>();
        teams.put(0, new Team(0, new ArrayList<>(this.context.getClients().values())));

        addEmptyTeams(this.context.getClients().size() / (teamsSupported() ? 2 : 1), teams);

        this.context.setGame(new Game(this.context, teams));

        if (!this.teamsSupported()) {
            this.finish();
            this.sessionService.advanceStateForSession(context);
        }
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
        System.out.println(this.context.getGame().getTeams());
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

        // notify clients that a user has switched teams
        GameSocketResource.makeGameBroadcast(context, new UserChangeTeamMessage(user.getId(), targetTeam.getTeamId(), currentTeam.getTeamId()));
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
        System.out.println(this.context.getGame().getTeams());
    }
}
