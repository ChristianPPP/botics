package esfot.tesis.botics.auth.validator;

import esfot.tesis.botics.auth.payload.request.LoginRequest;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

@Component
public class SigninValidator implements Validator {
    @Override
    public boolean supports(Class<?> clazz) {
        return LoginRequest.class.equals(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        LoginRequest loginRequest = (LoginRequest) target; 
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "username", "NotEmpty.username");
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "password", "NotEmpty.password");
    }
}
