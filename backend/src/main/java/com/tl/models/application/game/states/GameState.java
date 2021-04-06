package com.tl.models.application.game.states;

import com.tl.models.application.game.GameSessionContext;
import com.tl.models.application.user.SessionUser;

import javax.ws.rs.BadRequestException;
import java.util.Optional;
import java.util.UUID;

public abstract class GameState {
    private static final String BAD_STATE = "This operation is not possible in the current game state.";

    public SessionUser addUserToSession(GameSessionContext context, String userName) {
        throw new BadRequestException(BAD_STATE);
    }

    public SessionUser addUserToSession(GameSessionContext context, String userName, String password) {
        throw new BadRequestException(BAD_STATE);
    }

    public void removeUserFromSession(GameSessionContext context) {
        throw new BadRequestException(BAD_STATE);
    }

    protected Optional<SessionUser> getSessionUserByUserName(GameSessionContext context, String userName) {
        return context.getClients().values().stream().filter(u -> u.getUsername().equals(userName)).findFirst();
    }

}
