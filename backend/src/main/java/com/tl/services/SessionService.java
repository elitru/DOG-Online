package com.tl.services;

import com.tl.models.application.game.GameSessionContext;
import com.tl.models.application.game.states.LobbyState;
import com.tl.models.client.requests.CreateSessionRequest;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.HashMap;
import java.util.Map;
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
        context.setPublicSession(request.isPublicSession());
        context.setPassword(request.getPassword());

        var newUser = context.getState().addUserToSession(context, request.getUserName());
        context.setOwner(newUser);

        this.sessions.put(context.getSessionId(), context);

        return context;
    }
}
