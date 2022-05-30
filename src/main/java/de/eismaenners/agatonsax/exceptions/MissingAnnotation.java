package de.eismaenners.agatonsax.exceptions;

public class MissingAnnotation extends RuntimeException {

    private final String message;

    public MissingAnnotation(String message) {
        this.message = message;
    }

    @Override
    public String getMessage() {
        return message;
    }

}
