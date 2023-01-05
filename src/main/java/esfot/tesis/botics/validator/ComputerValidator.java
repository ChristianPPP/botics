package esfot.tesis.botics.validator;

import esfot.tesis.botics.payload.request.ComputerRequest;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

@Component
public class ComputerValidator implements Validator {
    @Override
    public boolean supports(Class<?> clazz) {
        return ComputerRequest.class.equals(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        ComputerRequest computerRequest = (ComputerRequest) target;
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "hostName", "Computer.fields.blank.hostName");
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "model", "Computer.fields.blank.model");
        if (computerRequest.getHostName().length() < 3 || computerRequest.getHostName().length() > 30) {
            errors.rejectValue("hostName", "Computer.fields.size.hostName");
        }
        if (computerRequest.getSerialMonitor().length() < 3 || computerRequest.getSerialMonitor().length() > 30) {
            errors.rejectValue("serialMonitor", "Computer.fields.size.serialMonitor");
        }
        if (computerRequest.getSerialKeyboard().length() < 3 || computerRequest.getSerialKeyboard().length() > 30) {
            errors.rejectValue("serialKeyboard", "Computer.fields.size.serialKeyboard");
        }
        if (computerRequest.getSerialCpu().length() < 3 || computerRequest.getSerialCpu().length() > 30) {
            errors.rejectValue("serialCpu", "Computer.fields.size.serialCpu");
        }
        if (computerRequest.getCodeMonitor().length() < 3 || computerRequest.getCodeMonitor().length() > 30) {
            errors.rejectValue("codeMonitor", "Computer.fields.siz.codeMonitor");
        }
        if (computerRequest.getCodeKeyboard().length() < 3 || computerRequest.getCodeKeyboard().length() > 30) {
            errors.rejectValue("codeKeyboard", "Computer.fields.size.codeKeyboard");
        }
        if (computerRequest.getCodeCpu().length() < 3 || computerRequest.getCodeCpu().length() > 30) {
            errors.rejectValue("codeCpu", "Computer.fields.size.codeCpu");
        }
        if (computerRequest.getModel().length() < 3 || computerRequest.getModel().length() > 30) {
            errors.rejectValue("model", "Computer.fields.size.model");
        }
        if (computerRequest.getHardDrive().length() < 3 || computerRequest.getHardDrive().length() > 6) {
            errors.rejectValue("hardDrive", "Computer.hardDrive.ram.size");
        }
        if (computerRequest.getRam().length() < 3 || computerRequest.getRam().length() > 6) {
            errors.rejectValue("ram", "Computer.hardDrive.ram.size");
        }
        if (computerRequest.getProcessor().length() < 3 || computerRequest.getProcessor().length() > 30) {
            errors.rejectValue("processor", "Computer.fields.size.processor");
        }
        if (computerRequest.getOperativeSystem().length() < 3 || computerRequest.getOperativeSystem().length() > 30) {
            errors.rejectValue("operativeSystem", "Computer.fields.size.operativeSystem");
        }
        if (computerRequest.getDetails().length() < 3) {
            errors.rejectValue("details", "Computer.details.observations.size");
        }
        if (computerRequest.getObservations().length() < 3) {
            errors.rejectValue("observations", "Computer.details.observations.size");
        }
    }
}
