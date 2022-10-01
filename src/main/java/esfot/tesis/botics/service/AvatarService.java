package esfot.tesis.botics.service;

import esfot.tesis.botics.entity.Avatar;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface AvatarService {
    String saveFileToCloudinary(MultipartFile multipartFile) throws IOException;
    void save(Avatar avatar);
}
