package de.eismaenners.elements;

import de.eismaenners.agatonsax.AgatonSax;
import de.eismaenners.agatonsax.exceptions.WrongEnumType;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlEnum;
import jakarta.xml.bind.annotation.XmlEnumValue;
import jakarta.xml.bind.annotation.XmlRootElement;
import static junit.framework.Assert.assertEquals;
import org.junit.Assert;
import static org.junit.Assert.assertNotNull;
import org.junit.Test;

public class ElementsAnnotationTest {

    @XmlRootElement
    public static class NoName {

    }

    @Test
    public void testUseNameWhenMissingInAnnotation() {
        NoName result = parseAnnotatedElement(
                NoName.class,
                "<NoName />"
        );
        assertNotNull(result);
    }


    private <T> T parseAnnotatedElement(Class<T> clasz, String xml) {
        MutableObject<T> result = new MutableObject<>();
        AgatonSax.create()
                .addAnnotatedRootClass(clasz, result::setObject)
                .parseString(xml);
        T object = result.getObject();
        return object;
    }
}
