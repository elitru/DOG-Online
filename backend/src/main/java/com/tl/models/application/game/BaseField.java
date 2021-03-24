package com.tl.models.application.game;

import java.util.Optional;

public abstract class BaseField {

    private Optional<NinePin> pin;

    /**
     * Clears the the pin and returns the latest value
     * 
     * @return
     */
    public Optional<NinePin> clearPin() {
        var pin = this.pin;
        this.pin = Optional.empty();
        return pin;
    }

    /**
     * returns an option containing the currently active ninpin of this field
     * 
     * @return the current ninepin if present; an empty option otherwise
     */
    public Optional<NinePin> getNinePin() {
        return this.pin;
    }

    /**
     * checks whether a ninepin is currently positioned on this field
     * 
     * @return true when a ninepin is present; otherwise false
     */
    public boolean isPinPresent() {
        return this.pin.isPresent();
    }
}
