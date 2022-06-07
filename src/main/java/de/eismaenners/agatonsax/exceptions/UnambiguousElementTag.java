package de.eismaenners.agatonsax.exceptions;

public class UnambiguousElementTag extends AgatonException {

    private final String currentTag;
    private final String parentTag;

    public UnambiguousElementTag(String currentTag, String parentTag) {
        this.currentTag = currentTag;
        this.parentTag = parentTag;
    }

    @Override
    public String getMessage() {
        return "Unambigous element <" + currentTag + "> on <" + parentTag + ">.";
    }

}
