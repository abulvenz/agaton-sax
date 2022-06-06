package de.eismaenners.agatonsax.exceptions;

import java.util.List;

public class UnexpectedElement extends AgatonException {

    private String qName;
    private List expectedNames;

    public UnexpectedElement() {
    }

    public UnexpectedElement(String qName) {
        this.qName = qName;
    }

    public UnexpectedElement(String qName, List expectedNames) {
        this.qName = qName;
        this.expectedNames = expectedNames;
    }

    @Override
    public String getMessage() {
        return "Unexpected element: <" + qName + (expectedNames != null ? "> expected one of: " + expectedNames : ">");
    }

}
