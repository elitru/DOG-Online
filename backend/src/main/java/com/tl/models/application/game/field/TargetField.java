package com.tl.models.application.game.field;

import java.util.Optional;
import java.util.UUID;

public class TargetField extends BaseField {

    public TargetField(Optional<BaseField> previous, Optional<BaseField> next, int nodeId) {
        super(previous, next, nodeId);
    }

    public TargetField(int nodeId) {
        super(Optional.empty(), Optional.empty(), nodeId);
    }
}
