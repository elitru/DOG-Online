package com.tl.beans.user;

import com.tl.beans.game.GameSession;

import lombok.*;

/**
 * Representing a user who is connected to a given session
 * 
 * @author Elias Trummer, Martin Linhard
 */
@ToString
@Data
public class SessionUser extends User {
    private GameSession session;

    public SessionUser(GameSession session, String username) {
        super(username);
        this.session = session;
    }

    @Override
    public boolean equals(Object anotherObject) {
        return super.equals(anotherObject);
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }
}
