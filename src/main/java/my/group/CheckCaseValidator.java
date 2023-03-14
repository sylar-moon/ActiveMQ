package my.group;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.lang.annotation.Annotation;

public class CheckCaseValidator implements ConstraintValidator<CheckA, String> {
    public boolean isValid(String obj, ConstraintValidatorContext context) {
        return false;
    }
}
