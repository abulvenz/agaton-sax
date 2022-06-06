package de.eismaenners.agatonsax;

import de.eismaenners.agatonsax.exceptions.AgatonException;

public interface ErrorHandler {

    public default void handleError(AgatonException e) {
        throw e;
    }
}
