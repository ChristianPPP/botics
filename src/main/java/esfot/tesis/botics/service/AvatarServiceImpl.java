package esfot.tesis.botics.service;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import esfot.tesis.botics.entity.Avatar;
import esfot.tesis.botics.repository.AvatarRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Map;
import java.util.Objects;

@Service
public class AvatarServiceImpl implements AvatarService{
    @Autowired
    AvatarRepository avatarRepository;

    @Autowired
    Cloudinary cloudinaryConfig;


    @Override
    public void save(Avatar avatar) {
        avatarRepository.save(avatar);
    }

    @Override
    public String saveFileToCloudinary(MultipartFile multipartFile) {
        try {
            File uploadedFile = convertMultiPartToFile(multipartFile);
            Map uploadResult = cloudinaryConfig.uploader().upload(uploadedFile, ObjectUtils.emptyMap());
            boolean isDeleted = uploadedFile.delete();
            if (isDeleted){
                System.out.println("File successfully deleted");
            }else
                System.out.println("File doesn't exist");
            return  uploadResult.get("url").toString();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


    private File convertMultiPartToFile(MultipartFile file) throws IOException {
        File convFile = new File(Objects.requireNonNull(file.getOriginalFilename()));
        FileOutputStream fos = new FileOutputStream(convFile);
        fos.write(file.getBytes());
        fos.close();
        return convFile;
    }

}
