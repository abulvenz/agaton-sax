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

public class ElementAnnotationTest {

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

    @XmlRootElement(name = "root")
    public static class RootElement {

    }

    @Test
    public void testUseNameFromAnnotation() {
        RootElement object = parseAnnotatedElement(
                RootElement.class,
                "<root />"
        );
        assertNotNull(object);
    }

    @XmlRootElement(name = "root")
    public static class RootElementWithAttributes {

        @XmlAttribute
        String name;

        @XmlAttribute
        Integer integer;

        @XmlAttribute
        int Int;

        @XmlAttribute
        Type type;

        @XmlEnum
        public enum Type {
            @XmlEnumValue("T1")
            T1,
            @XmlEnumValue("T2")
            T2
        }
    }

    @Test
    public void testStringAttribute() {
        RootElementWithAttributes object = parseAnnotatedElement(RootElementWithAttributes.class,
                "<root name=\"jupp\" />"
        );
        assertNotNull(object);
        assertEquals("jupp", object.name);
    }

    @Test
    public void testIntegerAttribute() {
        RootElementWithAttributes object = parseAnnotatedElement(RootElementWithAttributes.class,
                "<root integer=\"3\" />"
        );
        assertNotNull(object);
        assertEquals(Integer.valueOf(3), object.integer);
    }

    @Test
    public void testIntAttribute() {
        RootElementWithAttributes object = parseAnnotatedElement(RootElementWithAttributes.class,
                "<root Int=\"3\" />"
        );
        assertNotNull(object);
        assertEquals(3, object.Int);
    }

    @Test
    public void testEnumAttribute() {
        RootElementWithAttributes object = parseAnnotatedElement(RootElementWithAttributes.class,
                "<root type=\"T1\" />"
        );
        assertNotNull(object);
        assertEquals(RootElementWithAttributes.Type.T1, object.type);
    }

    @Test(expected = WrongEnumType.class)
    public void testWrongEnumAttribute() {
        parseAnnotatedElement(RootElementWithAttributes.class,
                "<root type=\"T12\" />"
        );
    }

    @XmlRootElement(name = "root")
    public static class RootElementWithNamedAttributes {

        @XmlAttribute(name = "the-name")
        String name;

        @XmlAttribute(name = "the-integer")
        Integer integer;

        @XmlAttribute(name = "the-int")
        int Int;

        @XmlAttribute(name = "the-type")
        RootElementWithAttributes.Type type;
    }

    @Test
    public void testNamedStringAttribute() {
        RootElementWithNamedAttributes object = parseAnnotatedElement(RootElementWithNamedAttributes.class,
                "<root the-name=\"jupp\" />"
        );
        assertNotNull(object);
        assertEquals("jupp", object.name);
    }

    @Test
    public void testNamedIntegerAttribute() {
        RootElementWithNamedAttributes object = parseAnnotatedElement(RootElementWithNamedAttributes.class,
                "<root the-integer=\"3\" />"
        );
        assertNotNull(object);
        assertEquals(Integer.valueOf(3), object.integer);
    }

    @Test
    public void testNamedIntAttribute() {
        RootElementWithNamedAttributes object = parseAnnotatedElement(RootElementWithNamedAttributes.class,
                "<root the-int=\"3\" />"
        );
        assertNotNull(object);
        assertEquals(3, object.Int);
    }

    @Test
    public void testNamedEnumAttribute() {
        RootElementWithNamedAttributes object = parseAnnotatedElement(RootElementWithNamedAttributes.class,
                "<root the-type=\"T1\" />"
        );
        assertNotNull(object);
        assertEquals(RootElementWithAttributes.Type.T1, object.type);
    }

    @XmlRootElement(name = "root")
    public static class RootWithElements {

        @XmlElement
        String name;

        @XmlElement
        Integer integer;

        @XmlElement
        int Int;
    }

    @Test
    public void testStringElement() {
        RootWithElements object = parseAnnotatedElement(RootWithElements.class,
                "  <root>"
                + "  <name>My funny Valentine</name>"
                + "</root>"
        );
        assertNotNull(object);
        assertEquals("My funny Valentine", object.name);
    }

    @Test
    public void testIntegerElement() {
        RootWithElements object = parseAnnotatedElement(RootWithElements.class,
                "  <root>"
                + "  <integer>3</integer>"
                + "</root>"
        );
        assertNotNull(object);
        assertEquals(Integer.valueOf(3), object.integer);
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
