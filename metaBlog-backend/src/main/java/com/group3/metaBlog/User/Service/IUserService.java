package com.group3.metaBlog.User.Service;

import com.group3.metaBlog.User.DataTransferObject.UserDetailsResponseDto;
import com.group3.metaBlog.User.DataTransferObject.UserUpdateRequestDto;
import org.springframework.http.ResponseEntity;

public interface IUserService {
    ResponseEntity<Object> getUserById(Long id, String token);

    ResponseEntity<Object> getUserDetails(String token);

    ResponseEntity<Object> updateUserDetails(UserUpdateRequestDto request, String token);

    ResponseEntity<Object> getUserBlogs(String token);

    ResponseEntity<Object> getUserSavedBlogs(String token);

    ResponseEntity<Object> saveBlog(Long blogId, String token);

    ResponseEntity<Object> removeSavedBlog(Long blogId, String token);
}
