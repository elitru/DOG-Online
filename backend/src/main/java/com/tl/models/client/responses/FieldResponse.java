package com.tl.models.client.responses;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.tl.models.client.responses.adapters.OptionalSerializer;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.Optional;
import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@ToString
public class FieldResponse {
    // Base Field
    @JsonProperty
    public int nodeId;
    @JsonProperty
    @JsonSerialize(using = OptionalSerializer.class)
    public Optional<Integer> previousId;
    @JsonProperty
    @JsonSerialize(using = OptionalSerializer.class)
    public Optional<Integer> nextId;

    // Start Field
    @JsonProperty
    @JsonSerialize(using = OptionalSerializer.class)
    public Optional<Integer> firstHomeFieldId;
    @JsonProperty
    @JsonSerialize(using = OptionalSerializer.class)
    public Optional<Integer> firstTargetFieldId;
    @JsonProperty
    @JsonSerialize(using = OptionalSerializer.class)
    public Optional<UUID> userId;
}
