package com.tl.beans.game;

import java.util.Set;
import java.util.UUID;

import com.tl.beans.user.SessionUser;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@AllArgsConstructor
@NoArgsConstructor
@ToString
@Data
public class GameSession {
    private String sessionName;
    private UUID sessionId;
    private String password;
    private boolean publicSession;
    private Game game;
    private Set<SessionUser> clients;
    private SessionUser owner;

    /**
     * Adds a new user (client) to the game session
     * 
     * @param user the session user to be added to the game
     */
    public void addClient(SessionUser user) {
        this.clients.add(user);
    }

    /**
     * Removes a user from the current game session also deleting his entire game
     * state
     * 
     * @param userId id of the user to be removed from the session and the according
     *               game
     */
    public void removeClient(UUID userId) {
        this.clients.removeIf(u -> u.getId().equals(userId));

        // if owner leaves -> select new owner of the session
        if (userId.equals(this.owner.getId())) {
            // try to select first user of set as newowner
            this.clients.stream().findFirst().ifPresentOrElse(
                    // there is a user left --> set owner
                    this::setOwner, () -> {
                        // no user left --> close session
                        this.closeSession();
                    });

        }
    }

    public void startGame() {

    }

    public void closeSession() {

    }
}
