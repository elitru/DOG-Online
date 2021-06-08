package com.tl.models.application.game.field;

public interface FieldSetter<T extends BaseField> {
    public void set(T next);
}
