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
public class JoinSessionRequest {
    @JsonProperty
    @Required
    public String userName;
    @JsonProperty
    @Required
    public UUID sessionId;
    @JsonProperty
    public String password;
}