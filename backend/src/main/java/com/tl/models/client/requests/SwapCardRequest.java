package com.tl.models.client.requests;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.tl.validation.Required;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class SwapCardRequest {
    @Required
    @JsonProperty
    public UUID toPlayer;
    @Required
    @JsonProperty
    public UUID cardId;
}
