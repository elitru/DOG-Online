package com.tl.models.client.responses;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class ResponseSession {
    @JsonProperty
    public UUID sessionId;
    @JsonProperty
    public String name;
    @JsonProperty
    public boolean hasPassword;
}
