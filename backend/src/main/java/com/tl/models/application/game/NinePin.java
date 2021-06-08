package com.tl.models.application.game;

import com.tl.models.application.game.field.BaseField;
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
}
