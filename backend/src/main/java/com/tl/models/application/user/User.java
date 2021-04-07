package com.tl.models.application.user;

import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.ToString;
import lombok.EqualsAndHashCode;

/**
 * Beans class holding the basic data of a user
 * 
 * @author Elias Trummer
 */
@AllArgsConstructor
@ToString
@Data
@EqualsAndHashCode
public class User {
    private final UUID id;
    @EqualsAndHashCode.Exclude
    private String username;

    public User(String username) {
        this.id = UUID.randomUUID();
        this.username = username;
    }

}