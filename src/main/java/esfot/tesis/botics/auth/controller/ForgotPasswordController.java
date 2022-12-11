package esfot.tesis.botics.auth.controller;

import esfot.tesis.botics.auth.entity.User;
import esfot.tesis.botics.auth.payload.request.ForgotPasswordRequest;
import esfot.tesis.botics.auth.payload.request.ResetPasswordRequest;
import esfot.tesis.botics.auth.payload.response.ErrorResponse;
import esfot.tesis.botics.auth.payload.response.MessageResponse;
import esfot.tesis.botics.auth.security.service.UserDetailsServiceImpl;
import esfot.tesis.botics.auth.validator.ForgotPasswordValidator;
import esfot.tesis.botics.auth.validator.ResetPasswordValidator;
import net.bytebuddy.utility.RandomString;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import javax.servlet.http.HttpServletRequest;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/auth/password")
public class ForgotPasswordController {
    @Autowired
    JavaMailSender mailSender;

    @Autowired
    UserDetailsServiceImpl userService;

    @Autowired
    ForgotPasswordValidator forgotPasswordValidator;

    @Autowired
    ResetPasswordValidator resetPasswordValidator;

    @PostMapping(value = "/forgot")
    public ResponseEntity<?> forgotPassword(HttpServletRequest request, @RequestBody ForgotPasswordRequest forgotPasswordRequest, BindingResult bindingResult) {
        List<String> errors = new ArrayList<>();
        if (forgotPasswordRequest.getEmail() == null) {
            forgotPasswordRequest.setEmail("");
        } else {
            forgotPasswordRequest.setEmail(forgotPasswordRequest.getEmail());
        }
        forgotPasswordValidator.validate(forgotPasswordRequest, bindingResult);
        ResourceBundleMessageSource resourceBundleMessageSource = new ResourceBundleMessageSource();
        resourceBundleMessageSource.setBasename("messages");
        if (bindingResult.hasErrors()) {
            bindingResult.getAllErrors().forEach(e -> errors.add(resourceBundleMessageSource.getMessage(e, Locale.US)));
            return ResponseEntity.badRequest().body(new ErrorResponse("Error en el formulario.",errors));
        }
        String token = RandomString.make(30);
        String baseUrl = ServletUriComponentsBuilder
                .fromRequestUri(request)
                .replacePath(null)
                .build()
                .toUriString();
        try {
            if (Objects.equals(userService.updateResetPasswordToken(token, forgotPasswordRequest.getEmail()), "Email not found.")) {
                return ResponseEntity.internalServerError().body(new MessageResponse("Email not found."));
            } else {
                String resetPasswordLink = baseUrl + "/api/auth/password/reset?token=" + token;
                sendEmail(forgotPasswordRequest.getEmail(), resetPasswordLink);
                return ResponseEntity.ok().body(new MessageResponse("We have sent a reset password link to your email."));
            }
        } catch (UnsupportedEncodingException | MessagingException e) {
            return ResponseEntity.internalServerError().body(new MessageResponse("Error while sending email."));
        }
    }

    public void sendEmail(String recipientEmail, String link) throws MessagingException, UnsupportedEncodingException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message);

        helper.setFrom("botics@esfot.epn.edu.ec", "Botics suport");
        helper.setTo(recipientEmail);

        String subject = "Link to reset your password";

        String content = "<p>Hola,</p>"
                +"<p>Has realizado una petición para resetear tu contraseña.</p>"
                +"<p>Has click en el link dejabo para cambiar tu contraseña:</p>"
                +"<p><a href=\"" + link + "\">Cambiar mi contraseña</a></p>"
                +"<br>"
                +"<p>Ignore este mensaje en dado caso que recuerde su contraseña, "
                +"o si no realizó esta petición.</p>";
        helper.setSubject(subject);
        helper.setText(content, true);
        mailSender.send(message);
    }

    @PostMapping("/reset")
    public ResponseEntity<?> resetPassword(@RequestBody ResetPasswordRequest resetPasswordRequest /*@Nullable @RequestPart("password") String password, @Nullable @RequestPart("confirmPassword") String confirmPassword*/, @RequestParam("token") String token, BindingResult bindingResult) {
        if (resetPasswordRequest.getPassword() == null || resetPasswordRequest.getConfirmPassword() == null) {
            resetPasswordRequest.setPassword("");
            resetPasswordRequest.setConfirmPassword("");
        }
        resetPasswordValidator.validate(resetPasswordRequest, bindingResult);
        List<String> errors = new ArrayList<>();
        ResourceBundleMessageSource resourceBundleMessageSource = new ResourceBundleMessageSource();
        resourceBundleMessageSource.setBasename("messages");
        if (bindingResult.hasErrors()) {
            bindingResult.getAllErrors().forEach(e -> errors.add(resourceBundleMessageSource.getMessage(e, Locale.US)));
            return ResponseEntity.badRequest().body(new ErrorResponse("Form error.",errors));
        }
        User user = userService.getByResetPasswordToken(token);
        if (user == null) {
            return ResponseEntity.badRequest().body(new MessageResponse("Invalid reset token."));
        } else if (!Objects.equals(resetPasswordRequest.getPassword(), resetPasswordRequest.getConfirmPassword())) {
            return ResponseEntity.badRequest().body(new MessageResponse("No coincident passwords."));
        } else {
            userService.resetPassword(user, resetPasswordRequest.getPassword());
            return ResponseEntity.ok().body(new MessageResponse("You have successfully changed your password."));
        }
    }
}
