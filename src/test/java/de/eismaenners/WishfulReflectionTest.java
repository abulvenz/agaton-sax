package de.eismaenners;

import de.eismaenners.agatonsax.AgatonSax;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlElementWrapper;
import jakarta.xml.bind.annotation.XmlRootElement;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParserFactory;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class WishfulReflectionTest {

    public WishfulReflectionTest() {
    }

    @BeforeClass
    public static void setUpClass() {
    }

    @AfterClass
    public static void tearDownClass() {
    }

    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {
    }

    @XmlRootElement(name = "file")
    public static class File {

        @XmlElement(name = "name")
        String name;
        @XmlElement(name = "time")
        Time time;

        @XmlElementWrapper(name = "properties")
        @XmlElement(name = "property")
        List<Property> properties;

        public File() {
        }

        public File(String name, Time time) {
            this.name = name;
            this.time = time;
        }

        public static class Property {
            @XmlAttribute(name = "key")
            String key;
            @XmlAttribute(name = "value")
            String value;
        }

        public static class Time {

            @XmlAttribute(name = "hour")
            Integer hour;
            @XmlAttribute(name = "minute")
            int minute;

            public Time() {
            }

            public Time(Integer hour, int minute) {
                this.hour = hour;
                this.minute = minute;
            }

            @Override
            public String toString() {
                return "TIME(" + Integer.toString(hour, 10) + ":" + Integer.toString(minute, 10) + ")";
            }

            @Override
            public int hashCode() {
                int hash = 7;
                hash = 29 * hash + Objects.hashCode(this.hour);
                hash = 29 * hash + this.minute;
                return hash;
            }

            @Override
            public boolean equals(Object obj) {
                if (this == obj) {
                    return true;
                }
                if (obj == null) {
                    return false;
                }
                if (getClass() != obj.getClass()) {
                    return false;
                }
                final Time other = (Time) obj;
                if (this.minute != other.minute) {
                    return false;
                }
                return Objects.equals(this.hour, other.hour);
            }

        }

        @Override
        public String toString() {
            return "FILE"
                    + "\n\tname: " + name
                    + "\n\ttime: " + time;
        }

        @Override
        public int hashCode() {
            int hash = 5;
            hash = 79 * hash + Objects.hashCode(this.name);
            hash = 79 * hash + Objects.hashCode(this.time);
            return hash;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null) {
                return false;
            }
            if (getClass() != obj.getClass()) {
                return false;
            }
            final File other = (File) obj;
            if (!Objects.equals(this.name, other.name)) {
                return false;
            }
            return Objects.equals(this.time, other.time);
        }

    }

    String xml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
            + "<file>"
            + "  <name>c:\\autoexec.bat</name>"
            + "  <time hour=\"11\" minute=\"55\">"
            + "  </time>"
            + "  <properties>"
            + "    <property key=\"owner\" value=\"root\"></property>"
            + "  </properties>"
            + "</file>";

    @Test
    public void testMethod() throws ParserConfigurationException, SAXException, IOException {
        try {
        List<File> files = new ArrayList<>();
        DefaultHandler handler
                = AgatonSax.create()
                        .addRootClass(File.class, files::add)
                        .getHandler();

        SAXParserFactory
                .newDefaultInstance()
                .newSAXParser()
                .parse(new ByteArrayInputStream(xml.getBytes()), handler);

        assertEquals("Size is 1",1, files.size());
        assertEquals("Name is correct","c:\\autoexec.bat", files.get(0).name);
        assertEquals("Hour is correct", Integer.valueOf(11), files.get(0).time.hour);
        assertEquals("Minute is correct",55, files.get(0).time.minute);
        assertEquals("Owner key is correct","owner", files.get(0).properties.get(0).key);
        assertEquals("Owner value is correct", "root", files.get(0).properties.get(0).value);

        assertEquals("Files are equal", new File("c:\\autoexec.bat", new File.Time(11, 55)), files.get(0));
        }catch (Exception e) {
            e.printStackTrace();
        }
    }
}
