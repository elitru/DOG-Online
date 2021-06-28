package com.tl.models.application.game;

import com.fasterxml.jackson.annotation.JsonValue;

public enum PinDirection {
    Forward("forward"),
    Backward("backward");

    private String directionStr;

    PinDirection(String directionStr) {
        this.directionStr = directionStr;
    }

    public String getDirectionStr() {
        return directionStr;
    }

    public static PinDirection fromPositions(int oldPosition, int newPosition) {

        // moving to/from home field or moving forward in general
        if ((oldPosition < 0 && oldPosition >= -16 ) || (newPosition < 0 && newPosition >= -16 ) || newPosition > oldPosition) {
            System.out.println("here");
            return PinDirection.Forward;
        }

        // moving from board to target field
        if (oldPosition > 0 && newPosition < -16) {
            System.out.println("here2");
            return PinDirection.Forward;
        }

        // here, newPosition < oldPosition
        int delta = Math.abs(newPosition) - Math.abs(oldPosition);
        if (delta > 0) {
            // moving forward
            return PinDirection.Forward;
        } else {
            if (delta < -4 ) {
                return PinDirection.Forward;
            }
            return PinDirection.Backward;
        }


    }
}
