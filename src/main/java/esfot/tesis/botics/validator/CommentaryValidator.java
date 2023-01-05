package esfot.tesis.botics.validator;


import esfot.tesis.botics.payload.request.CommentaryRequest;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

@Component
public class CommentaryValidator implements Validator{
    @Override
    public boolean supports(Class<?> clazz) {
        return CommentaryRequest.class.equals(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        CommentaryRequest commentaryRequest = (CommentaryRequest) target;
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "subject", "Commentary.fields.blank.subject");
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "message", "Commentary.fields.blank.message");
        if (commentaryRequest.getSubject().length() < 3 || commentaryRequest.getSubject().length() > 30) {
            errors.rejectValue("subject", "Commentary.fields.size.subject");
        }
    }
}
