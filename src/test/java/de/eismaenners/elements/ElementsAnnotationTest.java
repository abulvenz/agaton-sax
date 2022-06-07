package de.eismaenners.elements;

import de.eismaenners.agatonsax.AgatonSax;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlElementWrapper;
import jakarta.xml.bind.annotation.XmlElements;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlSeeAlso;
import jakarta.xml.bind.annotation.XmlType;
import java.util.List;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import org.junit.Test;

public class ElementsAnnotationTest {

    @XmlRootElement(name = "root")
    public static class RootElementWithAnnotatedList {

        @XmlElementWrapper
        @XmlElements(value = {
            @XmlElement(name = "r", type = Base.class),
            @XmlElement(name = "a", type = A.class),
            @XmlElement(name = "b", type = B.class)
        })
        List<Base> elements;

        public static class Base {

        }

        public static class A extends Base {

            String aWare;
        }

        public static class B extends Base {

            String bWare;
        }
    }

    @Test
    public void testListWithXmlElementsAnnotation() {
        RootElementWithAnnotatedList result = parseAnnotatedElement(RootElementWithAnnotatedList.class,
                "  <root>"
                + "  <elements>"
                + "    <r />"
                + "    <a aWare=\"aa\" />"
                + "    <b bWare=\"aa\" />"
                + "  </elements>"
                + "</root>"
        );
        assertNotNull(result);
        assertNotNull(result.elements);
        assertEquals(3, result.elements.size());
        assertEquals(RootElementWithAnnotatedList.Base.class, result.elements.get(0).getClass());
        assertEquals(RootElementWithAnnotatedList.A.class, result.elements.get(1).getClass());
        assertEquals(RootElementWithAnnotatedList.B.class, result.elements.get(2).getClass());
    }

    @XmlRootElement(name = "root")
    public static class RootElementWithXmlAlsoList {

        @XmlElementWrapper
        @XmlElements(value = {
            @XmlElement(name = "r", type = Base.class),
            @XmlElement(name = "a", type = A.class),
            @XmlElement(name = "b", type = B.class)
        })
        List<Base> elements;

        @XmlSeeAlso(value = {A.class, B.class})
        public abstract static class Base {

        }

        @XmlType(name = "a")
        public static class A extends Base {

            String aWare;
        }

        public static class B extends Base {

            String bWare;
        }
    }

    @Test
    public void testListWithXmlAlsoAnnotation() {
        RootElementWithXmlAlsoList result = parseAnnotatedElement(RootElementWithXmlAlsoList.class,
                "  <root>"
                + "  <elements>"
//                + "    <r />"
                + "    <a aWare=\"aa\" />"
                + "    <b bWare=\"aa\" />"
                + "  </elements>"
                + "</root>"
        );
        assertNotNull(result);
        assertNotNull(result.elements);
        assertEquals(2, result.elements.size());
        assertEquals(RootElementWithXmlAlsoList.A.class, result.elements.get(0).getClass());
        assertEquals(RootElementWithXmlAlsoList.B.class, result.elements.get(1).getClass());
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
