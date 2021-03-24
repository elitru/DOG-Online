package com.tl.models.client.requests;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class CreateSessionRequest {
    @JsonProperty(required = true)
    private String userName;
    @JsonProperty(required = true)
    private String sessionName;
    @JsonProperty(required = true)
    private String password;
    @JsonProperty(required = true)
    private boolean publicSession;
}

