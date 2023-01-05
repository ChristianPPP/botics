package esfot.tesis.botics.validator;


import esfot.tesis.botics.payload.request.TicketRequest;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

@Component
public class TicketValidator implements Validator {
    @Override
    public boolean supports(Class<?> clazz) {
        return TicketRequest.class.equals(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        TicketRequest ticketRequest = (TicketRequest) target;
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "subject", "Ticket.fields.blank.subject");
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "description", "Ticket.fields.blank.description");
        if (ticketRequest.getSubject().length() < 3 || ticketRequest.getSubject().length() > 30) {
            errors.rejectValue("subject", "Ticket.fields.size.subject");
        }
    }
}
