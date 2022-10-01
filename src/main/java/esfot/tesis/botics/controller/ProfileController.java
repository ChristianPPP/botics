package esfot.tesis.botics.controller;

import esfot.tesis.botics.auth.entity.User;
import esfot.tesis.botics.auth.payload.response.MessageResponse;
import esfot.tesis.botics.auth.repository.UserRepository;
import esfot.tesis.botics.entity.Avatar;
import esfot.tesis.botics.service.AvatarServiceImpl;
import esfot.tesis.botics.service.UserServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;

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

    @GetMapping("/")
    public ResponseEntity<?> show(Authentication authentication) {
        User user = userServiceImpl.getUser(authentication.getName());
        return ResponseEntity.ok().body(user);
    }

    @PostMapping(value = "/update", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE, MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<?> update(@Valid @RequestParam String firstName, @Valid @RequestParam String lastName, Authentication authentication, @RequestParam("avatar") MultipartFile multipartFile) {
        String url = avatarService.saveFileToCloudinary(multipartFile);
        Avatar avatar = new Avatar(multipartFile.getName(), multipartFile.getContentType(), url);
        User user = userServiceImpl.getUser(authentication.getName());
        user.setFirstName(firstName);
        user.setLastName(lastName);
        avatarService.save(avatar);
        user.setAvatar(avatar);
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
