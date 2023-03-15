package my.group;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

class MyValidatorTest {
MyValidator validator = new MyValidator();
    @Test
    void validatePerson() {
        Person person1 =new Person("Maksimchik",1, LocalDateTime.of(1991,1,10,5,10,5));
        String errors1 = Arrays.toString(validator.validatePerson(person1));
        assertEquals("[Count cannot be not less than 9]", errors1);

        Person person2 =new Person("Egorchik",10, LocalDateTime.of(1991,1,10,5,10,5));
        String errors2 = Arrays.toString(validator.validatePerson(person2));
        assertEquals("[Your name is not valid, name must contain at least one letter a]", errors2);

        Person person3 =new Person("Egorka",10, LocalDateTime.of(1991,1,10,5,10,5));
        String errors3 = Arrays.toString(validator.validatePerson(person3));
        assertEquals("[Yor name length must be more 6]", errors3);
    }

    @Test
    void validateCsvFormat() {
        assertTrue(validator.isValidCsv("myCsv.csv"));
        assertTrue(validator.isValidCsv("fileCsv.cSv"));
        assertFalse(validator.isValidCsv("myCsv.txt"));
        assertFalse(validator.isValidCsv("file.xml"));
    }
}