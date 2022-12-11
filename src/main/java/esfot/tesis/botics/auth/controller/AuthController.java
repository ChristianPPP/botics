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

import javax.servlet.http.HttpServletRequest;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/auth")
public class AuthController {
    @Autowired
    AuthenticationManager authenticationManager;

    @Autowired
    UserRepository userRepository;

    @Autowired
    RefreshTokenService refreshTokenService;

    @Autowired
    RoleRepository roleRepository;

    @Autowired
    PasswordEncoder encoder;

    @Autowired
    JwtUtils jwtUtils;

    @Autowired
    SignupValidator signupValidator;

    @Autowired
    SigninValidator signinValidator;

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
        if (refreshTokenService.getByUserId(userDetails.getId()) == null) {
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
                            roles, jwtCookie.toString()));
        } else {
            return ResponseEntity.badRequest().body(new MessageResponse("User already autenticated."));
        }
    }

    @PostMapping(value = "/signup")
    public ResponseEntity<?> registerUser(@RequestBody SignupRequest signUpRequest, BindingResult bindingResult) {
        List<String> errors = new ArrayList<>();
        ResourceBundleMessageSource resourceBundleMessageSource = new ResourceBundleMessageSource();
        resourceBundleMessageSource.setBasename("messages");
        signupValidator.validate(signUpRequest, bindingResult);
        if (bindingResult.hasErrors()) {
            bindingResult.getAllErrors().forEach(e -> errors.add(resourceBundleMessageSource.getMessage(e, Locale.US)));
            return ResponseEntity.badRequest().body(new ErrorResponse("Form error.",errors));
        }

        if (userRepository.existsByUsername(signUpRequest.getUsername())) {
            return ResponseEntity.badRequest().body(new MessageResponse("Error: Username is already taken!"));
        }

        if (userRepository.existsByEmail(signUpRequest.getEmail())) {
            return ResponseEntity.badRequest().body(new MessageResponse("Error: Email is already in use!"));
        }

        User user = new User(signUpRequest.getUsername(),
                signUpRequest.getEmail(),
                encoder.encode(signUpRequest.getPassword()), 0);

        Set<String> strRoles = signUpRequest.getRole();
        Set<Role> roles = new HashSet<>();

        strRoles.forEach(role -> {
            if ("admin".equals(role)) {
                Role userRole = roleRepository.findByName(ERole.ROLE_ADMIN).
                        orElseThrow(() -> new RuntimeException("Error: Role not found."));
                roles.add(userRole);
            }
            if ("administrativo".equals(role)) {
                Role userRole = roleRepository.findByName(ERole.ROLE_ADMINISTRATIVO).
                        orElseThrow(() -> new RuntimeException("Error: Role not found."));
                roles.add(userRole);
            }
            if ("profesor".equals(role)) {
                Role userRole = roleRepository.findByName(ERole.ROLE_PROFESOR).
                        orElseThrow(() -> new RuntimeException("Error: Role not found."));
                roles.add(userRole);
            }
        });

        user.setRoles(roles);
        userRepository.save(user);

        return ResponseEntity.ok(new MessageResponse("User registered successfully!"));
    }

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
                    .body(new MessageResponse("You've been signed out!"));
        }
        return ResponseEntity.ok().body(new MessageResponse("No login user"));
    }

    @PostMapping("/refreshtoken")
    public ResponseEntity<?> refreshtoken(HttpServletRequest request) {
        String refreshToken = jwtUtils.getJwtRefreshFromCookies(request);

        if ((refreshToken != null) && (refreshToken.length() > 0)) {
            return refreshTokenService.findByToken(refreshToken)
                    .map(refreshTokenService::verifyExpiration)
                    .map(RefreshToken::getUser)
                    .map(user -> {
                        ResponseCookie jwtCookie = jwtUtils.generateJwtCookie(user);
                        ResponseCookie refreshCookie = ResponseCookie
                                .from("refreshToken", refreshToken)
                                .path("/api")
                                .httpOnly(true)
                                .build();
                        return ResponseEntity.ok()
                                .header(HttpHeaders.SET_COOKIE, jwtCookie.toString())
                                .header(HttpHeaders.SET_COOKIE, refreshCookie.toString())
                                .body(new MessageResponse("Token is refreshed successfully!"));
                    })
                    .orElseThrow(() -> new TokenRefreshException(refreshToken,
                            "Refresh token is not in database!"));
        }
        return ResponseEntity.badRequest().body(new MessageResponse("Refresh Token is empty!"));
    }
}