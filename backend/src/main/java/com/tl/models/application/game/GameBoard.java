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
    private static final int NODES_BETWEEN = 13;
    public static final int RING_NODES = (NODES_BETWEEN + 1) * 4;
    public static final int[] START_FIELDS = {1 + (RING_NODES * 0), 1 + (RING_NODES * 1), 1 + (RING_NODES * 2), 1 + (RING_NODES * 3)};
    private Map<Integer, Team> teams;
    private BaseField reference;

    public GameBoard(GameSessionContext ctx) {
        this.teams = ctx.getGame().getTeams();
        this.initField(ctx);
    }

    public BaseField getCircleFieldById(int id, BaseField currentRef) {
        if (currentRef.getNodeId() == id) {
            return currentRef;
        }
        return this.getCircleFieldById(id, currentRef.getNext().get());
    }

    public <T extends BaseField> T getCircleFieldById(int id) {
        return ((T) this.getCircleFieldById(id, this.reference));
    }

    private void initField(GameSessionContext ctx) {
        StartField startField = null;
        BaseField prev = null;
        BaseField lastField = null;

        int counterBase = 1;
        int counterHome = -1;
        int counterTarget = -101;

        for (int t = 0; t < 4; t++) {
            var player = ctx.getGame().getUserForIndex(t);

            startField = new StartField(Optional.empty(), Optional.empty(), counterBase++);

            ctx.getGame().getStartFields().put(player, startField);

            addFields(startField, 4, HomeField.class, startField::setFirstHomeField, counterHome);
            counterHome -= 4;
            addFields(startField, 4, TargetField.class, startField::setFirstTargetField, counterTarget);
            counterTarget -= 4;

            if (prev == null) {
                this.reference = startField;
            }

            prev = startField;

            for (int i2 = 0; i2 < NODES_BETWEEN; i2++) {
                var newNode = new BaseField(counterBase++);
                newNode.setPrevious(prev);
                prev.setNext(newNode);
                prev = newNode;
                lastField = newNode;
            }
        }


        // make sure we retain an handle to one field
        this.reference.setPrevious(lastField);
        lastField.setNext(this.reference);
    }

    private static <T extends BaseField> void addFields(StartField start, int amount, Class<T> targetFieldType, FieldSetter<T> setFirst, int currentId) {
        BaseField prev = start;

        for (int i = 0; i < amount; i++) {
            T newNode = null;
            try {
                newNode = targetFieldType.getDeclaredConstructor(int.class).newInstance(currentId--);
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

    private Optional<UUID> getUserForStartField(Map<SessionUser, StartField> startFields, int startFieldId) {
        return startFields.entrySet().stream().filter(item -> item.getValue().getNodeId() == startFieldId).map(item -> item.getKey().getId()).findFirst();
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
