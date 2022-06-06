package de.eismaenners.agatonsax.exceptions;

import java.util.List;

public class UnexpectedAttribute extends AgatonException {

    private final String name;
    private final List<String> expected;

    public UnexpectedAttribute(String name, List<String> expected) {
        this.name = name;
        this.expected = expected;
    }

    @Override
    public String getMessage() {
        return "Unknown attribute " + name + " " + expected;
    }
      
}
