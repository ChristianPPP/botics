package esfot.tesis.botics.auth.validator;

import esfot.tesis.botics.auth.payload.request.ForgotPasswordRequest;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import java.util.regex.Pattern;

@Component
public class ForgotPasswordValidator implements Validator {
    @Override
    public boolean supports(Class<?> clazz) {
        return ForgotPasswordRequest.class.equals(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        ForgotPasswordRequest forgotPasswordRequest = (ForgotPasswordRequest) target;
        String regexEmailPattern = "^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$";
        if (!Pattern.compile(regexEmailPattern, Pattern.CASE_INSENSITIVE).matcher(forgotPasswordRequest.getEmail()).matches()) {
            errors.rejectValue(null, "NotValid.email");
        }
    }
}
