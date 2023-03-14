package my.group;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class CheckAValidator implements ConstraintValidator<CheckA, String> {

    public boolean isValid(String obj, ConstraintValidatorContext context) {

        return obj.contains("a");
    }
}
