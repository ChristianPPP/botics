package esfot.tesis.botics.controller;

import esfot.tesis.botics.auth.entity.User;
import esfot.tesis.botics.auth.payload.response.ErrorResponse;
import esfot.tesis.botics.auth.payload.response.MessageResponse;
import esfot.tesis.botics.auth.repository.UserRepository;
import esfot.tesis.botics.entity.Avatar;
import esfot.tesis.botics.payload.request.ProfileRequest;
import esfot.tesis.botics.service.AvatarServiceImpl;
import esfot.tesis.botics.service.UserServiceImpl;
import esfot.tesis.botics.validator.ProfileValidator;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@Slf4j
@RequestMapping("/api/v1/profile")
public class ProfileController {

    private final UserRepository userService;

    private final UserServiceImpl userServiceImpl;

    private final AvatarServiceImpl avatarService;

    private final ProfileValidator profileValidator;

    @Autowired
    public ProfileController(UserRepository userService, UserServiceImpl userServiceImpl, AvatarServiceImpl avatarService, ProfileValidator profileValidator) {
        this.userService = userService;
        this.userServiceImpl = userServiceImpl;
        this.avatarService = avatarService;
        this.profileValidator = profileValidator;
    }


    @Operation(summary = "Endpoint para obtener el perfil de usuario.", description = "Se retorna el perfil de usuario autenticado mendiante el JWT token.")
    @ApiResponses(value= {
            @ApiResponse(responseCode = "200", description = "Se devolverá un objeto que contiene la información del perfil de usuario.", content =
                    {@Content(mediaType = "application/json", schema = @Schema(implementation = User.class))}),
            @ApiResponse(responseCode = "401", description = "Esta respuesta indica que se requiere iniciar sesión para acceder al perfil de usuario.", content =
                    {@Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))})
    })
    @GetMapping("/")
    public ResponseEntity<?> show(Authentication authentication) {
        User user = userServiceImpl.getUser(authentication.getName());
        return ResponseEntity.ok().body(user);
    }


    @Operation(summary = "Endpoint para actualizar la información del perfil de usuario.", description = "Se actualizan los campos de primer nombre, apellido y extensión; la extensión es 0 si se es un usuario con rol 'pasante'.")
    @ApiResponses(value= {
            @ApiResponse(responseCode = "200", description = "Se devolverá un mensaje que indica que el perfil ha sido actualizado exitosamente o bien que no existen cambios en los campos.", content =
                    {@Content(mediaType = "application/json", schema = @Schema(implementation = MessageResponse.class))}),
            @ApiResponse(responseCode = "401", description = "Esta respuesta indica que existen errores en el formulario.", content =
                    {@Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))})
    })
    @PostMapping(value = "/update/info")
    public ResponseEntity<?> updateUserInfo(Authentication authentication, @RequestBody ProfileRequest profileRequest, BindingResult bindingResult) {
        if (profileRequest.getFirstName() == null || (Objects.equals(profileRequest.getFirstName(), "") ) && (profileRequest.getLastName() == null || Objects.equals(profileRequest.getLastName(), "")) && (profileRequest.getExtension() == null || profileRequest.getExtension() == 0)) {
            return ResponseEntity.ok().body(new MessageResponse("No hay campos que actualizar."));
        } else {
            List<String> errors = new ArrayList<>();
            ResourceBundleMessageSource resourceBundleMessageSource = new ResourceBundleMessageSource();
            resourceBundleMessageSource.setBasename("messages");
            if (profileRequest.getFirstName() == null) {
                profileRequest.setFirstName("");
            }
            if (profileRequest.getLastName() == null) {
                profileRequest.setLastName("");
            }
            if (profileRequest.getExtension() == null) {
                profileRequest.setExtension(0);
            }
            profileValidator.validate(profileRequest, bindingResult);
            if (bindingResult.hasErrors()) {
                bindingResult.getAllErrors().forEach(e -> errors.add(resourceBundleMessageSource.getMessage(e, Locale.US)));
                return ResponseEntity.badRequest().body(new ErrorResponse("Errores en el formulario.",errors));
            }
            User user = userServiceImpl.getUser(authentication.getName());
            user.setFirstName(profileRequest.getFirstName());
            user.setLastName(profileRequest.getLastName());
            user.setExtension(profileRequest.getExtension());
            userService.save(user);
            return ResponseEntity.ok().body(new MessageResponse("Perfil actualizado."));
        }
    }


    @Operation(summary = "Endpoint para actualizar la foto del perfil de usuario.", description = "Admite imágenes de un máximo de 1MB de tamaño con extensión .jpeg o .png")
    @ApiResponses(value= {
            @ApiResponse(responseCode = "200", description = "Se devolverá un mensaje que indica que la foto de perfil ha sido actualizado exitosamente.", content =
                    {@Content(mediaType = "application/json", schema = @Schema(implementation = MessageResponse.class))}),
            @ApiResponse(responseCode = "400", description = "Esta respuesta indica que el archivo subido no es válido.", content =
                    {@Content(mediaType = "application/json", schema = @Schema(implementation = MessageResponse.class))})
    })
    @PutMapping(value = "/update/avatar", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public  ResponseEntity<?> updateAvatar(Authentication authentication, @RequestPart("avatar") MultipartFile multipartFile) {
        User user = userServiceImpl.getUser(authentication.getName());
        if (!Objects.equals(multipartFile.getContentType(), "image/jpeg") && !Objects.equals(multipartFile.getContentType(), "image/png")) {
            return ResponseEntity.badRequest().body(new MessageResponse("Tipo de archivo no admitido, debe ser una imagen de máximo 1MB de tamaño con extensión .jpeg o .png."));
        } else {
            String url = avatarService.saveFileToCloudinary(multipartFile);
            Avatar avatar = new Avatar(multipartFile.getName(), multipartFile.getContentType(), url);
            avatarService.save(avatar);
            user.setAvatar(avatar);
            userService.save(user);
            return ResponseEntity.ok().body(new MessageResponse("Foto de perfil actualizada."));
        }
    }
}
