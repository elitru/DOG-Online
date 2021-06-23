package com.tl.models.application.game.cards.payloads;

public class JokerPayload {
    // Which card is the joker supposed to represent? (1_11...)
    public String cardType;
    // Additional data required if the card is actually played (Which pins to swap...?)
    public String cardPayload;
}
