package com.tl.models.application.game;

import com.tl.models.application.game.cards.BaseCard;
import com.tl.models.application.game.cards.JokerCard;
import com.tl.models.application.game.cards.StartCard;
import com.tl.models.application.game.field.BaseField;
import com.tl.models.application.game.field.HomeField;
import com.tl.models.application.game.field.StartField;
import com.tl.models.application.game.sub_states.IngameSubState;
import com.tl.models.application.user.SessionUser;
import com.tl.models.client.requests.PlayCardRequest;
import lombok.*;

import javax.ws.rs.BadRequestException;
import java.util.*;

@AllArgsConstructor
@NoArgsConstructor
@ToString
@Data
@EqualsAndHashCode
public class Game {
    private Map<SessionUser, List<NinePin>> ninepins = new HashMap<>();
    private Map<SessionUser, StartField> startFields = new HashMap<>();
    private Map<Integer, Team> teams = new HashMap<>();
    private Map<SessionUser, List<BaseCard>> cards = new HashMap<>();
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

    private int getRealIndex(int i) {
        return i <= GameBoard.RING_NODES ? i : i - GameBoard.RING_NODES;
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
                return this.calculateAllMovesForSimpleCard(Integer.parseInt(card.getStringRepresentation()), currentField, player);
            default:
                throw new BadRequestException("Encountered invalid card");
        }
    }

    @SneakyThrows
    public void playCard(GameSessionContext context, PlayCardRequest request, SessionUser user) {
        var card = this.stack.getAllCards().get(request.getCardId());
        card.makeMove(context, request.getPayload(), request.getPinId(), user);
        // remove card from stack
        this.stack.playCard(card.getCardId());
        // remove from cards hashmap
        this.cards.get(user).removeIf(c -> c.getCardId().equals(card.getCardId()));
        // announce next player
        var p = this.getNextUser(user);
        this.getState().announcePlayerIsToPlay(p);
    }

    private SessionUser getNextUser(SessionUser s) {
        List<SessionUser> all = new ArrayList<>();

        all.add(this.teams.get(1).getMembers().get(0));
        all.add(this.teams.get(2).getMembers().get(0));
        all.add(this.teams.get(1).getMembers().get(1));
        all.add(this.teams.get(2).getMembers().get(1));

        int index = all.indexOf(s);
        int nextIndex = (index + 1) % 4;

        return all.get(nextIndex);
    }


    public Map<UUID, List<UUID>> getCardMovesForUser(SessionUser user) {
        var cards = this.cards.get(user);
        Map<UUID, List<UUID>> result = new HashMap<>();
        for (BaseCard c : cards) {
            for (NinePin p : this.ninepins.get(user)) {
                var res = this.isMovementPossible(c, p, user);
                if (res) {
                    var prev = result.getOrDefault(c.getCardId(), new ArrayList<>());
                    prev.add(p.getPinId());
                    result.put(c.getCardId(), prev);
                }
            }
        }
        return result;
    }

    private boolean isMovementPossible(BaseCard c, NinePin p, SessionUser user) {
        return c.isMovePossible(p, this, user);
    }

    public long amountOfPinsAtHome(SessionUser user) {
        return this.ninepins.get(user).stream()
                .filter(np -> np.getCurrentLocation().getNodeId() < 0 && np.getCurrentLocation().getNodeId() >= -16)
                .count();
    }

    public long amountOfPinsIngame(SessionUser user) {
        return this.ninepins.get(user).stream()
                .filter(np -> np.getCurrentLocation().getNodeId() > 0)
                .count();
    }

    private <T> boolean isTypePresent(SessionUser user, Class<T> search) {
        for (BaseCard b : this.cards.get(user)) {
            if (search.isInstance(b)) {
                return true;
            }
        }
        return false;
    }

    public List<Integer> getAllStraightWalkPositions(int amount, BaseField currentField, SessionUser currentPlayer) {

        // make sure we're not on a home field
        if (currentField instanceof HomeField) {
            return new ArrayList<>();
        }

        // Calculate all nodes on the path --> start with the first field after the current field
        List<Integer> path = new ArrayList<>();

        if (amount < 0) {
            BaseField currentNode = currentField;
            for (int i = 0; i < Math.abs(amount); i++) {
                currentNode = currentNode.getPrevious().get();
                path.add(currentNode.getNodeId());
            }
            var start = path.stream().filter(integer -> Arrays.stream(GameBoard.START_FIELDS).anyMatch(integer::equals)).findFirst();

            BaseField finalCurrentNode = currentNode;

            return start.map(fieldId -> {
                List<Integer> all = new ArrayList<>();
                StartField field = this.board.getCircleFieldById(fieldId);

                // start field is not occupied by pin of the same color --> add the end field
                if (!this.isStartFieldOccupiedByPlayerOfSameColor(field)) {
                    all.add(finalCurrentNode.getNodeId());
                }
                return all;
            }).orElse(new ArrayList<>() {{
                add(finalCurrentNode.getNodeId());
            }});
        }

        for (int i = currentField.getNodeId() + 1; i <= currentField.getNodeId() + amount; i++) {
            int index = this.getRealIndex(i);
            path.add(index);
        }

        // first, check if we're traversing a start field
        var start = path.stream().filter(integer -> Arrays.stream(GameBoard.START_FIELDS).anyMatch(integer::equals)).findFirst();

        return start.map(fieldId -> {
            List<Integer> all = new ArrayList<>();
            StartField field = this.board.getCircleFieldById(fieldId);

            // start field is not occupied by pin of the same color --> add the end field
            if (!this.isStartFieldOccupiedByPlayerOfSameColor(field)) {
                all.add(currentField.getNodeId() + amount);
            }

            // now: check if the player would be able to move his pin towards the goal
            // first: check if the startfield is actually owned by the current player
            OUTER:
            if (this.startFields.get(currentPlayer).getNodeId() == fieldId) {
                // we know that we're not *currently* standing on a start field as the current field is not included
                // in the path list (and therefore not in the search results)
                // get the index of the start field within the path list to calculate the remaining fields after it
                int indexWithinPath = path.indexOf(fieldId);
                int remainingFields = path.size() - (indexWithinPath + 1);

                // make sure that we don't have more points than necessary
                if (remainingFields > 4) {
                    break OUTER;
                }

                // check whether there are enough free fields left; stop at the first sight of a occupied field
                var curr = field.getFirstTargetField();
                var free = true;

                for (int i = 0; i < remainingFields; i++) {
                    if (this.isFieldOccupiedByPin(curr)) {
                        free = false;
                        break;
                    }
                }

                // check if not occupied
                if (free) {
                    // not occupied --> add target field as well
                    all.add(field.getFirstTargetField().getNodeId() - remainingFields);
                }
            }
            return all;
        }).orElse(new ArrayList<>() {{
            add(getRealIndex(currentField.getNodeId() + amount));
        }});

    }

    public boolean isStartFieldOccupiedByPlayerOfSameColor(BaseField startField) {
        for (Map.Entry<SessionUser, List<NinePin>> pin : this.ninepins.entrySet()) {
            if (pin.getValue().stream().map(NinePin::getCurrentLocation).anyMatch(b -> b.equals(startField))) {
                // found start field within the list of ninepins
                // now: check if the ninepin is owned by the same user as the current startfield
                return this.startFields.get(pin.getKey()).equals(startField);
            }
        }
        return false;
    }

    private boolean isFieldOccupiedByPin(BaseField field) {
        for (Map.Entry<SessionUser, List<NinePin>> pin : this.ninepins.entrySet()) {
            for (NinePin p : pin.getValue()) {
                // if the location of the pin is the field you search for
                if (p.getCurrentLocation().equals(field)) {
                    return true;
                }
            }
        }
        return false;
    }

    private List<Integer> calculateAllMovesForSimpleCard(int cardValue, BaseField currentField, SessionUser player) {
        return this.getAllStraightWalkPositions(cardValue, currentField, player);
    }
}
