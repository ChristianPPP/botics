package esfot.tesis.botics.auth.validator;

import esfot.tesis.botics.auth.payload.request.ResetPasswordRequest;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import java.util.regex.Pattern;

@Component
public class ResetPasswordValidator implements Validator {
    @Override
    public boolean supports(Class<?> clazz) {
        return ResetPasswordRequest.class.equals(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        ResetPasswordRequest resetPasswordRequest = (ResetPasswordRequest) target;
        String regexPasswordPattern = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[!@#&()–[{}]:;',?/*~$^+=<>]).{8,20}$";
        if (!Pattern.compile(regexPasswordPattern).matcher(resetPasswordRequest.getPassword()).matches()) {
            errors.rejectValue(null, "NotValid.password");
        }
        if (!Pattern.compile(regexPasswordPattern).matcher(resetPasswordRequest.getConfirmPassword()).matches()) {
            errors.rejectValue(null, "NotValid.password");
        }
    }
}
