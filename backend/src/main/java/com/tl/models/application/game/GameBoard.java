package com.tl.models.application.game;

import com.tl.models.application.game.field.*;
import com.tl.models.application.game.field.BaseField;
import com.tl.models.application.user.SessionUser;
import com.tl.models.client.responses.FieldResponse;
import com.tl.models.client.responses.adapters.OptionalSerializer;
import lombok.Data;

import java.util.*;

@Data
public class GameBoard {
    private static final int NODES_BETWEEN = 15;
    private Map<Integer, Team> teams;
    private BaseField reference;

    public GameBoard(GameSessionContext ctx) {
        this.teams = ctx.getGame().getTeams();
        System.out.println(this.teams == null);
        this.initField(ctx);
    }

    private void initField(GameSessionContext ctx) {
        StartField startField = null;
        BaseField prev = null;
        BaseField lastField = null;

        var players = getTotalPlayers();

        // check whether there's 1 or 2 players per team
        if (players % 2 != 0) {
            for (int t = 0; t < teams.size(); t++) {
                var player = teams.get(t).getMembers().get(0);
                startField = new StartField(Optional.empty(), Optional.empty(), UUID.randomUUID());

                ctx.getGame().getStartFields().put(player, startField);

                addFields(startField, 4, HomeField.class, startField::setFirstHomeField);
                addFields(startField, 4, TargetField.class, startField::setFirstTargetField);

                if (prev == null) {
                    this.reference = startField;
                }

                prev = startField;

                for (int i2 = 0; i2 < NODES_BETWEEN; i2++) {
                    var newNode = new BaseField();
                    newNode.setPrevious(prev);
                    prev.setNext(newNode);
                    prev = newNode;
                    lastField = newNode;
                }
            }
        } else {
            for (int t = 0; t < teams.size() * 2; t++) {
                int team = t / teams.size();
                int member = t % teams.size();

                var player = teams.get(team).getMembers().get(member);
                startField = new StartField(Optional.empty(), Optional.empty(), UUID.randomUUID());

                ctx.getGame().getStartFields().put(player, startField);

                addFields(startField, 4, HomeField.class, startField::setFirstHomeField);
                addFields(startField, 4, TargetField.class, startField::setFirstTargetField);

                if (prev == null) {
                    this.reference = startField;
                }

                prev = startField;

                for (int i2 = 0; i2 < NODES_BETWEEN; i2++) {
                    var newNode = new BaseField();
                    newNode.setPrevious(prev);
                    prev.setNext(newNode);
                    prev = newNode;
                    lastField = newNode;
                }
            }
        }


        // make sure we retain an handle to one field
        this.reference.setPrevious(lastField);
        lastField.setNext(this.reference);
    }

    private static <T extends BaseField> void addFields(StartField start, int amount, Class<T> targetFieldType, FieldSetter<T> setFirst) {
        BaseField prev = start;

        for (int i = 0; i < amount; i++) {
            T newNode = null;
            try {
                newNode = targetFieldType.newInstance();
            } catch (Exception e) {
                e.printStackTrace();
            }

            if (prev instanceof StartField) {
                // first field
                setFirst.set(newNode);
            } else {
                prev.setNext(newNode);
            }

            newNode.setPrevious(prev);
            prev = newNode;
        }
    }

    private int getTotalPlayers() {
        return this.teams.values().stream().mapToInt(Team::getAmountOfMembers).sum();
    }

    public List<FieldResponse> toResponseList() {
        var result = new ArrayList<FieldResponse>();
        var next = this.reference;

        do {
            var nodeId = next.getNodeId();
            var prevId = next.getPrevious().map(BaseField::getNodeId);
            var nextId = next.getNext().map(BaseField::getNodeId);

            var isStartField = next instanceof StartField;

            if (isStartField) {
                var homeFieldId = Optional.of(((StartField) next).getFirstHomeField().getNodeId());
                var targetFieldId = Optional.of(((StartField) next).getFirstTargetField().getNodeId());

                result.add(new FieldResponse(nodeId, prevId, nextId, homeFieldId, targetFieldId));
                result.addAll(getAttachedFields(((StartField) next).getFirstHomeField()));
                result.addAll(getAttachedFields(((StartField) next).getFirstTargetField()));
            } else {
                result.add(new FieldResponse(nodeId, prevId, nextId, Optional.empty(), Optional.empty()));
            }

            next = next.getNext().get();
        } while (next.hasNext() && !next.equals(this.reference));

        return result;
    }

    private static List<FieldResponse> getAttachedFields(BaseField field) {
        var result = new ArrayList<FieldResponse>();
        var next = field;

        while (next != null) {
            var nodeId = next.getNodeId();
            var prevId = next.getPrevious().map(BaseField::getNodeId);
            var nextId = next.getNext().map(BaseField::getNodeId);

            result.add(new FieldResponse(nodeId, prevId, nextId, Optional.empty(), Optional.empty()));
            next = next.getNext().orElseGet(() -> null);
        }

        return result;
    }
}
