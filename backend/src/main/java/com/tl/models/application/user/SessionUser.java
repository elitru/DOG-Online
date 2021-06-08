package com.tl.models.application.user;

import com.tl.models.application.game.GameSessionContext;

import lombok.*;

import javax.websocket.Session;

/**
 * Representing a user who is connected to a given session
 * 
 * @author Elias Trummer, Martin Linhard
 */
@Data
@ToString
public class SessionUser extends User {
    @ToString.Exclude
    private Session websocketSession;

    public SessionUser(String username) {
        super(username);
    }

    @Override
    public boolean equals(Object anotherObject) {
        return super.equals(anotherObject);
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }

    public User toBaseUser() {
        return new User(this.getId(), this.getUsername());
    }
}
