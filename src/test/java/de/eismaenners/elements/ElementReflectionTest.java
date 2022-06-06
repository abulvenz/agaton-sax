package de.eismaenners.elements;

import de.eismaenners.agatonsax.AgatonSax;
import de.eismaenners.agatonsax.exceptions.MissingAnnotation;
import org.junit.Test;
import static org.junit.Assert.*;

public class ElementReflectionTest {

    public static class RootElementWithoutAnnotation {

        public static class NestedElementWithoutAnnotation {

            String name;
        }

        NestedElementWithoutAnnotation nested;
    }

    @Test(expected = MissingAnnotation.class)
    public void testRootWithoutAnnotationThrowsWhenAddingAnnotatedRoot() {
        MutableObject<RootElementWithoutAnnotation> result = new MutableObject<>();

        AgatonSax.create()
                .addAnnotatedRootClass(RootElementWithoutAnnotation.class, result::setObject)
                .parseString("<RootElementWithoutAnnotation />");

        assertNotNull(result.getObject());
        assertNull(result.getObject().nested);
    }

    @Test
    public void testRootWithoutAnnotation() {
        MutableObject<RootElementWithoutAnnotation> result = new MutableObject<>();

        AgatonSax.create()
                .addRootClass(RootElementWithoutAnnotation.class, result::setObject)
                .parseString("<RootElementWithoutAnnotation />");

        assertNotNull(result.getObject());
        assertNull(result.getObject().nested);
    }

    @Test
    public void testRootWithoutAnnotationAndNestedElement() {
        MutableObject<RootElementWithoutAnnotation> result = new MutableObject<>();

        AgatonSax.create()
                .addRootClass(RootElementWithoutAnnotation.class, result::setObject)
                .parseString("<RootElementWithoutAnnotation><nested /></RootElementWithoutAnnotation>");

        assertNotNull(result.getObject());
        assertNotNull(result.getObject().nested);
        assertNull(result.getObject().nested.name);
    }

    @Test
    public void testRootWithoutAnnotationAndNestedElementWithStringElement() {
        MutableObject<RootElementWithoutAnnotation> result = new MutableObject<>();

        AgatonSax.create()
                .addRootClass(RootElementWithoutAnnotation.class, result::setObject)
                .parseString(
                        "  <RootElementWithoutAnnotation>"
                        + "  <nested><name>My name be Gantenbein</name></nested>"
                        + "</RootElementWithoutAnnotation>");

        assertNotNull(result.getObject());
        assertNotNull(result.getObject().nested);
        assertNotNull(result.getObject().nested.name);

        assertEquals("My name be Gantenbein", result.object.nested.name);
    }

    @Test
    public void testRootWithoutAnnotationAndNestedElementWithStringAttribute() {
        MutableObject<RootElementWithoutAnnotation> result = new MutableObject<>();

        AgatonSax.create()
                .addRootClass(RootElementWithoutAnnotation.class, result::setObject)
                .parseString(
                        "  <RootElementWithoutAnnotation>"
                        + "  <nested name=\"My name be Gantenbein\"></nested>"
                        + "</RootElementWithoutAnnotation>");

        assertNotNull(result.getObject());
        assertNotNull(result.getObject().nested);
        assertNotNull(result.getObject().nested.name);

        assertEquals("My name be Gantenbein", result.object.nested.name);
    }

}
