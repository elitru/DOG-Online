package com.tl.services;

import com.tl.models.application.game.Game;
import com.tl.models.application.game.GameSessionContext;
import com.tl.models.application.game.states.IngameState;
import com.tl.models.application.game.states.LobbyState;
import com.tl.models.application.game.states.TeamAssignmentState;
import com.tl.models.application.user.SessionUser;
import com.tl.models.client.requests.CreateSessionRequest;
import com.tl.models.client.requests.JoinSessionRequest;
import io.quarkus.security.UnauthorizedException;
import org.jboss.resteasy.spi.NotImplementedYetException;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.BadRequestException;
import javax.ws.rs.NotFoundException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@ApplicationScoped
public class SessionService {

    private Map<UUID, GameSessionContext> sessions;

    @Inject
    private void initMap() {
        this.sessions = new HashMap<>();
    }

    public GameSessionContext getSessionOrThrow(UUID sessionId) {
        return Optional.ofNullable(this.sessions.get(sessionId)).orElseThrow(BadRequestException::new);
    }

    public GameSessionContext createSession(CreateSessionRequest request) {
        var context = new GameSessionContext();

        context.setState(new LobbyState(context), false);
        context.setSessionId(UUID.randomUUID());
        context.setSessionName(request.getSessionName());
        context.setPublicSession(request.getPublicSession());
        context.setPassword(request.getPassword());

        var newUser = context.getState().addUserToSession(request.getUserName());
        context.setOwner(newUser);

        this.sessions.put(context.getSessionId(), context);

        return context;

    }

    public SessionUser joinSession(JoinSessionRequest request) {
        var context = getSessionOrThrow(request.sessionId);

        if (context.getClients().size() == Game.MAX_PLAYERS) {
            throw new BadRequestException();
        }
        return context.getState().addUserToSession(request.userName, request.password);
    }

    public void quitSession(UUID sessionId, UUID userId) {
        var context = getSessionOrThrow(sessionId);

        context.getState().removeUserFromSession(userId);

        if (context.getClients().size() == 0) {
            this.sessions.remove(sessionId);
        }
    }

    public void joinTeam(UUID sessionId, UUID userId, int teamId) {
        var session = this.getSessionOrThrow(sessionId);
        var user = session.getUser(userId).orElseThrow(BadRequestException::new);
        session.getState().joinTeam(teamId, user);
    }

    public void advanceStateForSession(UUID sessionId, UUID userId) {
        var session = this.getSessionOrThrow(sessionId);
        if (!userId.equals(session.getOwner().getId())) {
            throw new UnauthorizedException();
        }
        this.advanceStateForSession(session);
    }

    public void advanceStateForSession(UUID sessionId) {
        var session = this.getSessionOrThrow(sessionId);
        this.advanceStateForSession(session);
    }

    public void advanceStateForSession(GameSessionContext context) {
        System.out.println("advancing state");
        var state = context.getState();
        if (state instanceof LobbyState) {
            // make sure that we have at least 3 players
            if (context.getClients().size() < Game.MIN_PLAYERS) {
                throw new BadRequestException();
            }
            context.setState(new TeamAssignmentState(context));
        } else if (state instanceof TeamAssignmentState) {
            System.out.println("got teamassignment state");
            // make sure to assign remaining members
            ((TeamAssignmentState) state).finish();
            context.setState(new IngameState(context));
        } else {
            // TODO implement switch to other states
            throw new NotImplementedYetException();
        }
    }
}
