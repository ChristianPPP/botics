package esfot.tesis.botics.auth.controller;

import esfot.tesis.botics.auth.entity.RefreshToken;
import esfot.tesis.botics.auth.entity.Role;
import esfot.tesis.botics.auth.entity.User;
import esfot.tesis.botics.auth.entity.enums.ERole;
import esfot.tesis.botics.auth.payload.request.LoginRequest;
import esfot.tesis.botics.auth.payload.request.SignupRequest;
import esfot.tesis.botics.auth.payload.response.ErrorResponse;
import esfot.tesis.botics.auth.payload.response.MessageResponse;
import esfot.tesis.botics.auth.payload.response.UserInfoResponse;
import esfot.tesis.botics.auth.repository.RoleRepository;
import esfot.tesis.botics.auth.repository.UserRepository;
import esfot.tesis.botics.auth.security.jwt.JwtUtils;
import esfot.tesis.botics.auth.security.jwt.TokenRefreshException;
import esfot.tesis.botics.auth.security.service.RefreshTokenService;
import esfot.tesis.botics.auth.security.service.UserDetailsImpl;
import esfot.tesis.botics.auth.validator.SigninValidator;
import esfot.tesis.botics.auth.validator.SignupValidator;
import esfot.tesis.botics.payload.request.ProfileRequest;
import esfot.tesis.botics.validator.ProfileValidator;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final AuthenticationManager authenticationManager;

    private final UserRepository userRepository;

    private final RefreshTokenService refreshTokenService;

    private final RoleRepository roleRepository;

    private final PasswordEncoder encoder;

    private final JwtUtils jwtUtils;

    private final SignupValidator signupValidator;

    private final SigninValidator signinValidator;

    private final ProfileValidator profileValidator;


    @Autowired
    public AuthController(AuthenticationManager authenticationManager, UserRepository userRepository, RefreshTokenService refreshTokenService, RoleRepository roleRepository, PasswordEncoder encoder, JwtUtils jwtUtils, SignupValidator signupValidator, SigninValidator signinValidator, ProfileValidator profileValidator) {
        this.authenticationManager = authenticationManager;
        this.userRepository = userRepository;
        this.refreshTokenService = refreshTokenService;
        this.roleRepository = roleRepository;
        this.encoder = encoder;
        this.jwtUtils = jwtUtils;
        this.signupValidator = signupValidator;
        this.signinValidator = signinValidator;
        this.profileValidator = profileValidator;
    }

    @Operation(summary = "Endpoint para iniciar sesión.", description = "Se otorgan los detalles del usuario junto con un token de autorización el cual caduca cada 15 minutos, a su vez almacena el token de reinicio en la base de datos.")
    @ApiResponses(value= {
            @ApiResponse(responseCode = "200", description = "Se devolverá un objeto de autorización con el token.", content =
                    {@Content(mediaType = "application/json", schema = @Schema(implementation = UserInfoResponse.class))}),
            @ApiResponse(responseCode = "401", description = "Esta respuesta indica que las credenciales ingresadas son incorrectas."),
            @ApiResponse(responseCode = "400", description = "Esta respuesta indica errores en el formulario.", content =
                    {@Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))})
    })
    @PostMapping("/signin")
    public ResponseEntity<?> authenticateUser(@RequestBody LoginRequest loginRequest, BindingResult bindingResult) {
        List<String> errors = new ArrayList<>();
        ResourceBundleMessageSource resourceBundleMessageSource = new ResourceBundleMessageSource();
        resourceBundleMessageSource.setBasename("messages");
        signinValidator.validate(loginRequest, bindingResult);
        if (bindingResult.hasErrors()) {
            bindingResult.getAllErrors().forEach(e -> errors.add(resourceBundleMessageSource.getMessage(e, Locale.US)));
            return ResponseEntity.badRequest().body(new ErrorResponse("Error en el formulario.",errors));
        }
        if (loginRequest.getUsername().contains("@")) {
            User user = userRepository.findByEmail(loginRequest.getUsername());
            if (user == null) {
                authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));
            } else {
                loginRequest.setUsername(user.getUsername());
            }
        }
        Authentication authentication = authenticationManager
                .authenticate(new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        SecurityContextHolder.getContext().setAuthentication(authentication);

        ResponseCookie jwtCookie = jwtUtils.generateJwtCookie(userDetails);

        List<String> roles = userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());

        RefreshToken refreshToken = refreshTokenService.createRefreshToken(userDetails.getId());

        ResponseCookie jwtRefreshCookie = jwtUtils.generateRefreshJwtCookie(refreshToken.getToken());

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, jwtCookie.toString())
                .header(HttpHeaders.SET_COOKIE, jwtRefreshCookie.toString())
                .body(new UserInfoResponse(userDetails.getId(),
                        userDetails.getUsername(),
                        userDetails.getEmail(),
                        roles, jwtCookie.getValue(), jwtRefreshCookie.getValue()));
    }

    @Operation(summary = "Endpoint para registrarse.", description = "Los campos 'username' y 'email' son únicos y sólo se admite el registro para los roles 'admin', 'administrativo' y 'profesor'")
    @ApiResponses(value= {
            @ApiResponse(responseCode = "200", description = "Se devolverá un mensaje de registro exitoso.", content =
                    {@Content(mediaType = "application/json", schema = @Schema(implementation = MessageResponse.class))}),
            @ApiResponse(responseCode = "400", description = "Esta respuesta indica errores en el formulario o bien que el nombre de usuario o email ya se encuentran registrados.", content =
                    {@Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))})
    })
    @PostMapping(value = "/signup")
    public ResponseEntity<?> registerUser(@RequestBody SignupRequest signUpRequest, BindingResult bindingResult) {
        List<String> errors = new ArrayList<>();
        ResourceBundleMessageSource resourceBundleMessageSource = new ResourceBundleMessageSource();
        resourceBundleMessageSource.setBasename("messages");
        signupValidator.validate(signUpRequest, bindingResult);
        if (bindingResult.hasErrors()) {
            bindingResult.getAllErrors().forEach(e -> errors.add(resourceBundleMessageSource.getMessage(e, Locale.US)));
            return ResponseEntity.badRequest().body(new ErrorResponse("Errores en el formulario.",errors));
        }
        if (userRepository.existsByUsername(signUpRequest.getUsername())) {
            return ResponseEntity.badRequest().body(new MessageResponse("Error: Nombre de usuario ya registrado."));
        }

        if (userRepository.existsByEmail(signUpRequest.getEmail())) {
            return ResponseEntity.badRequest().body(new MessageResponse("Error: Email ya registrado."));
        }

        User user = new User(signUpRequest.getUsername(),
                signUpRequest.getEmail(),
                encoder.encode(signUpRequest.getPassword()), 0);

        Set<String> strRoles = signUpRequest.getRole();
        Set<Role> roles = new HashSet<>();

        strRoles.forEach(role -> {
            if ("admin".equals(role)) {
                Role userRole = roleRepository.findByName(ERole.ROLE_ADMIN).
                        orElseThrow(() -> new RuntimeException("Error: Rol no encontrado."));
                roles.add(userRole);
            }
            else if ("administrativo".equals(role)) {
                Role userRole = roleRepository.findByName(ERole.ROLE_ADMINISTRATIVO).
                        orElseThrow(() -> new RuntimeException("Error: Rol no encontrado."));
                roles.add(userRole);
            }
            else if ("profesor".equals(role)) {
                Role userRole = roleRepository.findByName(ERole.ROLE_PROFESOR).
                        orElseThrow(() -> new RuntimeException("Error: Rol no encontrado."));
                roles.add(userRole);
            }
            else {
                roles.clear();
            }
        });
        if (roles.isEmpty()) {
            return ResponseEntity.ok(new MessageResponse("El rol especificado no es válido."));
        } else {
            user.setRoles(roles);
            ProfileRequest profileRequest = new ProfileRequest(signUpRequest.getFirstName(), signUpRequest.getLastName(), 0);
            if (signUpRequest.getFirstName() == "" || signUpRequest.getLastName() == "") {
                return ResponseEntity.badRequest().body(new MessageResponse("Nombre o apellido no válidos."));
            }
            profileValidator.validate(profileRequest, bindingResult);
            if (bindingResult.hasErrors()) {
                bindingResult.getAllErrors().forEach(e -> errors.add(resourceBundleMessageSource.getMessage(e, Locale.US)));
                return ResponseEntity.badRequest().body(new ErrorResponse("Errores en el formulario.",errors));
            }
            user.setFirstName(signUpRequest.getFirstName());
            user.setLastName(signUpRequest.getLastName());
            user.setExtension(0);
            userRepository.save(user);
            return ResponseEntity.ok(new MessageResponse("Usuario registrado."));
        }
    }


    @Operation(summary = "Endpoint para el cirre de sesión.", description = "El cierre de sesión se realiza mediante el JWT token del header.")
    @ApiResponses(value= {
            @ApiResponse(responseCode = "200", description = "Se devolverá un mensaje de cierre de sesión exitoso.", content =
                    {@Content(mediaType = "application/json", schema = @Schema(implementation = MessageResponse.class))}),
            @ApiResponse(responseCode = "400", description = "Esta respuesta indica que no existe el inicio de sesión de un usuario.", content =
                    {@Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))})
    })
    @PostMapping("/signout")
    public ResponseEntity<?> logoutUser() {
        Object principle = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (!Objects.equals(principle.toString(), "anonymousUser")) {
            Long userId = ((UserDetailsImpl) principle).getId();
            refreshTokenService.deleteByUserId(userId);
            ResponseCookie jwtCookie = jwtUtils.getCleanJwtCookie();
            ResponseCookie jwtRefreshCookie = jwtUtils.getCleanJwtRefreshCookie();
            return ResponseEntity.ok()
                    .header(HttpHeaders.SET_COOKIE, jwtCookie.toString())
                    .header(HttpHeaders.SET_COOKIE, jwtRefreshCookie.toString())
                    .body(new MessageResponse("Cierre de sesión correcto."));
        }
        return ResponseEntity.badRequest().body(new MessageResponse("El token de autenticación está vacío."));
    }


    @Operation(summary = "Endpoint para reiniciar el token de autorización.", description = "Otorga un nuevo token de autorización.")
    @ApiResponses(value= {
            @ApiResponse(responseCode = "200", description = "Se devolverá los detalles del usuario con un nuevo JWT token.", content =
                    {@Content(mediaType = "application/json", schema = @Schema(implementation = UserInfoResponse.class))}),
            @ApiResponse(responseCode = "400", description = "Esta respuesta indica que el rol no es válido o bien que el token de reinicio no es válido.", content =
                    {@Content(mediaType = "application/json", schema = @Schema(implementation = MessageResponse.class))})
    })
    @PostMapping("/refreshtoken/{token}")
    public ResponseEntity<?> refreshtoken(@PathVariable("token") String token) {
        if ((token != null) && (token.length() > 0)) {
            if (!refreshTokenService.findByToken(token).isPresent()) {
                return ResponseEntity.badRequest().body(new MessageResponse("El token " + token + " no se encuentra en la base de datos."));
            }
            return refreshTokenService.findByToken(token)
                    .map(refreshTokenService::verifyExpiration)
                    .map(RefreshToken::getUser)
                    .map(user -> {
                        ResponseCookie jwtCookie = jwtUtils.generateJwtCookie(user);
                        ResponseCookie refreshCookie = jwtUtils.generateRefreshJwtCookie(token);
                        List<String> roles = new ArrayList<>();
                        user.getRoles().forEach(role -> {
                            if (role.getName() == ERole.ROLE_ADMIN) {
                                roles.add("ROLE_ADMIN");
                            }
                            if (role.getName() == ERole.ROLE_ADMINISTRATIVO) {
                                roles.add("ROLE_ADMINISTRATIVO");
                            }
                            if (role.getName() == ERole.ROLE_PROFESOR) {
                                roles.add("ROLE_PROFESOR");
                            }
                            if (role.getName() == ERole.ROLE_PASANTE) {
                                roles.add("ROLE_PASANTE");
                            } else {
                                roles.clear();
                            }
                        });
                        if (roles.isEmpty()) {
                            ResponseEntity.badRequest().body(new MessageResponse("Rol no válido."));
                        }
                        return ResponseEntity.ok()
                                .header(HttpHeaders.SET_COOKIE, jwtCookie.toString())
                                .header(HttpHeaders.SET_COOKIE, refreshCookie.toString())
                                .body(new UserInfoResponse(user.getId(),
                                        user.getUsername(),
                                        user.getEmail(),
                                        roles, jwtCookie.getValue(), refreshCookie.getValue()));
                    })
                    .orElseThrow(() ->
                        new TokenRefreshException(token,
                                "El token de reinicio no se encuentra en la base de datos.")
                    );
        }
        return ResponseEntity.badRequest().body(new MessageResponse("Token de reinicio vacío."));
    }
}