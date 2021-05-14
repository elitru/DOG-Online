package com.tl.models.client.responses;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.tl.models.client.responses.adapters.OptionalSerializer;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.util.Optional;
import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class FieldResponse {
    // Base Field
    @JsonProperty
    public UUID nodeId;
    @JsonProperty
    @JsonSerialize(using = OptionalSerializer.class)
    public Optional<UUID> previousId;
    @JsonProperty
    @JsonSerialize(using = OptionalSerializer.class)
    public Optional<UUID> nextId;

    // Start Field
    @JsonProperty
    @JsonSerialize(using = OptionalSerializer.class)
    public Optional<UUID> firstHomeFieldId;
    @JsonProperty
    @JsonSerialize(using = OptionalSerializer.class)
    public Optional<UUID> firstTargetFieldId;
}
