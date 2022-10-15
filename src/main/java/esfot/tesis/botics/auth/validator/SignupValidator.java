package esfot.tesis.botics.auth.validator;

import esfot.tesis.botics.auth.payload.request.SignupRequest;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

import java.util.regex.Pattern;

@Component
public class SignupValidator implements Validator {
    @Override
    public boolean supports(Class<?> clazz) {
        return SignupRequest.class.equals(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        SignupRequest signupRequest = (SignupRequest) target;
        String regexEmailPattern = "^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$";
        String regexPasswordPattern = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[!@#&()–[{}]:;',?/*~$^+=<>]).{8,20}$";
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "username", "NotEmpty.username");
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "email", "NotEmpty.email");
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "password", "NotEmpty.password");
        if (!Pattern.compile(regexEmailPattern, Pattern.CASE_INSENSITIVE).matcher(signupRequest.getEmail()).matches()) {
            errors.rejectValue("email", "NotValid.email");
        }
        if (signupRequest.getUsername().length() < 3 || signupRequest.getUsername().length() > 20) {
            errors.rejectValue("username", "Size.signupRequest.username");
        }
        if (!Pattern.compile(regexPasswordPattern, Pattern.CASE_INSENSITIVE).matcher(signupRequest.getPassword()).matches()) {
            errors.rejectValue("password", "NotValid.password");
        }
    }
}
