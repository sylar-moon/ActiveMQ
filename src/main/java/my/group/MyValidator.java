package my.group;

import org.slf4j.Logger;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.io.IOException;
import java.util.Set;

public class MyValidator {
    private static final Logger LOGGER = new MyLogger().getLogger();
    public static final ValidatorFactory FACTORY = Validation.buildDefaultValidatorFactory();
    public static final Validator VALIDATOR = FACTORY.getValidator();
    private final JsonConverter converter = new JsonConverter();
    private static final String INVALID_PERSONS_CSV = "invalidPersons.csv";
    private static final String VALID_PERSONS_CSV = "validPersons.csv";


    public void validatePerson(String json) { //visualwm
        Person person = converter.createPOJOFromJson(json);
        Set<ConstraintViolation<Person>> violation = VALIDATOR.validate(person);
        WriterToCSV invalidWriter;
        WriterToCSV validWriter;
        try {
            WriterToCSV invalidWriter = new WriterToCSV(INVALID_PERSONS_CSV);
            WriterToCSV validWriter = new WriterToCSV(VALID_PERSONS_CSV);
            if (!violation.isEmpty()) {
                String[] errorMessage = new String[violation.size() + 2];
                errorMessage[0] = json;
                errorMessage[1] = "errors: ";
                LOGGER.error("This POJO is invalid:{}", json);
                for (ConstraintViolation<Person> viol : violation) {
                    LOGGER.error(viol.getMessage());
                }
                invalidWriter.WriteMessageToCSV(errorMessage);
            } else {
                String[] validPerson = {json};
                validWriter.WriteMessageToCSV(validPerson);
                LOGGER.info("This POJO is valid! :{}", json);
            }
        } catch (FileFormatException e) {
            LOGGER.error("The file format must be csv",e);
        }
        catch (IOException e) {
            LOGGER.error("It is not possible to create such a csv file",e);
        }

    }
}
