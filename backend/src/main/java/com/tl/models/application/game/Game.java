package com.tl.models.application.game;

import com.tl.models.application.game.field.BaseField;
import com.tl.models.application.game.field.HomeField;
import com.tl.models.application.game.field.StartField;
import com.tl.models.application.game.sub_states.IngameSubState;
import com.tl.models.application.user.SessionUser;
import lombok.*;

import javax.swing.*;
import javax.ws.rs.BadRequestException;
import java.util.*;
import java.util.stream.Collectors;

@AllArgsConstructor
@NoArgsConstructor
@ToString
@Data
@EqualsAndHashCode
public class Game {
    private Map<SessionUser, List<NinePin>> ninepins = new HashMap<>();
    private Map<SessionUser, StartField> startFields = new HashMap<>();
    private Map<Integer, Team> teams = new HashMap<>();
    private CardStack stack;
    private GameBoard board;
    private IngameSubState state;

    public static final int MIN_PLAYERS = 3;
    public static final int MAX_PLAYERS = 8;

    public void setState(IngameSubState state) {
        this.state = state;
    }

    public Game(GameSessionContext ctx, Map<Integer, Team> teams) {
        this.teams = teams;
        ctx.setGame(this);
        this.stack = new CardStack();
    }

    public void initField(GameSessionContext ctx) {
        this.board = new GameBoard(ctx);
    }

    public Optional<Team> getTeamForUser(SessionUser user) {
        return this.teams.values().stream().filter(t -> t.getMembers().contains(user)).findFirst();
    }

    public void initNinePins() {
        var color = NinePinColor.Red;
        for (int i = 0; i < 4; i++) {
            var p = this.getUserForIndex(i);
            this.initNinePinsForUser(p, this.startFields.get(p).getFirstHomeField(), color);
            color = color.next();
        }
    }

    public SessionUser getUserForIndex(int index) {
        int player = 0, team = 0;
        switch (index) {
            case 0:
                player = 0;
                team = 1;
                break;
            case 1:
                player = 0;
                team = 2;
                break;
            case 2:
                team = 1;
                player = 1;
                break;
            case 3:
                team = 2;
                player = 1;
                break;
        }
        return this.teams.get(team).getMembers().get(player);
    }

    private void initNinePinsForUser(SessionUser user, HomeField field, NinePinColor color) {
        List<NinePin> pins = new ArrayList<>();
        BaseField next = field;

        while (next != null) {

            pins.add(new NinePin(next, color));

            next = next.getNext().orElse(null);
        }

        this.ninepins.put(user, pins);
    }

    private NinePin getPinForPinId(SessionUser player, UUID pinId) {
        return this.ninepins.get(player).stream().filter(p -> p.getPinId().equals(pinId)).findFirst().get();
    }

    public List<Integer> calculateAllMoves(UUID pinId, UUID cardId, Optional<String> jokerIdent, SessionUser player) {
        var currentField = this.getPinForPinId(player, pinId).getCurrentLocation();
        var card = this.stack.getAllCards().get(cardId);

        switch (card.getStringRepresentation()) {
            case "2":
            case "3":
            case "5":
            case "6":
            case "8":
            case "9":
            case "10":
            case "12":
                return this.calculateAllMovesForSimpleCard(Integer.parseInt(card.getStringRepresentation()), currentField);
            default:
                throw new BadRequestException("Encountered invalid card");
        }
    }

    private List<Integer> getAllStraightWalkPositions(int amount, BaseField currentField) {

        // make sure we're not on a home field
        if (currentField instanceof HomeField) {
            return new ArrayList<>();
        }

        return this.getAllStraightWalkPositions(amount, currentField, new ArrayList<>());
    }

    private boolean isStartFieldOccupiedByPlayerOfSameColor(BaseField startField) {
        for (Map.Entry<SessionUser, List<NinePin>> pin : this.ninepins.entrySet()) {
            if (pin.getValue().stream().map(NinePin::getCurrentLocation).anyMatch(b -> b.equals(startField))) {
                // found start field within the list of ninepins
                // now: check if the ninepin is owned by the same user as the current startfield
                return this.startFields.get(pin.getKey()).equals(startField);
            }
        }
        return false;
    }

    private List<Integer> getAllStraightWalkPositions(int amount, BaseField currentField, List<Integer> previous) {
        if (amount == 0 && !(currentField instanceof StartField)) {
            previous.add(currentField.getNodeId());
            return previous;
        } else if (amount == 0) {
            // amount = 0; currentField is instance of StartField
            // it isn't occupied by a ninepin of the same color --> mark as viable choice
            if (!isStartFieldOccupiedByPlayerOfSameColor(currentField)) {
                previous.add(currentField.getNodeId());
            }
            return previous;
        } else if (currentField instanceof StartField) {
            // TODO fix this case (check if it is your own startfield; if so check if theres space in goal)
            // currentField is instance of StartField, but there's still some fields to go
            return getAllStraightWalkPositions(amount - 1, currentField.getNext().get(), previous);
        }
        return previous;
    }

    private List<Integer> calculateAllMovesForSimpleCard(int cardValue, BaseField currentField) {
        return this.getAllStraightWalkPositions(cardValue, currentField);
    }
}
