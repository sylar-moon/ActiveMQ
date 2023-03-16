package my.group;

import org.junit.jupiter.api.Test;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

class PersonFactoryTest {
    PersonFactory factory = new PersonFactory();
    @Test
    void createStreamRandomPerson() {
        Stream<Person> stream = factory.createStreamRandomPerson();
        Person person = stream.findFirst().get();
        assertTrue(person instanceof Person);
    }
}