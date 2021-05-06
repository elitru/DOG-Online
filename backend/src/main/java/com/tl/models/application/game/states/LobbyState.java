package com.tl.models.application.game.states;

import com.tl.models.application.game.GameSessionContext;
import com.tl.models.application.game.ws_messages.messages.StateChangedMessage;
import com.tl.models.application.game.ws_messages.messages.UserUpdateMessage;
import com.tl.models.application.user.SessionUser;
import com.tl.models.application.user.User;
import com.tl.resources.GameSocketResource;

import javax.ws.rs.BadRequestException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;

public class LobbyState extends GameState {

    public LobbyState(GameSessionContext context) {
        super(context);
    }

    /**
     * This method tries to add a new user to the current session.
     * @param userName The new username
     * @return The ID of the newly created user
     */
    @Override
    public SessionUser addUserToSession(String userName) {

        // check if the name is already present
        if (this.getSessionUserByUserName(context, userName).isPresent()) {
            throw new BadRequestException("A user with the given user name already exists.");
        }

        // create new user + insert it
        var newUser = new SessionUser(userName);
        context.getClients().put(newUser.getId(), newUser);

        return newUser;
    }

    /**
     * This method tries to add a new user to the current session; provided that the password matches
     * @param userName The new username
     * @param password The password to check
     * @return The ID of the newly created user
     */
    @Override
    public SessionUser addUserToSession(String userName, String password) {
        if (!Objects.equals(password, context.getPassword())) {
            throw new BadRequestException();
        }
        return this.addUserToSession(userName);
    }

    /**
     * This method removes a given user from the current session.
     * @param userId The user to remove
     */
    @Override
    public void removeUserFromSession(UUID userId) {
        context.getClients().remove(userId);

        if(userId.equals(context.getOwner().getId())) {
            // select new owner
            if(context.getClients().size() == 0) {
                return;
            }
            context.setOwner(context.getClients().values().stream().findFirst().get());
            // TODO: send message to web sockets
        }
    }

    @Override
    public void sendWSInitMessage() {
        var message = new StateChangedMessage<List<User>>(GameStateIdentifier.Lobby, this.context.getClients().values().stream().map(u -> (User) u).collect(Collectors.toList()));
        GameSocketResource.makeGameBroadcast(context, message);
    }
}
