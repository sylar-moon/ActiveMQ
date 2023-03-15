package my.group;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class JsonConverterTest {
JsonConverter converter = new JsonConverter();
    @Test
    void createJsonFromObjects() {
        Person person1 =new Person("Maksim",1, LocalDateTime.of(1991,1,10,5,10,5));
        String json1 = "{\"name\":\"Maksim\",\"count\":1,\"createdAt\":\"1991-01-10T05:10:05\"}";
        assertEquals(converter.createJsonFromObjects(person1),json1);
        Person person2 =new Person("Taras",2, LocalDateTime.of(1990,2,21,10,10,5));
        String json2 = "{\"name\":\"Taras\",\"count\":2,\"createdAt\":\"1990-02-21T10:10:05\"}";
        assertEquals(converter.createJsonFromObjects(person2),json2);
    }

    @Test
    void createPersonFromJson() {
        Person person1 =new Person("Maksim",1, LocalDateTime.of(1991,1,10,5,10,5));
        String json1 = "{\"name\":\"Maksim\",\"count\":1,\"createdAt\":\"1991-01-10T05:10:05\"}";
        assertEquals(converter.createPersonFromJson(json1),person1);
        Person person2 =new Person("Taras",2, LocalDateTime.of(1990,2,21,10,10,5));
        String json2 = "{\"name\":\"Taras\",\"count\":2,\"createdAt\":\"1990-02-21T10:10:05\"}";
        assertEquals(converter.createPersonFromJson(json2),person2);
    }

}