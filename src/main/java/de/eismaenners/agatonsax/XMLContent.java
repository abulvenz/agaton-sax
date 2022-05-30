package de.eismaenners.agatonsax;

public interface XMLContent<OwnType, ParentType> {

    XMLContentType contentType();

    public enum XMLContentType {
        ELEMENT, ATTRIBUTE, CDATA
    }
    
    public String print(String indent);
}
