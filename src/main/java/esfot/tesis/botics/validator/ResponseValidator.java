package esfot.tesis.botics.validator;


import esfot.tesis.botics.payload.request.ResponseRequest;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

@Component
public class ResponseValidator implements Validator {
    @Override
    public boolean supports(Class<?> clazz) {
        return ResponseRequest.class.equals(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        ResponseRequest responseRequest = (ResponseRequest) target;
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "subject", "Response.fields.blank.subject");
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "details", "Response.fields.blank.details");
        if (responseRequest.getSubject().length() < 3 || responseRequest.getSubject().length() > 30) {
            errors.rejectValue("subject", "Response.fields.size.subject");
        }
    }
}
