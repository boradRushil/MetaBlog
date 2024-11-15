package com.group3.metaBlog.User.Service;

import com.group3.metaBlog.Blog.Model.Blog;
import com.group3.metaBlog.Blog.Repository.IBlogRepository;
import com.group3.metaBlog.Config.ApplicationConfig;
import com.group3.metaBlog.Exception.MetaBlogException;
import com.group3.metaBlog.Image.Model.Image;
import com.group3.metaBlog.Image.Service.ImageService;
import com.group3.metaBlog.Jwt.ServiceLayer.JwtService;
import com.group3.metaBlog.User.DataTransferObject.SavedBlogResponseDto;
import com.group3.metaBlog.User.DataTransferObject.UserDetailsResponseDto;
import com.group3.metaBlog.User.DataTransferObject.UserUpdateRequestDto;
import com.group3.metaBlog.User.Model.User;
import com.group3.metaBlog.User.Repository.IUserRepository;
import com.group3.metaBlog.Utils.MetaBlogResponse;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class UserService implements IUserService {

    private final IUserRepository userRepository;
    private final IBlogRepository blogRepository;
    private final JwtService jwtService;
    private final ImageService imageService;
    private final ApplicationConfig applicationConfig;
    private final Logger logger = LoggerFactory.getLogger(UserService.class);

    private User findUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> {
                    logger.error("User not found with email: {}", email);
                    return new MetaBlogException("User not found.");
                });
    }

    @Override
    public ResponseEntity<Object> getUserById(Long id, String token) {
        try {
            String email = jwtService.extractUserEmailFromToken(token);
            logger.info("Fetching user details for ID: {} with email: {}", id, email);
            User user = userRepository.findById(id)
                    .orElseThrow(() -> {
                        logger.error("User not found with ID: {}", id);
                        return new MetaBlogException("User not found.");
                    });

            UserDetailsResponseDto userDetailsResponseDto = UserDetailsResponseDto.builder()
                    .userName(user.getUsername())
                    .imageURL(user.getImageURL())
                    .email(user.getEmail())
                    .password(user.getPassword())
                    .linkedinURL(user.getLinkedinURL())
                    .githubURL(user.getGithubURL())
                    .bio(user.getBio())
                    .build();

            return ResponseEntity.ok().body(MetaBlogResponse.builder()
                    .success(true)
                    .message("User details fetched successfully.")
                    .data(userDetailsResponseDto)
                    .build());
        } catch (MetaBlogException e) {
            logger.error("Error fetching user details for ID: {}", id);
            logger.error("Message of the error: {}", e.getMessage());
            return ResponseEntity.badRequest().body(MetaBlogResponse.builder()
                    .success(false)
                    .message(e.getMessage())
                    .build());
        }
    }

    @Override
    public ResponseEntity<Object> getUserDetails(String token) {
        try {
            String email = jwtService.extractUserEmailFromToken(token);
            logger.info("Fetching user details for email: {}", email);
            User user = findUserByEmail(email);

            UserDetailsResponseDto userDetailsResponseDto = UserDetailsResponseDto.builder()
                    .userName(user.getUsername())
                    .imageURL(user.getImageURL())
                    .email(user.getEmail())
                    .linkedinURL(user.getLinkedinURL())
                    .githubURL(user.getGithubURL())
                    .bio(user.getBio())
                    .build();

            return ResponseEntity.ok().body(MetaBlogResponse.builder()
                    .success(true)
                    .message("User details fetched successfully.")
                    .data(userDetailsResponseDto)
                    .build());
        } catch (MetaBlogException e) {
            logger.error("Error fetching user details");
            logger.error("Message of the error: {}", e.getMessage());
            return ResponseEntity.badRequest().body(MetaBlogResponse.builder()
                    .success(false)
                    .message(e.getMessage())
                    .build());
        }
    }

    @Override
    public ResponseEntity<Object> updateUserDetails(UserUpdateRequestDto request, String token) {
        try {
            String email = jwtService.extractUserEmailFromToken(token);
            logger.info("Updating user details for email: {}", email);
            User user = findUserByEmail(email);

            if(request.getImageURL() != null && request.getImageURL().isPresent()) {
                Optional<MultipartFile> file = request.getImageURL();
                Image image = imageService.uploadImage(file.get());
                user.setImageURL(image.getUrl());
            }
            user.setUsername(request.getUserName());
            user.setBio(request.getBio());
            user.setGithubURL(request.getGithubURL());
            user.setLinkedinURL(request.getLinkedinURL());

            userRepository.save(user);
            return ResponseEntity.ok().body(MetaBlogResponse.builder()
                    .success(true)
                    .message("User details updated successfully.")
                    .build());
        } catch (Exception e) {
            logger.error("Error updating user details");
            logger.error("Message of the error: {}", e.getMessage());
            return ResponseEntity.badRequest().body(MetaBlogResponse.builder()
                    .success(false)
                    .message(e.getMessage())
                    .build());
        }
    }

    @Override
    public ResponseEntity<Object> getUserBlogs(String token) {
        try {
            String email = jwtService.extractUserEmailFromToken(token);
            logger.info("Fetching blogs for user with email: {}", email);
            User user = findUserByEmail(email);

            List<Blog> blogs = user.getBlogs();
            return ResponseEntity.ok().body(MetaBlogResponse.builder()
                    .success(true)
                    .message("User blogs fetched successfully.")
                    .data(blogs)
                    .build());
        } catch (Exception e) {
            logger.error("Error fetching user blogs");
            logger.error("Message of the error: {}", e.getMessage());
            return ResponseEntity.badRequest().body(MetaBlogResponse.builder()
                    .success(false)
                    .message(e.getMessage())
                    .build());
        }
    }

    @Override
    public ResponseEntity<Object> getUserSavedBlogs(String token) {
        try {
            String email = jwtService.extractUserEmailFromToken(token);
            logger.info("Fetching saved blogs for user with email: {}", email);
            User user = findUserByEmail(email);

            List<SavedBlogResponseDto> savedBlogs = user.getSavedBlogs().stream().map(blog -> SavedBlogResponseDto.builder()
                    .id(blog.getId())
                    .title(blog.getTitle())
                    .imageUrl(blog.getImageUrl())
                    .author(blog.getAuthor().getUsername())
                    .author_image_url(blog.getAuthor().getImageURL())
                    .createdOn(blog.getCreatedOn())
                    .build()).toList();

            return ResponseEntity.ok().body(MetaBlogResponse.builder()
                    .success(true)
                    .message("User saved blogs fetched successfully.")
                    .data(savedBlogs)
                    .build());
        } catch (Exception e) {
            logger.error("Error fetching user saved blogs");
            logger.error("Message of the error: {}", e.getMessage());
            return ResponseEntity.badRequest().body(MetaBlogResponse.builder()
                    .success(false)
                    .message(e.getMessage())
                    .build());
        }
    }

    @Override
    public ResponseEntity<Object> saveBlog(Long blogId, String token) {
        try {
            String email = jwtService.extractUserEmailFromToken(token);
            logger.info("Saving blog with id: {} for user with email: {}", blogId, email);
            User user = findUserByEmail(email);

            Blog blog = blogRepository.findById(blogId)
                    .orElseThrow(() -> {
                        logger.error("Blog not found with id: {}", blogId);
                        return new MetaBlogException("Blog not found.");
                    });

            if (user.getSavedBlogs().contains(blog)) {
                logger.warn("Blog with id: {} is already saved for user with email: {}", blogId, email);
                return ResponseEntity.badRequest().body(MetaBlogResponse.builder()
                        .success(false)
                        .message("Blog is already saved.")
                        .build());
            }

            user.getSavedBlogs().add(blog);
            userRepository.save(user);

            return ResponseEntity.ok().body(MetaBlogResponse.builder()
                    .success(true)
                    .message("Blog saved successfully.")
                    .data(blog)
                    .build());
        } catch (MetaBlogException e) {
            logger.error("Error saving blog: {}", e.getMessage());
            return ResponseEntity.badRequest().body(MetaBlogResponse.builder()
                    .success(false)
                    .message(e.getMessage())
                    .build());
        }
    }

    @Override
    public ResponseEntity<Object> removeSavedBlog(Long blogId, String token) {
        try {
            String email = jwtService.extractUserEmailFromToken(token);
            logger.info("Removing saved blog with id: {} for user with email: {}", blogId, email);
            User user = findUserByEmail(email);

            Blog blog = user.getSavedBlogs().stream().filter(b -> b.getId().equals(blogId))
                    .findFirst()
                    .orElseThrow(() -> {
                        logger.error("Saved blog not found with id: {}", blogId);
                        return new MetaBlogException("Saved blog not found.");
                    });

            user.getSavedBlogs().remove(blog);
            userRepository.save(user);

            return ResponseEntity.ok().body(MetaBlogResponse.builder()
                    .success(true)
                    .message("Saved blog removed successfully.")
                    .data(blog)
                    .build());
        } catch (MetaBlogException e) {
            logger.error("Error removing saved blog: {}", e.getMessage());
            return ResponseEntity.badRequest().body(MetaBlogResponse.builder()
                    .success(false)
                    .message(e.getMessage())
                    .build());
        }
    }
}
