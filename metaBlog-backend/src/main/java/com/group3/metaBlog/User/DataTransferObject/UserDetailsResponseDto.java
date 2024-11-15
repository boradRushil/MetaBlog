package com.group3.metaBlog.User.DataTransferObject;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserDetailsResponseDto {
    private String userName;
    private String imageURL;
    private String email;
    private String password;
    private String linkedinURL;
    private String githubURL;
    private String bio;
}
