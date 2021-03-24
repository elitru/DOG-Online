package com.tl.models.application.user;

import com.tl.models.application.game.GameSessionContext;

import lombok.*;

/**
 * Representing a user who is connected to a given session
 * 
 * @author Elias Trummer, Martin Linhard
 */
@ToString
@Data
public class SessionUser extends User {
    private GameSessionContext session;

    public SessionUser(GameSessionContext session, String username) {
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
