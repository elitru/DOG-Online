package com.tl.models.application.game.ws_messages.messages.state_data_models;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.tl.models.application.user.User;
import com.tl.models.client.responses.FieldResponse;
import com.tl.models.client.responses.NinePinResponse;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class IngameStatePayload {
    @JsonProperty
    private List<FieldResponse> boardSetup;

    @JsonProperty
    private Map<UUID, List<NinePinResponse>> ninepins;
}
