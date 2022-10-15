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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.Nullable;
import org.springframework.security.core.Authentication;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/v1/profile")
public class ProfileController {
    @Autowired
    UserRepository userService;

    @Autowired
    UserServiceImpl userServiceImpl;

    @Autowired
    AvatarServiceImpl avatarService;

    @Autowired
    ProfileValidator profileValidator;


    @GetMapping("/")
    public ResponseEntity<?> show(Authentication authentication) {
        User user = userServiceImpl.getUser(authentication.getName());
        return ResponseEntity.ok().body(user);
    }

    @PostMapping(value = "/update", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> update(@Nullable @RequestPart("firstName") String firstName, @Nullable @RequestPart("lastName") String lastName,
                                    @Nullable @RequestParam("extension") Integer extension, BindingResult bindingResult,
                                    Authentication authentication, @Nullable @RequestPart("avatar") MultipartFile multipartFile) {
        if (firstName == null) {
            firstName = "";
        }
        if (lastName == null) {
            lastName = "";
        }
        if (extension == null) {
            extension = 0;
        }
        ProfileRequest profileRequest = new ProfileRequest();
        profileRequest.setFirstName(firstName);
        profileRequest.setLastName(lastName);
        profileRequest.setExtension(extension);
        List<String> errors = new ArrayList<>();
        ResourceBundleMessageSource resourceBundleMessageSource = new ResourceBundleMessageSource();
        resourceBundleMessageSource.setBasename("messages");
        profileValidator.validate(profileRequest, bindingResult);
        if (bindingResult.hasErrors()) {
            bindingResult.getAllErrors().forEach(e -> errors.add(resourceBundleMessageSource.getMessage(e, Locale.US)));
            return ResponseEntity.badRequest().body(new ErrorResponse("Form error.",errors));
        }
        User user = userServiceImpl.getUser(authentication.getName());
        if (multipartFile == null) {
            user.setFirstName(firstName);
            user.setLastName(lastName);
            user.setExtension(extension);
            user.setAvatar(null);
        } else {
            String url = avatarService.saveFileToCloudinary(multipartFile);
            Avatar avatar = new Avatar(multipartFile.getName(), multipartFile.getContentType(), url);
            user.setFirstName(firstName);
            user.setLastName(lastName);
            user.setExtension(extension);
            avatarService.save(avatar);
            user.setAvatar(avatar);
        }
        userService.save(user);
        return ResponseEntity.ok().body(new MessageResponse("Profile updated successfully."));
    }

    @DeleteMapping("/delete")
    public ResponseEntity<?> delete(Authentication authentication) {
        User user = userServiceImpl.getUser(authentication.getName());
        userService.delete(user);
        return ResponseEntity.ok().body(new MessageResponse(("User account deleted successfully")));
    }
}
