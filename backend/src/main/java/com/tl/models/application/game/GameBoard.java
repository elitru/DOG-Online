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
    private static final int NODES_BETWEEN = 11;
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

        if (players % 2 != 0) {
            for (int t = 1; t <= 4; t++) {
                var optionalPlayer = Optional.ofNullable(teams.get(t)).map(team -> team.getMembers().get(0));
                startField = new StartField(Optional.empty(), Optional.empty(), UUID.randomUUID());

                StartField finalStartField = startField;
                optionalPlayer.ifPresent(player -> ctx.getGame().getStartFields().put(player, finalStartField));

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
            for (int t = 1; t <= 4; t++) {
                int team = t == 1 || t == 2 ? 1 : 2;
                int member = t == 1 || t == 3 ? 0 : 1;

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

    private Optional<UUID> getUserForStartField(Map<SessionUser, StartField> startFields, UUID startFieldId) {
        return startFields.entrySet().stream().filter(item -> item.getValue().getNodeId().equals(startFieldId)).map(item -> item.getKey().getId()).findFirst();
    }

    public List<FieldResponse> toResponseList(Map<SessionUser, StartField> startFields) {
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

                result.add(new FieldResponse(nodeId, prevId, nextId, homeFieldId, targetFieldId, Optional.empty()));
                result.addAll(getAttachedFields(((StartField) next).getFirstHomeField()));
                result.addAll(getAttachedFields(((StartField) next).getFirstTargetField()));
            } else {
                result.add(new FieldResponse(nodeId, prevId, nextId, Optional.empty(), Optional.empty(), Optional.empty()));
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

            result.add(new FieldResponse(nodeId, prevId, nextId, Optional.empty(), Optional.empty(), Optional.empty()));
            next = next.getNext().orElseGet(() -> null);
        }

        return result;
    }
}
