package com.tl.models.application.game.field;

import lombok.Data;

import java.util.Optional;
import java.util.UUID;

@Data
public class StartField extends BaseField {

    private HomeField firstHomeField;
    private TargetField firstTargetField;

    public StartField(Optional<BaseField> previous, Optional<BaseField> next, int nodeId) {
        super(previous, next, nodeId);
    }

    public StartField(int nodeId) {
        super(Optional.empty(), Optional.empty(), nodeId);
    }
}
