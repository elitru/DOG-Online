package com.tl.models.application.game.field;

import java.util.Optional;
import java.util.UUID;

public class HomeField extends BaseField {

    public HomeField(Optional<BaseField> previous, Optional<BaseField> next, int nodeId) {
        super(previous, next, nodeId);
    }

    public HomeField(int nodeId) {
        super(Optional.empty(), Optional.empty(), nodeId);
    }
}
