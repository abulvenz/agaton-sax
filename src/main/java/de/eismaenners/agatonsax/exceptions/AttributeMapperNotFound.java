package de.eismaenners.agatonsax.exceptions;

public class AttributeMapperNotFound extends RuntimeException {
    
    Class<?> clasz;

    public AttributeMapperNotFound(Class<?> clasz) {
        this.clasz = clasz;
    }
    
   
    
}
