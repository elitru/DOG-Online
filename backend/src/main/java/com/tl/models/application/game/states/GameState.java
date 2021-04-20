package com.tl.models.application.game.states;

import com.tl.models.application.game.GameSessionContext;
import com.tl.models.application.user.SessionUser;

import javax.ws.rs.BadRequestException;
import java.util.Optional;
import java.util.UUID;

public abstract class GameState {
    private static final String BAD_STATE = "This operation is not possible in the current game state.";

    protected GameSessionContext context;

    public GameState(GameSessionContext context) {
        this.context = context;
    }

    public SessionUser addUserToSession(String userName) {
        throw new BadRequestException(BAD_STATE);
    }

    public SessionUser addUserToSession(String userName, String password) {
        throw new BadRequestException(BAD_STATE);
    }

    public void removeUserFromSession(UUID userId) {
        throw new BadRequestException(BAD_STATE);
    }

    public void joinTeam(int target, SessionUser user) {
        throw new BadRequestException(BAD_STATE);
    }

    protected Optional<SessionUser> getSessionUserByUserName(GameSessionContext context, String userName) {
        return context.getClients().values().stream().filter(u -> u.getUsername().equals(userName)).findFirst();
    }

    public abstract void sendWSInitMessage();
}
