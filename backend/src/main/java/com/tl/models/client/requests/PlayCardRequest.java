package com.tl.models.client.requests;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;
import com.tl.validation.Recursive;
import com.tl.validation.Required;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.UUID;

@Data
@NoArgsConstructor
@ToString
public class PlayCardRequest {
    @JsonProperty
    @Required
    UUID cardId;
    @JsonProperty
    @Required
    UUID pinId;
    @JsonProperty
    @Required
    @Recursive
    String payload;
}
