package com.tl.models.application.game.states;

public enum GameStateIdentifier {
    TeamAssignment("teamAssignment"),
    Lobby("lobby");

    private String stringRepresentation;

    private GameStateIdentifier(String stringRepresentation) {
        this.stringRepresentation = stringRepresentation;
    }

    public String toString() {
        return stringRepresentation;
    }
}
