package de.eismaenners.elements;

import de.eismaenners.agatonsax.AgatonSax;
import de.eismaenners.agatonsax.exceptions.UnexpectedAttribute;
import de.eismaenners.agatonsax.exceptions.UnexpectedElement;
import de.eismaenners.agatonsax.exceptions.WrongEnumType;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlEnum;
import jakarta.xml.bind.annotation.XmlEnumValue;
import jakarta.xml.bind.annotation.XmlRootElement;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
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
        AnnotatedEnum type;

    }

    @XmlEnum
    public enum AnnotatedEnum {
        @XmlEnumValue("T1")
        T1,
        @XmlEnumValue("T2")
        T2
    }

    @Test
    public void testStringAttribute() {
        RootElementWithAttributes object = parseAnnotatedElement(
                RootElementWithAttributes.class,
                "<root name=\"jupp\" />"
        );
        assertNotNull(object);
        assertEquals("jupp", object.name);
    }

    @Test
    public void testIntegerAttribute() {
        RootElementWithAttributes object = parseAnnotatedElement(
                RootElementWithAttributes.class,
                "<root integer=\"3\" />"
        );
        assertNotNull(object);
        assertEquals(Integer.valueOf(3), object.integer);
    }

    @Test
    public void testIntAttribute() {
        RootElementWithAttributes object = parseAnnotatedElement(
                RootElementWithAttributes.class,
                "<root Int=\"3\" />"
        );
        assertNotNull(object);
        assertEquals(3, object.Int);
    }

    @Test
    public void testEnumAttribute() {
        RootElementWithAttributes object = parseAnnotatedElement(
                RootElementWithAttributes.class,
                "<root type=\"T1\" />"
        );
        assertNotNull(object);
        assertEquals(AnnotatedEnum.T1, object.type);
    }

    @Test(expected = UnexpectedAttribute.class)
    public void testUnexpectedAttribute() {
        parseAnnotatedElement(
                RootElementWithAttributes.class,
                "<root not-present=\"this is\" />"
        );
    }

    @Test(expected = WrongEnumType.class)
    public void testWrongEnumAttribute() {
        parseAnnotatedElement(
                RootElementWithAttributes.class,
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
        AnnotatedEnum type;
    }

    @Test
    public void testNamedStringAttribute() {
        RootElementWithNamedAttributes object = parseAnnotatedElement(
                RootElementWithNamedAttributes.class,
                "<root the-name=\"jupp\" />"
        );
        assertNotNull(object);
        assertEquals("jupp", object.name);
    }

    @Test
    public void testNamedIntegerAttribute() {
        RootElementWithNamedAttributes object = parseAnnotatedElement(
                RootElementWithNamedAttributes.class,
                "<root the-integer=\"3\" />"
        );
        assertNotNull(object);
        assertEquals(Integer.valueOf(3), object.integer);
    }

    @Test
    public void testNamedIntAttribute() {
        RootElementWithNamedAttributes object = parseAnnotatedElement(
                RootElementWithNamedAttributes.class,
                "<root the-int=\"3\" />"
        );
        assertNotNull(object);
        assertEquals(3, object.Int);
    }

    @Test
    public void testNamedEnumAttribute() {
        RootElementWithNamedAttributes object = parseAnnotatedElement(
                RootElementWithNamedAttributes.class,
                "<root the-type=\"T1\" />"
        );
        assertNotNull(object);
        assertEquals(AnnotatedEnum.T1, object.type);
    }

    @XmlRootElement(name = "root")
    public static class RootWithElements {

        @XmlElement
        String name;

        @XmlElement
        Integer integer;

        @XmlElement
        int Int;

        @XmlElement
        AnnotatedEnum type;
    }

    @Test
    public void testStringElement() {
        RootWithElements object = parseAnnotatedElement(
                RootWithElements.class,
                "  <root>"
                + "  <name>My funny Valentine</name>"
                + "</root>"
        );
        assertNotNull(object);
        assertEquals("My funny Valentine", object.name);
    }

    @Test
    public void testIntegerElement() {
        RootWithElements object = parseAnnotatedElement(
                RootWithElements.class,
                "  <root>"
                + "  <integer>3</integer>"
                + "</root>"
        );
        assertNotNull(object);
        assertEquals(Integer.valueOf(3), object.integer);
    }

    @Test
    public void testEnumElement() {
        RootWithElements object = parseAnnotatedElement(
                RootWithElements.class,
                "  <root>"
                + "  <type>T1</type>"
                + "</root>"
        );
        assertNotNull(object);
        assertEquals(AnnotatedEnum.T1, object.type);
    }

    @Test(expected = WrongEnumType.class)
    public void testWrongEnumElement() {
        parseAnnotatedElement(
                RootWithElements.class,
                "  <root>"
                + "  <type>T3</type>"
                + "</root>"
        );
    }

    @Test(expected = UnexpectedElement.class)
    public void testUnexpectedElement() {
        parseAnnotatedElement(
                RootWithElements.class,
                "  <root>"
                + "  <unknown></unknown>"
                + "</root>"
        );
    }

    @XmlRootElement(name = "root")
    public static class RootWithNestedElements {

        public static class Nested {

            @XmlElement(name = "further")
            Nested nested;

            @XmlElement
            String content;
        }
        @XmlElement(name = "onroot")
        Nested nested;
    }

    @Test
    public void testNestedElements() {
        RootWithNestedElements object = parseAnnotatedElement(
                RootWithNestedElements.class,
                "  <root>"
                + "  <onroot>"
                + "    <further />"
                + "  </onroot>"
                + "</root>"
        );
        assertNotNull(object);
        assertNotNull(object.nested);
        assertNotNull(object.nested.nested);
    }

    @Test
    public void testFurtherNestedElements() {
        RootWithNestedElements object = parseAnnotatedElement(
                RootWithNestedElements.class,
                "  <root>"
                + "  <onroot>"
                + "    <further>"
                + "      <further>"
                + "        <further>"
                + "          <content>TheContent</content>"
                + "        </further>"
                + "      </further>"
                + "    </further>"
                + "  </onroot>"
                + "</root>"
        );
        assertNotNull(object);
        assertNotNull(object.nested);
        assertNotNull(object.nested.nested);
        assertNotNull(object.nested.nested.nested);
        assertNotNull(object.nested.nested.nested.nested);
        assertNull(object.nested.nested.nested.nested.nested);
        assertEquals("TheContent", object.nested.nested.nested.nested.content);
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
