package com.tl.models.application.game;

import com.tl.models.application.game.cards.*;
import lombok.Data;

import java.util.*;
@Data
public class CardStack {

    public static final int MAX_BUFFER_CAP = 20;

    //private static int[] normalCards = {2, 3, 5, 6, 8, 9, 10, 12};
    private static int[] normalCards = {10, 3, 10, 6, 8, 9, 10, 12};

    HashMap<UUID, BaseCard> allCards = new HashMap<>();


    private Queue<BaseCard> cardStack = new ArrayDeque<>();
    private List<BaseCard> shuffleBuffer = new ArrayList<>();

    public CardStack() {
        this.initStack();
    }

    private void initStack() {
        List<BaseCard> cardStack = new ArrayList<>();

        // add all the normal cards
        for (int number : normalCards) {
            this.addCardNTimes(8, cardStack, () -> new SimpleCard(number));
        }

        // add 6 jokers
        this.addCardNTimes(6, cardStack, JokerCard::new);
        // add the cards for swapping players
        this.addCardNTimes(8, cardStack, SwapCard::new);
        // add the two types of start cards
        this.addCardNTimes(8, cardStack, () -> new StartCard(StartCardType.Eleven));
        this.addCardNTimes(8, cardStack, () -> new StartCard(StartCardType.Thirteen));
        // add the magic 7
        //this.addCardNTimes(8, cardStack, VariableCard::new);
        // add the +- 4
        this.addCardNTimes(8, cardStack, BidirectionalCard::new);

        // shuffle intermediary list & add it to stack
        Collections.shuffle(cardStack);
        this.cardStack.addAll(cardStack);
    }

    private void addCardNTimes(int times, List<BaseCard> cardStack, CardGenerator gen) {
        for (int i = 0; i < times; i++) {
            var card = gen.generateCard();

            cardStack.add(card);
            this.allCards.put(card.getCardId(), card);
        }
    }

    public BaseCard drawCard() {
        return this.cardStack.poll();
    }

    public List<BaseCard> drawNCards(int amount) {
        List<BaseCard> list = new ArrayList<>();
        for (int i = 0; i < amount; i++) {
            list.add(this.cardStack.poll());
        }
        return list;
    }

    public void playCard(UUID cardId) {
        var card = this.allCards.get(cardId);
        if (card != null) {
            this.addCardToShuffle(card);
        }
    }

    private void addCardToShuffle(BaseCard card) {
        this.shuffleBuffer.add(card);
        if (this.shuffleBuffer.size() == MAX_BUFFER_CAP) {
            // change order of cards
            Collections.shuffle(this.shuffleBuffer);
            // put them back on the stack
            this.cardStack.addAll(this.shuffleBuffer);
            // clear the buffer
            this.shuffleBuffer.clear();
        }
    }
}
