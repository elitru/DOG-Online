package com.tl.models.application.game.states;

import com.tl.models.application.game.GameSessionContext;
import com.tl.models.application.user.SessionUser;
import org.jboss.resteasy.spi.NotImplementedYetException;

import javax.ws.rs.BadRequestException;
import javax.ws.rs.core.NewCookie;
import java.util.Objects;
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

    /**
     * This method tries to add a new user to the current session; provided that the password matches
     * @param context The context of the current session
     * @param userName The new username
     * @param password The password to check
     * @return The ID of the newly created user
     */
    @Override
    public SessionUser addUserToSession(GameSessionContext context, String userName, String password) {
        if (!Objects.equals(password, context.getPassword())) {
            throw new BadRequestException();
        }
        return this.addUserToSession(context, userName);
    }

    /**
     * This method removes a given user from the current session.
     * @param context The context of the current session
     * @param userId The user to remove
     */
    @Override
    public void removeUserFromSession(GameSessionContext context, UUID userId) {
        context.getClients().remove(userId);

        if(userId.equals(context.getOwner().getId())) {
            // select new owner
            if(context.getClients().size() == 0) {
                return;
            }
            context.setOwner(context.getClients().get(0));
            // TODO: send message to web sockets
        }
    }
}
