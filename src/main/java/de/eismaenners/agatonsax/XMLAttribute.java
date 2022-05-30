package de.eismaenners.agatonsax;

import java.util.function.BiConsumer;
import java.util.function.Function;

public class XMLAttribute<OwnType, ParentType> implements XMLContent<OwnType, ParentType> {

    private final String name;
    private final Class<OwnType> clasz;
    private final Function<String, OwnType> parser;
    private final BiConsumer< ParentType, OwnType> whenParsed;

    public XMLAttribute(String name, Class<OwnType> clasz, Function<String, OwnType> parser, BiConsumer< ParentType, OwnType> whenParsed) {
        this.name = name;
        this.clasz = clasz;
        this.parser = parser;
        this.whenParsed = whenParsed;
    }

    public Function<String, OwnType> getParser() {
        return parser;
    }

    public BiConsumer< ParentType, OwnType> getWhenParsed() {
        return whenParsed;
    }

    public void apply(ParentType decoratee, String value) {
        whenParsed.accept(decoratee, parser.apply(value));
    }

    @Override
    public XMLContentType contentType() {
        return XMLContentType.ATTRIBUTE;
    }

    public String getName() {
        return name;
    }

    @Override
    public String print(String indent) {
        return name + "[" + clasz.getSimpleName() + "]";
    }

}
