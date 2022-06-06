package de.eismaenners.agatonsax.exceptions;

public class AttributeMapperNotFound extends AgatonException {

    Class<?> clasz;

    public AttributeMapperNotFound(Class<?> clasz) {
        this.clasz = clasz;
        System.out.println("de.eismaenners.agatonsax.exceptions.AttributeMapperNotFound.<init>()" + clasz.getName());
    }

    @Override
    public String getMessage() {
        return "AttributeMapper not found for " + clasz.getName();
    }

}
