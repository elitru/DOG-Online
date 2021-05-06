package com.tl.models.client.responses;

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
public class JoinSessionResponse {
    @JsonProperty
    public String url;
    @JsonProperty
    public UUID sessionId;
    @JsonProperty
    public UUID userId;
}
