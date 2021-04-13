package com.tl.services;

import com.tl.models.application.game.Game;
import com.tl.models.application.game.GameSessionContext;
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

    public GameSessionContext createSession(CreateSessionRequest request) {
        var context = new GameSessionContext();

        context.setState(new LobbyState());
        context.setSessionId(UUID.randomUUID());
        context.setSessionName(request.getSessionName());
        context.setPublicSession(request.getPublicSession());
        context.setPassword(request.getPassword());

        var newUser = context.getState().addUserToSession(context, request.getUserName());
        context.setOwner(newUser);

        this.sessions.put(context.getSessionId(), context);

        return context;

    }

    public SessionUser joinSession(JoinSessionRequest request) {
        var context = getSessionContext(request.sessionId);

        if (context.getClients().size() == Game.MAX_PLAYERS) {
            throw new BadRequestException();
        }
        return context.getState().addUserToSession(context, request.userName, request.password);
    }

    public void quitSession(UUID sessionId, UUID userId) {
        var context = getSessionContext(sessionId);

        context.getState().removeUserFromSession(context, userId);

        if (context.getClients().size() == 0) {
            this.sessions.remove(sessionId);
        }
    }

    public GameSessionContext getContextById(UUID sessionId) {
        return this.sessions.get(sessionId);
    }

    private GameSessionContext getSessionContext(UUID sessionId) {
        var context = this.sessions.get(sessionId);
        if (context == null)
            throw new NotFoundException();
        return context;
    }

    public void advanceStateForSession(UUID sessionId, UUID userId) {
        var session = Optional.ofNullable(this.sessions.get(sessionId)).orElseThrow(BadRequestException::new);
        if (!userId.equals(session.getOwner().getId())) {
            throw new UnauthorizedException();
        }
        this.advanceStateForSession(session);
    }

    public void advanceStateForSession(UUID sessionId) {
        var session = Optional.ofNullable(this.sessions.get(sessionId)).orElseThrow(BadRequestException::new);
        this.advanceStateForSession(session);
    }

    public void advanceStateForSession(GameSessionContext context) {
        var state = context.getState();
        if (state instanceof LobbyState) {
            context.setState(new TeamAssignmentState(context));
        } else {
            // TODO implement switch to other states
            throw new NotImplementedYetException();
        }
    }
}
