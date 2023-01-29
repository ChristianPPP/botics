package esfot.tesis.botics.auth.controller;

import esfot.tesis.botics.auth.entity.User;
import esfot.tesis.botics.auth.payload.request.ForgotPasswordRequest;
import esfot.tesis.botics.auth.payload.request.ResetPasswordRequest;
import esfot.tesis.botics.auth.payload.response.ErrorResponse;
import esfot.tesis.botics.auth.payload.response.MessageResponse;
import esfot.tesis.botics.auth.security.service.UserDetailsServiceImpl;
import esfot.tesis.botics.auth.validator.ForgotPasswordValidator;
import esfot.tesis.botics.auth.validator.ResetPasswordValidator;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
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
@RequestMapping("/api/v1/auth/password")
public class ForgotPasswordController {

    private final JavaMailSender mailSender;

    private final UserDetailsServiceImpl userService;

    private final ForgotPasswordValidator forgotPasswordValidator;

    private final ResetPasswordValidator resetPasswordValidator;

    @Autowired
    public ForgotPasswordController(JavaMailSender mailSender, UserDetailsServiceImpl userService, ForgotPasswordValidator forgotPasswordValidator, ResetPasswordValidator resetPasswordValidator) {
        this.mailSender = mailSender;
        this.userService = userService;
        this.forgotPasswordValidator = forgotPasswordValidator;
        this.resetPasswordValidator = resetPasswordValidator;
    }


    @Operation(summary = "Endpoint para el envío del correo para restablecer contraseña.", description = "Se enviará al correo electrónico especificado un correo electrónico con las intrucciones para restablecer la contraseña en caso de haberla olvidado, a su vez se genera un token de restablecimiento de contraseña en la base de datos.")
    @ApiResponses(value= {
            @ApiResponse(responseCode = "200", description = "Se devolverá un mensaje que indica que se ha enviado el correo de forma existosa.", content =
                    {@Content(mediaType = "application/json", schema = @Schema(implementation = MessageResponse.class))}),
            @ApiResponse(responseCode = "400", description = "Esta respuesta indica errores en el formulario o que el correo electrónico no ha sido encontrado.", content =
                    {@Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))}),
            @ApiResponse(responseCode = "500", description = "Esta respuesta indica un error al enviar el correo de restablecimiento.", content =
                    {@Content(mediaType = "application/json", schema = @Schema(implementation = MessageResponse.class))})
    })
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
        String frontUrl = "http://localhost:4200/auth/nueva-contraseña";
        try {
            if (Objects.equals(userService.updateResetPasswordToken(token, forgotPasswordRequest.getEmail()), "Correo electrónico no encontrado.")) {
                return ResponseEntity.badRequest().body(new MessageResponse("El correo electrónico: " + forgotPasswordRequest.getEmail() + " no se encuentra registrado."));
            } else {
                String resetPasswordLink = frontUrl + "?token=" + token;
                sendEmail(forgotPasswordRequest.getEmail(), resetPasswordLink);
                return ResponseEntity.ok().body(new MessageResponse("Se ha enviado un enlace para el restablecimiento de la contraseña al correo electrónico: " + forgotPasswordRequest.getEmail()));
            }
        } catch (UnsupportedEncodingException | MessagingException e) {
            return ResponseEntity.internalServerError().body(new MessageResponse("Error al enviar el correo."));
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



    @Operation(summary = "Endpoint para restablecer contraseña.", description = "Se restablece la contraseña enviándose el token generado en el mensaje de correo electrónico y adicionalmente se elimina el token de restablecimiento de la base de datos.")
    @ApiResponses(value= {
            @ApiResponse(responseCode = "200", description = "Se devolverá un mensaje que indica que la contraseña ha sido restablecida exitosamente.", content =
                    {@Content(mediaType = "application/json", schema = @Schema(implementation = MessageResponse.class))}),
            @ApiResponse(responseCode = "400", description = "Esta respuesta indica errores en el formulario o bien que el token de reinicio no es válido o que las contraseñas no coinciden.", content =
                    {@Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))})
    })
    @PostMapping("/reset")
    public ResponseEntity<?> resetPassword(@RequestBody ResetPasswordRequest resetPasswordRequest, @RequestParam("token") String token, BindingResult bindingResult) {
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
            return ResponseEntity.badRequest().body(new ErrorResponse("Errores en el formulario.",errors));
        }
        User user = userService.getByResetPasswordToken(token);
        if (user == null) {
            return ResponseEntity.badRequest().body(new MessageResponse("Token de reinicio no válido."));
        } else if (!Objects.equals(resetPasswordRequest.getPassword(), resetPasswordRequest.getConfirmPassword())) {
            return ResponseEntity.badRequest().body(new MessageResponse("Las contraseñas ingresadas no coinciden."));
        } else {
            userService.resetPassword(user, resetPasswordRequest.getPassword());
            return ResponseEntity.ok().body(new MessageResponse("Contraseña restablecida."));
        }
    }
}
