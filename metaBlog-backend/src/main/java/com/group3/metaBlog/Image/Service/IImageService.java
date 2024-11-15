package com.group3.metaBlog.Image.Service;

import com.group3.metaBlog.Image.Model.Image;
import jakarta.mail.MessagingException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

public interface IImageService {
    Image uploadImage(MultipartFile file) throws MessagingException;
    ResponseEntity<Object> getProfileImage(String token);
    ResponseEntity<Object> setUserUrl(String url, String token);
}
