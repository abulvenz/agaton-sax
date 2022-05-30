package de.eismaenners.agatonsax;

import de.eismaenners.agatonsax.exceptions.UnexpectedAttribute;
import java.util.Optional;

public class ParsingContextWithObject<OwnType, ParentType> extends ParsingContext<OwnType, ParentType> {

    private final XMLElement<OwnType, ParentType> element;
    private OwnType decoratee;

    public ParsingContextWithObject(XMLElement<OwnType, ParentType> element) {
        super(element.subElements());
        this.element = element;
    }

    @Override
    void createObject() {
        decoratee = element.getCreator().get();
    }

    @Override
    void applyAttribute(String name, String value) {
        XMLAttribute<?, OwnType> attribute = element.getAttributesByName().get(name);
        if (attribute == null) {
            throw new UnexpectedAttribute(name, element.attributeNames());
        }

        attribute.apply(decoratee, value);
    }

    @Override
    OwnType getObject() {
        return decoratee;
    }

    @Override
    void reduce(ParsingContext<ParentType, ?> next) {
        ParentType parent = Optional.ofNullable(next).map(ParsingContext::getObject).orElse(null);
        element.getWhenParsed().accept(parent, decoratee);
    }

    @Override
    public void addText(String str) {
        if (element.getClasz().equals(String.class)) {
            decoratee = (OwnType) str;
        }
    }

    @Override
    public String toString() {
        return decoratee == null ? "null" : decoratee.getClass().toString();
    }

}
