package com.tl.models.application.game.field;

import java.util.Optional;
import java.util.UUID;

public class StartField extends BaseField {

    private HomeField firstHomeField;
    private TargetField firstTargetField;

    public StartField(Optional<BaseField> previous, Optional<BaseField> next, UUID nodeId) {
        super(previous, next, nodeId);
    }

}
