package com.group3.metaBlog.Image.Service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.group3.metaBlog.Exception.MetaBlogException;
import com.group3.metaBlog.Image.Model.Image;
import com.group3.metaBlog.Image.Repository.ImageRepository;
import com.group3.metaBlog.Jwt.ServiceLayer.JwtService;
import com.group3.metaBlog.User.Model.User;
import com.group3.metaBlog.User.Repository.IUserRepository;
import com.group3.metaBlog.Utils.MetaBlogResponse;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.util.Objects;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ImageService implements IImageService {

    private static final Logger logger = LoggerFactory.getLogger(ImageService.class);
    private final IUserRepository userRepository;
    private final JwtService jwtService;
    private final ImageRepository imageRepository;
    private final AmazonS3 s3client;
    public final String bucketName = "metablog-bucket";

    @Override
    public Image uploadImage(MultipartFile file) {
        try {
            String fileName = System.currentTimeMillis() + "_" + file.getOriginalFilename();
            File fileObj = convertMultiPartFileToFile(file);
            s3client.putObject(new PutObjectRequest(bucketName, fileName, fileObj));
            if (fileObj == null) {
                logger.error("Error uploading image: file is null");
                return null;
            }
            fileObj.delete();
            var image = Image.builder()
                    .name(fileName)
                    .url(s3client.getUrl(bucketName, fileName).toString())
                    .uploadTime((double) System.currentTimeMillis())
                    .type(file.getContentType())
                    .build();
            imageRepository.save(image);
            return image;
        } catch (MetaBlogException e) {
            logger.error("Error uploading image: {}", e.getMessage());
            return null;
        }
    }

    @Override
    public ResponseEntity<Object> getProfileImage(String token) {
        try {
            String userEmail = jwtService.extractUserEmailFromToken(token);
            Optional<User> userOptional = userRepository.findByEmail(userEmail);
            if (userOptional.isEmpty()) {
                logger.error("User not found: {}", userEmail);
                return new ResponseEntity<>(MetaBlogResponse.builder()
                        .success(false)
                        .message("User not found")
                        .build(), HttpStatus.NOT_FOUND);
            }
            User user = userOptional.get();
            Optional<Image> image = imageRepository.findByName(user.getImageURL());
            if (image.isEmpty()) {
                logger.error("Error retrieving image: image not found");
                return ResponseEntity.badRequest().body(MetaBlogResponse.builder()
                        .success(false)
                        .message("Image not found")
                        .build());
            }
            return ResponseEntity.ok(MetaBlogResponse.builder()
                    .success(true)
                    .message("Image retrieved successfully")
                    .data(image.get())
                    .build());

        } catch (MetaBlogException e) {
            logger.error("Error retrieving image: {}", e.getMessage());
            return ResponseEntity.badRequest().body(MetaBlogResponse.builder()
                    .success(false)
                    .message("Error retrieving image")
                    .build());
        }
    }

    private File convertMultiPartFileToFile(MultipartFile file) {
        try {
            File convFile = new File(Objects.requireNonNull(file.getOriginalFilename()));
            FileOutputStream fos = new FileOutputStream(convFile);
            fos.write(file.getBytes());
            fos.close();
            return convFile;
        } catch (java.io.IOException | MetaBlogException e) {
            logger.error("Error converting multipart file to file: {}", e.getMessage());
            return null;
        }
    }

    @Override
    public ResponseEntity<Object> setUserUrl(String url, String token) {
        try {
            String userEmail = jwtService.extractUserEmailFromToken(token.split(" ")[1]);
            Optional<User> userOptional = userRepository.findByEmail(userEmail);
            if (userOptional.isEmpty()) {
                logger.error("User not found: {}", userEmail);
                return new ResponseEntity<>(MetaBlogResponse.builder()
                        .success(false)
                        .message("User not found")
                        .build(), HttpStatus.NOT_FOUND);
            }
            User user = userOptional.get();
            user.setImageURL(url);
            userRepository.save(user);
            logger.info("User image URL set successfully");
            return ResponseEntity.ok(MetaBlogResponse.builder()
                    .success(true)
                    .message("User image URL set successfully")
                    .build());
        } catch (MetaBlogException e) {
            logger.error("Error setting user image URL: {}", e.getMessage());
            return new ResponseEntity<>(MetaBlogResponse.builder()
                    .success(false)
                    .message("Error setting user image URL")
                    .build(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
