package esfot.tesis.botics.validator;

import esfot.tesis.botics.payload.request.ProfileRequest;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import java.util.Objects;
import java.util.regex.Pattern;

@Component
public class ProfileValidator implements Validator {
    @Override
    public boolean supports(Class<?> clazz) {
        return ProfileRequest.class.equals(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        ProfileRequest profileRequest = (ProfileRequest) target;
        String regexNamePattern = "^[A-Z][-a-zA-Z]+$";
        if (!Pattern.compile(regexNamePattern, Pattern.CASE_INSENSITIVE).matcher(profileRequest.getFirstName()).matches() && !Objects.equals(profileRequest.getFirstName(), "")) {
            errors.rejectValue(null, "NotValid.firstName");
        }
        if ((profileRequest.getFirstName().length() < 3 || profileRequest.getFirstName().length() > 20) && !Objects.equals(profileRequest.getFirstName(), "")) {
            errors.rejectValue(null, "Size.profileRequest.firstName");
        }
        if (!Pattern.compile(regexNamePattern, Pattern.CASE_INSENSITIVE).matcher(profileRequest.getLastName()).matches() && !Objects.equals(profileRequest.getLastName(), "")) {
            errors.rejectValue(null, "NotValid.lastName");
        }
        if ((profileRequest.getLastName().length() < 3 || profileRequest.getLastName().length() > 20) && !Objects.equals(profileRequest.getLastName(), "")) {
            errors.rejectValue(null, "Size.profileRequest.lastName");
        }
        if (profileRequest.getExtension().toString().length() != 4 && profileRequest.getExtension() != 0) {
            errors.rejectValue(null, "Size.profileRequest.extension");
        }
    }
}
