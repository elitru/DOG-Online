package com.tl.models.application.game.cards.payloads;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.UUID;

@ToString
@AllArgsConstructor
@NoArgsConstructor
@Data
public class SwapCardPayload {
    @JsonProperty
    public UUID otherPin;
}
