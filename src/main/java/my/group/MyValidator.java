package my.group;

import org.slf4j.Logger;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.util.Locale;
import java.util.Set;

public class MyValidator {
    private static final Logger LOGGER = new MyLogger().getLogger();
    public static final ValidatorFactory FACTORY = Validation.buildDefaultValidatorFactory();
    public static final Validator VALIDATOR = FACTORY.getValidator();

    public String[] validatePerson(Person person) { //visualwm
        Set<ConstraintViolation<Person>> violation = VALIDATOR.validate(person);
        String[] result;
        if (!violation.isEmpty()) {
            int count = 0;
            result = new String[violation.size()];
            LOGGER.info("This Person is invalid:{}", person.getName());
            for (ConstraintViolation<Person> viol : violation) {

                result[count] = viol.getMessage();
                count++;
            }
        } else {
            result = new String[0];
            LOGGER.info("This Person is VALID! :{}", person.getName());
        }
        return result;
    }

    public void validateCsvFormat(String format){
        try {
        if(!isValidCsv(format)){
            throw new FileFormatException("Incorrect file format, File format must be csv") ;
        }
        }catch (FileFormatException e){
            LOGGER.error(e.getMessage(),e);
        }
    }

    public boolean isValidCsv(String format){
        return format.toLowerCase(Locale.ROOT).substring(format.lastIndexOf(".")).equals(".csv");
    }
}
