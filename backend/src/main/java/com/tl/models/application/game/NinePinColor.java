package com.tl.models.application.game;

import com.tl.models.client.responses.NinePinColorResponse;

public enum NinePinColor {
    Red,
    Blue,
    Green,
    Yellow;
    public NinePinColor next() {
        switch (this) {
            case Red:
                return NinePinColor.Blue;
            case Blue:
                return NinePinColor.Green;
            case Green:
                return NinePinColor.Yellow;
            case Yellow:
                return NinePinColor.Red;
            default:
                throw new RuntimeException("Can't happen");
        }
    }
    public NinePinColorResponse toResponse() {
        switch (this) {
            case Red:
                return NinePinColorResponse.Red;
            case Blue:
                return NinePinColorResponse.Blue;
            case Green:
                return NinePinColorResponse.Green;
            case Yellow:
                return NinePinColorResponse.Yellow;
            default:
                throw new RuntimeException("Can't happen");
        }
    }
}
