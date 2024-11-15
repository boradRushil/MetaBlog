package com.group3.metaBlog.User.DataTransferObject;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import java.util.Optional;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserUpdateRequestDto {
    private String userName;
    private String email;
    private String linkedinURL;
    private String githubURL;
    private String bio;
    private Optional<MultipartFile> imageURL;

}
