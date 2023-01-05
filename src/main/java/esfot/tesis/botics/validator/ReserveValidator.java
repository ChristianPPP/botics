package esfot.tesis.botics.validator;


import esfot.tesis.botics.payload.request.ReserveRequest;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

@Component
public class ReserveValidator implements Validator {
    @Override
    public boolean supports(Class<?> clazz) {
        return ReserveRequest.class.equals(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        ReserveRequest reserveRequest = (ReserveRequest) target;
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "labName", "Reserve.fields.blank.labName");
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "description", "Reserve.fields.blank.description");
        if (reserveRequest.getLabName().length() < 3 || reserveRequest.getLabName().length() > 30) {
            errors.rejectValue("labName", "Reserve.fields.size.labName");
        }
    }
}
