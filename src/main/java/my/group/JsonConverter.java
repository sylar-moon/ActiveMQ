package my.group;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.slf4j.Logger;


public class JsonConverter {
    private static final Logger LOGGER = new MyLogger().getLogger();
    private final ObjectMapper mapper = new ObjectMapper();

    public String createJsonFromObjects(Person person) {
        mapper.registerModule(new JavaTimeModule());
        mapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
        try {
            return (mapper.writeValueAsString(person));
        } catch (JsonProcessingException e) {
            LOGGER.error("Invalid POJO object: {} ", person, e);
            System.exit(0);
        }
        return null;
    }

    public Person createPOJOFromJson(String json) {
        mapper.registerModule(new JavaTimeModule());
        mapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
        try {
            return mapper.readValue(json, Person.class);
        } catch (JsonProcessingException e) {
            LOGGER.error("It is not possible to create POJO object from this string: {}", json, e);
            System.exit(0);
        }
        return null;
    }

}

