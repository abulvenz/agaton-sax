package de.eismaenners.agatonsax.exceptions;

public class WrongEnumType extends AgatonException {

    String str;
    Class<?> clasz;

    public WrongEnumType(String str, Class<?> clasz) {
        this.str = str;
        this.clasz = clasz;
    }

    @Override
    public String getMessage() {
        return "Cannot create "
                + clasz.getName()
                + " from "
                + str
                + ".";
    }

}
