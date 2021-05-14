package com.tl.models.application.game;

import com.tl.models.application.game.field.BaseField;
import com.tl.models.application.game.field.HomeField;
import com.tl.models.application.game.field.StartField;
import com.tl.models.application.game.field.TargetField;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public class GameField {
    private static final int NODES_BETWEEN = 15;
    private Map<Integer, Team> teams;
    private BaseField reference;

    public GameField(Map<Integer, Team> teams) {
        this.teams = teams;
        this.initField();
    }
    
    private void initField() {
        StartField startField = null;
        BaseField prev = null;

        var players = getTotalPlayers();

        for(int i = 0; i < players; i++) {
            startField = new StartField(Optional.empty(), Optional.empty(), UUID.randomUUID());
            addHomeFields(startField);
            addTargetFields(startField);

            if(prev != null) {
                startField.setPrevious(prev);
            }

            prev = startField;

            for(int i2 = 0; i2 < NODES_BETWEEN; i2++) {
                var newNode = new BaseField();
                newNode.setPrevious(prev);
                prev.setNext(newNode);
                prev = newNode;
            }
        }
        // make sure we retain an handle to one field
        this.reference = startField;
    }

    private static void addTargetFields(StartField field) {
        BaseField prev = field;
        for (int i = 0; i < 4; i++) {
            var newField = new TargetField();
            prev.setNext(newField);
            prev = newField;
        }
    }

    private static void addHomeFields(StartField field) {
        var prev = new HomeField();

        for (int i = 0; i < 3; i++) {
            var newNode = new HomeField();
            prev.setNext(newNode);
            prev = newNode;
        }

        prev.setNext(field);
    }

    private int getTotalPlayers() {
        return this.teams.values().stream().mapToInt(Team::getAmountOfMembers).sum();
    }
}
