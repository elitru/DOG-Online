package com.tl.models.client.requests;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.tl.validation.Required;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.Optional;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class CreateSessionRequest {
    @JsonProperty
    @Required
    public String userName;
    @JsonProperty
    @Required
    public String sessionName;
    @JsonProperty
    public String password;
    @JsonProperty
    @Required
    public Boolean publicSession;
}

