package com.tl.models.application.game;

import com.tl.models.application.game.field.BaseField;
import com.tl.models.application.game.ws_messages.messages.MovePinMessage;
import com.tl.models.application.user.SessionUser;
import com.tl.resources.GameSocketResource;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.UUID;

@Data
@ToString
public class NinePin {
    private final UUID pinId;
    private BaseField currentLocation;
    private NinePinColor color;

    public NinePin(BaseField location, NinePinColor color) {
        this.pinId = UUID.randomUUID();
        this.currentLocation = location;
        this.color = color;
    }

    public void broadcastMovement(GameSessionContext context, int previousLocation) {
        var loc = PinDirection.fromPositions(previousLocation, this.currentLocation.getNodeId());
        GameSocketResource.makeGameBroadcast(context, new MovePinMessage(this.pinId, this.currentLocation.getNodeId(), loc));
    }
}
