package esfot.tesis.botics.controller;


import esfot.tesis.botics.auth.entity.Role;
import esfot.tesis.botics.auth.entity.User;
import esfot.tesis.botics.auth.entity.enums.ERole;
import esfot.tesis.botics.auth.payload.request.SignupRequest;
import esfot.tesis.botics.auth.payload.response.ErrorResponse;
import esfot.tesis.botics.auth.payload.response.MessageResponse;
import esfot.tesis.botics.auth.repository.RoleRepository;
import esfot.tesis.botics.auth.repository.UserRepository;
import esfot.tesis.botics.auth.validator.SignupValidator;
import esfot.tesis.botics.entity.enums.ELab;
import esfot.tesis.botics.service.LabServiceImpl;
import esfot.tesis.botics.service.UserServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("api/v1/admin")
public class AdminController {
    @Autowired
    UserServiceImpl userService;

    @Autowired
    SignupValidator signupValidator;

    @Autowired
    UserRepository userRepository;

    @Autowired
    PasswordEncoder encoder;

    @Autowired
    RoleRepository roleRepository;

    @Autowired
    LabServiceImpl labService;

    @GetMapping("/interns")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> index() {
        return ResponseEntity.ok().body(userService.getAllInternUsers());
    }

    @GetMapping("/intern/{name}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> show(@PathVariable("name") String name) {
        return ResponseEntity.ok().body(userService.getInternUser(name));
    }

    @PostMapping("/intern/save")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> save(@RequestBody SignupRequest signUpRequest, BindingResult bindingResult) {
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

        // Create new user's account
        User user = new User(signUpRequest.getUsername(),
                signUpRequest.getEmail(),
                encoder.encode(signUpRequest.getPassword()), 0);

        Set<String> strRoles = signUpRequest.getRole();
        Set<Role> roles = new HashSet<>();

        strRoles.forEach(role -> {
            if ("pasante".equals(role)) {
                Role userRole = roleRepository.findByName(ERole.ROLE_PASANTE).
                        orElseThrow(() -> new RuntimeException("Error: Role not found."));
                roles.add(userRole);
            }
        });

        user.setRoles(roles);
        userRepository.save(user);

        return ResponseEntity.ok(new MessageResponse("User registered successfully!"));
    }
    
    @GetMapping("/intern/disable/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> disable(@PathVariable("id") Long id) {
        User user = userService.getUserById(id);
        if (user == null) {
            return ResponseEntity.badRequest().body(new MessageResponse("Intern not found."));
        }
        user.setState(false);
        userRepository.save(user);
        return ResponseEntity.ok().body(new MessageResponse("Intern disable successfully."));
    }

    @GetMapping("/intern/enable/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> enable(@PathVariable("id") Long id) {
        User user = userService.getUserById(id);
        if (user == null) {
            return ResponseEntity.badRequest().body(new MessageResponse("Intern not found."));
        }
        user.setState(true);
        userRepository.save(user);
        return ResponseEntity.ok().body(new MessageResponse("Intern enable successfully."));
    }

    @GetMapping("/labs")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> indexLabs() {
        return ResponseEntity.ok().body(labService.getAll());
    }

    @GetMapping("/lab/{name}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> showLab(@PathVariable("name") String name) {
        if ("smd".equals(name)) {
            return ResponseEntity.ok().body(labService.getLabByName(ELab.LAB_SMD));
        }
        if ("14".equals(name)) {
            return ResponseEntity.ok().body(labService.getLabByName(ELab.LAB_ET14));
        }
        if ("15".equals(name)) {
            return ResponseEntity.ok().body(labService.getLabByName(ELab.LAB_ET15));
        }
        if ("16".equals(name)) {
            return ResponseEntity.ok().body(labService.getLabByName(ELab.LAB_ET16));
        }
        if ("22A".equals(name)) {
            return ResponseEntity.ok().body(labService.getLabByName(ELab.LAB_ET22A));
        }
        if ("22B".equals(name)) {
            return ResponseEntity.ok().body(labService.getLabByName(ELab.LAB_ET22B));
        }
        return ResponseEntity.badRequest().body(new MessageResponse("Lab not found."));
    }
}