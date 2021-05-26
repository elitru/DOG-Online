package com.tl.models.client.responses;

import com.tl.models.application.game.NinePin;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class NinePinResponse {
    public UUID pinId;
    public int fieldId;

    public static NinePinResponse from(NinePin pin) {
        return new NinePinResponse(pin.getPinId(), pin.getCurrentLocation().getNodeId());
    }
}
