package com.tl.models.application.user;

import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

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
    @JsonProperty
    private final UUID id;
    @EqualsAndHashCode.Exclude
    @JsonProperty
    private String username;

    public User() {
        this.id = null;
    }

    public User(String username) {
        this.id = UUID.randomUUID();
        this.username = username;
    }

}