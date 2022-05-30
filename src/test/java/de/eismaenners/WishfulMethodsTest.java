package de.eismaenners;

import static de.eismaenners.agatonsax.AgatonSax.*;
import de.eismaenners.agatonsax.AgatonSax;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;
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

public class WishfulMethodsTest {

    public WishfulMethodsTest() {
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

    static class File {

        String name;
        Time time;
        List<Property> properties;

        public File() {
        }

        public File(String name, Time time) {
            this.name = name;
            this.time = time;
        }

        public void setName(String name) {
            this.name = name;
        }

        public void setTime(Time time) {
            this.time = time;
        }

        public void setProperties(List<Property> properties) {
            this.properties = properties;
        }

        static class Property {

            String key;
            String value;

            public void setKey(String key) {
                this.key = key;
            }

            public void setValue(String value) {
                this.value = value;
            }
        }

        static class Time {

            Integer hour;
            int minute;

            public Time() {
            }

            public Time(Integer hour, int minute) {
                this.hour = hour;
                this.minute = minute;
            }

            public void setHour(Integer hour) {
                this.hour = hour;
            }

            public void setMinute(int minute) {
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
    public void test() throws ParserConfigurationException, SAXException, IOException {
        List<File> files = new ArrayList<>();
        DefaultHandler handler = AgatonSax
                .create()
                .addRootElement(
                        "file", File.class, File::new, files::add,
                        element("name", String.class, String::new, File::setName),
                        element("time", File.Time.class, File.Time::new, File::setTime,
                                attribute("hour", Integer.class, Integer::parseInt, File.Time::setHour),
                                attribute("minute", Integer.class, Integer::parseInt, File.Time::setMinute)
                        ),
                        element("properties", List.class, LinkedList::new, File::setProperties,
                                element("property", File.Property.class, File.Property::new, List::add,
                                        attribute("key", String.class, Function.identity(), File.Property::setKey),
                                        attribute("value", String.class, Function.identity(), File.Property::setValue)
                                )
                        )
                )
                .getHandler();

        SAXParserFactory
                .newDefaultInstance()
                .newSAXParser()
                .parse(new ByteArrayInputStream(xml.getBytes()), handler);

        assertEquals(1, files.size());
        assertEquals("c:\\autoexec.bat", files.get(0).name);
        assertEquals(Integer.valueOf(11), files.get(0).time.hour);
        assertEquals(55, files.get(0).time.minute);
        assertEquals("owner", files.get(0).properties.get(0).key);
        assertEquals("root", files.get(0).properties.get(0).value);

        assertEquals(new File("c:\\autoexec.bat", new File.Time(11, 55)), files.get(0));
    }
}
