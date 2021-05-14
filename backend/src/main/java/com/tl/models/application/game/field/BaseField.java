package com.tl.models.application.game.field;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.Optional;
import java.util.UUID;

@Getter
public class BaseField {
    private Optional<BaseField> previous;
    private Optional<BaseField> next;

    private UUID nodeId;

    public BaseField(Optional<BaseField> previous, Optional<BaseField> next, UUID nodeId) {
        this.previous = previous;
        this.next = next;
        this.nodeId = nodeId;
    }

    public BaseField() {
        this.previous = Optional.empty();
        this.next = Optional.empty();
        this.nodeId = UUID.randomUUID();
    }

    public boolean hasNext() {
        return this.next.isPresent();
    }

    public boolean hasPrevious() {
        return this.previous.isPresent();
    }

    public void setNext(BaseField next) {
        this.next = Optional.ofNullable(next);
    }

    public void setPrevious(BaseField previous) {
        this.previous = Optional.ofNullable(previous);
    }
}
