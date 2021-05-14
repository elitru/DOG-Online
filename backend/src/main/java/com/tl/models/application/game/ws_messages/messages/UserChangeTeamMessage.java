package com.tl.models.application.game.ws_messages.messages;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tl.models.application.game.states.GameStateIdentifier;
import com.tl.models.application.game.ws_messages.Message;
import com.tl.models.application.game.ws_messages.message_type.MessageType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.SneakyThrows;

import java.util.UUID;


@Data

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

    public UserChangeTeamMessage(UUID userId, int newTeam, int oldTeam) {
        super(MessageType.UserTeamChangedUpdate);
        this.userId = userId;
        this.newTeam = newTeam;
        this.oldTeam = oldTeam;
    }
}
