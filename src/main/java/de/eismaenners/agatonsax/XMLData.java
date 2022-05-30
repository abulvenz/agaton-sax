package de.eismaenners.agatonsax;

import java.util.function.BiConsumer;
import java.util.function.Function;

public class XMLData<OwnType, ParentType> {

    private final Class<OwnType> clasz;
    private final Function<String, OwnType> parser;
    private final BiConsumer<OwnType, ParentType> whenParsed;

    public XMLData(Class<OwnType> clasz, Function<String, OwnType> parser, BiConsumer<OwnType, ParentType> whenParsed) {
        this.clasz = clasz;
        this.parser = parser;
        this.whenParsed = whenParsed;
    }

    public Function<String, OwnType> getParser() {
        return parser;
    }

    public BiConsumer<OwnType, ParentType> getWhenParsed() {
        return whenParsed;
    }

}
