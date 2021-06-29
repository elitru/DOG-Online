package com.tl.models.application.game;

import com.tl.models.application.game.cards.BaseCard;
import com.tl.models.application.game.cards.StartCardType;
import com.tl.models.application.game.cards.payloads.JokerPayload;
import com.tl.models.application.game.field.BaseField;
import com.tl.models.application.game.field.HomeField;
import com.tl.models.application.game.field.StartField;
import com.tl.models.application.game.sub_states.DealCardsSubState;
import com.tl.models.application.game.sub_states.IngameSubState;
import com.tl.models.application.game.sub_states.SwapCardsSubState;
import com.tl.models.application.game.ws_messages.messages.AnnounceWinnerMessage;
import com.tl.models.application.user.SessionUser;
import com.tl.models.client.requests.DropCardRequest;
import com.tl.models.client.requests.PlayCardRequest;
import com.tl.resources.GameSocketResource;
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

    public NinePin getNinePinById(UUID pinId) {
        return this.ninepins.values().stream().flatMap(List::stream).filter(p -> p.getPinId().equals(pinId)).findFirst().get();
    }

    public BaseField getFieldForFieldId(int fieldId, SessionUser user) {
        BaseField start;
        if (NinePin.isOnHomeField(fieldId)) {
            start = this.startFields.get(user).getFirstHomeField();
        } else if (NinePin.isOnTargetField(fieldId)) {
            start = this.startFields.get(user).getFirstTargetField();
        } else {
            start = this.board.getReference();
        }

        return this.board.getCircleFieldById(fieldId, start);
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

    public int getRealIndex(int i) {
        return i <= GameBoard.RING_NODES ? i : i - GameBoard.RING_NODES;
    }

    public List<Integer> calculateAllMoves(UUID pinId, UUID cardId, JokerPayload payload, SessionUser player) {
        var card = this.stack.getAllCards().get(cardId);
        return card.getPossibleMoves(this.getPinForPinId(player, pinId), this, player, payload);
    }

    @SneakyThrows
    public void playCard(GameSessionContext context, PlayCardRequest request, SessionUser user) {
        var card = this.stack.getAllCards().get(request.getCardId());
        System.out.println("now playing card " + card.getStringRepresentation() + " with payload " + request.getPayload());
        var movedTo = card.makeMove(context, request.getPayload(), request.getPinId(), user);
        // check to see if there is _another_ pin with the same location
        var maybeThrow = this.getPinForLocation(movedTo, request.getPinId());

        maybeThrow.ifPresent(p -> {
            // send message that this pin needs to be removed
            var removedUser = this.getUserForPin(p);
            var previousLocation = p.getCurrentLocation().getNodeId();

            var loc = this.getNextFreeHomeFieldForUser(removedUser);
            p.setCurrentLocation(loc);

            p.broadcastMovement(context, previousLocation);
        });

        // remove card
        this.removeCardForPlayer(user, card.getCardId());

        // check for winner, else announce new player / swap
        var winner = this.hasAnyTeamWon();
        if (winner.isPresent()) {
            // send out win message
            GameSocketResource.makeGameBroadcast(context, new AnnounceWinnerMessage(winner.get()));
        } else {
            // announce next player or swap
            var p = this.getNextUser(user);
            this.announceNextOrSwapState(context, p);
        }
    }

    private BaseField getNextFreeHomeFieldForUser(SessionUser user) {
        BaseField fh = this.startFields.get(user).getFirstHomeField();
        do {
            if (!this.isFieldOccupiedByPin(fh)) {
                return fh;
            }
            fh = fh.getNext().get();
        } while (true);
    }

    private SessionUser getUserForPin(NinePin p) {
        return this.ninepins.entrySet()
                .stream()
                .filter(e -> e.getValue().contains(p))
                .map(Map.Entry::getKey).findFirst()
                .get();
    }

    private Optional<NinePin> getPinForLocation(int location, UUID skipId) {
        return this.ninepins.values().stream().flatMap(List::stream).filter(np -> !np.getPinId().equals(skipId) && np.getCurrentLocation().getNodeId() == location).findFirst();
    }

    public void dropCard(GameSessionContext context, DropCardRequest request, SessionUser user) {
        var card = this.stack.getAllCards().get(request.getCardId());
        // remove the card for the player without moving any pins
        this.removeCardForPlayer(user, card.getCardId());
        // announce next player or swap
        var p = this.getNextUser(user);
        this.announceNextOrSwapState(context, p);
    }

    private void removeCardForPlayer(SessionUser user, UUID cardId) {
        // remove card from stack
        this.stack.playCard(cardId);
        // remove from cards hashmap
        this.cards.get(user).removeIf(c -> c.getCardId().equals(cardId));
    }

    private void announceNextOrSwapState(GameSessionContext context, SessionUser p) {
        if (!this.getState().registerCardPlayed()) {
            // there are still cards to be played --> just announce the next player
            this.getState().announcePlayerIsToPlay(p);
        } else {
            System.out.println("Starting new round");
            // no cards left --> switch to new state (deal the cards again)
            var newAmount = this.getState().getAmountOfCardsPerRound() == 1 ? DealCardsSubState.START_WITH : this.getState().getAmountOfCardsPerRound() - 1;
            System.out.println("New amount: " + newAmount);
            // set new state
            this.setState(new DealCardsSubState(context, newAmount));
            // then --> swap cards
            this.setState(new SwapCardsSubState(context, p, newAmount));
        }
    }

    private SessionUser getNextUser(SessionUser s) {
        List<SessionUser> all = new ArrayList<>();

        // add all players
        all.add(this.teams.get(1).getMembers().get(0));
        all.add(this.teams.get(2).getMembers().get(0));
        all.add(this.teams.get(1).getMembers().get(1));
        all.add(this.teams.get(2).getMembers().get(1));

        // remove the players who've got all the pins in their target fields
        all.removeIf(this::isUserFinished);

        // get the next player
        int index = all.indexOf(s);
        int nextIndex = (index + 1) % all.size();

        return all.get(nextIndex);
    }

    private boolean isUserFinished(SessionUser user) {
        return this.ninepins.get(user).stream().allMatch(NinePin::isOnTargetField);
    }

    private Optional<Integer> hasAnyTeamWon() {
        for (Team t : this.teams.values()) {
            var m = t.getMembers();
            if (m.stream().allMatch(this::isUserFinished) && t.getTeamId() != 0) {
                return Optional.of(t.getTeamId());
            }
        }
        return Optional.empty();
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
        return !c.getPossibleMoves(p, this, user, null).isEmpty();
    }

    public long amountOfPinsIngame(SessionUser user) {
        return this.ninepins.get(user).stream()
                .filter(np -> np.getCurrentLocation().getNodeId() > 0)
                .count();
    }

    public long amountOfPinsIngame() {
        return this.ninepins.values().stream()
                .flatMap(List::stream)
                .filter(np -> np.getCurrentLocation().getNodeId() > 0)
                .count();
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

        var onTargetField = currentField.getNodeId() < 0;

        if (onTargetField) {
            List<Integer> all = new ArrayList<>();
            var free = true;
            for (int i = 0; i < amount; i++) {
                if (this.isFieldOccupiedByPin(currentField)) {
                    free = false;
                    break;
                }

                if (currentField.getNext().isPresent()) {
                    currentField = currentField.getNext().get();
                }
            }

            // check if not occupied
            if (free) {
                // not occupied --> add target field as well
                all.add(this.getRealIndex(currentField.getNodeId() - amount + 1));
            }

            return all;
        } else {
            for (int i = this.getRealIndex(currentField.getNodeId() + 1); i <= currentField.getNodeId() + amount; i++) {
                int index = this.getRealIndex(i);
                path.add(index);
            }
        }


        System.out.println("Walking on path: " + Arrays.toString(path.toArray()));

        // first, check if we're traversing a start field
        var start = path.stream().filter(integer -> Arrays.stream(GameBoard.START_FIELDS).anyMatch(integer::equals)).findFirst();

        BaseField finalCurrentField = currentField;
        BaseField finalCurrentField1 = currentField;
        return start.map(fieldId -> {
            System.out.println("Found start field with id of  " + fieldId);

            List<Integer> all = new ArrayList<>();
            StartField field = this.board.getCircleFieldById(fieldId);

            // start field is not occupied by pin of the same color --> add the end field
            if (!this.isStartFieldOccupiedByPlayerOfSameColor(field)) {
                System.out.println("Transgressing start field because it is not occupied!");
                all.add(this.getRealIndex(finalCurrentField.getNodeId() + amount));
            }

            // now: check if the player would be able to move his pin towards the goal
            // first: check if the startfield is actually owned by the current player
            OUTER:
            if (this.startFields.get(currentPlayer).getNodeId() == fieldId) {
                // we know that we're not *currently* standing on a start field as the current field is not included
                // in the path list (and therefore not in the search results)
                // get the index of the start field within the path list to calculate the remaining fields after it
                int indexWithinPath = path.indexOf(fieldId);
                int remainingFields = path.size() - indexWithinPath - 1;
                System.out.println("Found start field! Remaining: " + remainingFields);

                // make sure that we don't have more points than necessary
                if (remainingFields > 4 || remainingFields == 0) {
                    break OUTER;
                }

                // check whether there are enough free fields left; stop at the first sight of a occupied field
                BaseField curr = field.getFirstTargetField();
                var free = true;

                for (int i = 0; i < remainingFields; i++) {
                    if (this.isFieldOccupiedByPin(curr)) {
                        free = false;
                        break;
                    }

                    if (curr.getNext().isPresent()) {
                        curr = curr.getNext().get();
                    }
                }

                System.out.println("Is the target free? " + free);

                // check if not occupied
                if (free) {
                    System.out.println("Target is free! Adding " + this.getRealIndex(field.getFirstTargetField().getNodeId() - remainingFields + 1));
                    // not occupied --> add target field as well
                    all.add(this.getRealIndex(field.getFirstTargetField().getNodeId() - remainingFields + 1));
                }
            }
            System.out.println("Found possibilities: "  + Arrays.toString(all.toArray()));
            return all;
        }).orElse(new ArrayList<>() {{
            add(getRealIndex(finalCurrentField1.getNodeId() + amount));
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
                if (p.getCurrentLocation().getNodeId() == field.getNodeId()) {
                    return true;
                }
            }
        }
        return false;
    }
}
