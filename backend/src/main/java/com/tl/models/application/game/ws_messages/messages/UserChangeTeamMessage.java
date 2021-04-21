package com.tl.models.application.game.ws_messages.messages;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tl.models.application.game.ws_messages.Message;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.SneakyThrows;

import java.util.UUID;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserChangeTeamMessage extends Message {

    @JsonProperty
    private UUID userId;
    @JsonProperty
    private int newTeam;
    @JsonProperty
    private int oldTeam;

    @SneakyThrows
    @Override
    public String serialize() {
        return new ObjectMapper().writeValueAsString(this);
    }
}
