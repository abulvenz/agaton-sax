package de.eismaenners.agatonsax;

public class ExpectedEnum extends RuntimeException {

    private final Class<?> clasz;

    public ExpectedEnum(Class<?> clasz) {
        this.clasz = clasz;
    }

    @Override
    public String getMessage() {
        return "Expected type " + clasz.getName() + " to be an enum.";
    }
    
    
    
}
