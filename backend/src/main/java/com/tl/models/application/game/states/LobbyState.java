package com.tl.models.application.game.states;

import com.tl.models.application.game.GameSessionContext;
import com.tl.models.application.user.SessionUser;

import javax.ws.rs.BadRequestException;
import java.util.UUID;

public class LobbyState extends GameState {

    /**
     * This method tries to add a new user to the current session.
     * @param context The context of the current session
     * @param userName The new username
     * @return The ID of the newly created user
     */
    @Override
    public SessionUser addUserToSession(GameSessionContext context, String userName) {

        // check if the name is already present
        if (this.getSessionUserByUserName(context, userName).isPresent()) {
            throw new BadRequestException("A user with the given user name already exists.");
        }

        // create new user + insert it
        var newUser = new SessionUser(context, userName);
        context.getClients().put(newUser.getId(), newUser);

        return newUser;
    }

}
