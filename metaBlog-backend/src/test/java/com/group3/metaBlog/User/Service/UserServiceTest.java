package com.group3.metaBlog.User.Service;

import com.group3.metaBlog.Blog.Model.Blog;
import com.group3.metaBlog.Blog.Repository.IBlogRepository;
import com.group3.metaBlog.Image.Model.Image;
import com.group3.metaBlog.Image.Service.ImageService;
import com.group3.metaBlog.Jwt.ServiceLayer.JwtService;
import com.group3.metaBlog.User.DataTransferObject.SavedBlogResponseDto;
import com.group3.metaBlog.User.DataTransferObject.UserUpdateRequestDto;
import com.group3.metaBlog.User.Model.User;
import com.group3.metaBlog.User.Repository.IUserRepository;
import com.group3.metaBlog.Utils.MetaBlogResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
public class UserServiceTest {

    @Mock
    private IBlogRepository blogRepository;
    @Mock
    private IUserRepository userRepository;

    @Mock
    private ImageService imageService;

    @Mock
    private JwtService jwtService;

    @InjectMocks
    private UserService userService;

    private User user;
    private Blog blog;
    private String token;

    @BeforeEach
    public void setUp() {
        user = new User();
        user.setEmail("test@example.com");
        user.setImageURL("imageURL");
        user.setPassword("password");
        user.setLinkedinURL("linkedinURL");
        user.setGithubURL("githubURL");
        user.setBio("bio");
        user.setBlogs(new ArrayList<>());
        user.setSavedBlogs(new ArrayList<>());

        blog = new Blog();
        blog.setId(1L);
        blog.setTitle("Test Blog");
        blog.setContent("Test Content");
        blog.setImageUrl("Test Image URL");
        blog.setAuthor(user);

        token = "testToken";
    }

    @Test
    public void GetUserByIdSuccessTest() {
        Long userId = 1L;
        when(jwtService.extractUserEmailFromToken(token)).thenReturn(user.getEmail());
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        ResponseEntity<Object> response = userService.getUserById(userId, token);

        verify(userRepository).findById(userId);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(true, ((MetaBlogResponse<?>) Objects.requireNonNull(response.getBody())).getSuccess());
    }

    @Test
    public void GetUserByIdUserNotFoundTest() {
        Long userId = 1L;
        when(jwtService.extractUserEmailFromToken(token)).thenReturn(user.getEmail());
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        ResponseEntity<Object> response = userService.getUserById(userId, token);

        verify(userRepository).findById(userId);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals(false, ((MetaBlogResponse<?>) Objects.requireNonNull(response.getBody())).getSuccess());
    }

    @Test
    public void GetUserDetailsSuccessTest() {
        when(jwtService.extractUserEmailFromToken(token)).thenReturn(user.getEmail());
        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));

        ResponseEntity<Object> response = userService.getUserDetails(token);

        verify(userRepository).findByEmail(user.getEmail());
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(true, ((MetaBlogResponse<?>) Objects.requireNonNull(response.getBody())).getSuccess());
    }

    @Test
    public void GetUserDetailsUserNotFoundTest() {
        when(jwtService.extractUserEmailFromToken(token)).thenReturn(user.getEmail());
        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.empty());

        ResponseEntity<Object> response = userService.getUserDetails(token);

        verify(userRepository).findByEmail(user.getEmail());
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals(false, ((MetaBlogResponse<?>) Objects.requireNonNull(response.getBody())).getSuccess());
    }

    @Test
    public void updateUserDetailsWithImageSuccessTest() {
        when(jwtService.extractUserEmailFromToken(token)).thenReturn(user.getEmail());
        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));

        UserUpdateRequestDto updateRequest = new UserUpdateRequestDto();
        updateRequest.setUserName("newUsername");
        updateRequest.setBio("new bio");
        updateRequest.setGithubURL("new githubURL");
        updateRequest.setLinkedinURL("new linkedinURL");

        MultipartFile mockFile = mock(MultipartFile.class);
        Optional<MultipartFile> optionalFile = Optional.of(mockFile);
        updateRequest.setImageURL(optionalFile);

        Image mockImage = new Image();
        mockImage.setUrl("newImageUrl");
        when(imageService.uploadImage(mockFile)).thenReturn(mockImage);

        ResponseEntity<Object> response = userService.updateUserDetails(updateRequest, token);

        verify(userRepository).save(user);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(true, ((MetaBlogResponse<?>) Objects.requireNonNull(response.getBody())).getSuccess());
        assertEquals("newImageUrl", user.getImageURL());
        assertEquals("newUsername", user.getUsername());
        assertEquals("new bio", user.getBio());
        assertEquals("new githubURL", user.getGithubURL());
        assertEquals("new linkedinURL", user.getLinkedinURL());
    }

    @Test
    public void updateUserDetailsWithoutImageSuccessTest() {
        when(jwtService.extractUserEmailFromToken(token)).thenReturn(user.getEmail());
        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));

        UserUpdateRequestDto updateRequest = new UserUpdateRequestDto();
        updateRequest.setUserName("newUsername");
        updateRequest.setBio("new bio");
        updateRequest.setGithubURL("new githubURL");
        updateRequest.setLinkedinURL("new linkedinURL");
        updateRequest.setImageURL(Optional.empty());

        ResponseEntity<Object> response = userService.updateUserDetails(updateRequest, token);

        verify(userRepository).save(user);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(true, ((MetaBlogResponse<?>) Objects.requireNonNull(response.getBody())).getSuccess());
        assertEquals("newUsername", user.getUsername());
        assertEquals("new bio", user.getBio());
        assertEquals("new githubURL", user.getGithubURL());
        assertEquals("new linkedinURL", user.getLinkedinURL());
    }

    @Test
    public void updateUserDetailsExceptionTest() {
        when(jwtService.extractUserEmailFromToken(token)).thenReturn(user.getEmail());
        when(userRepository.findByEmail(user.getEmail())).thenThrow(new RuntimeException("Database error"));

        UserUpdateRequestDto updateRequest = new UserUpdateRequestDto();

        ResponseEntity<Object> response = userService.updateUserDetails(updateRequest, token);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals(false, ((MetaBlogResponse<?>) Objects.requireNonNull(response.getBody())).getSuccess());
        assertEquals("Database error", ((MetaBlogResponse<?>) response.getBody()).getMessage());
    }

    @Test
    public void getUserSavedBlogsSuccessTest() {
        user.getSavedBlogs().add(blog);
        when(jwtService.extractUserEmailFromToken(token)).thenReturn(user.getEmail());
        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));

        ResponseEntity<Object> response = userService.getUserSavedBlogs(token);

        verify(userRepository).findByEmail(user.getEmail());
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(true, ((MetaBlogResponse<?>) Objects.requireNonNull(response.getBody())).getSuccess());

        List<SavedBlogResponseDto> savedBlogs = (List<SavedBlogResponseDto>) ((MetaBlogResponse<?>) response.getBody()).getData();
        assertNotNull(savedBlogs);
        assertEquals(1, savedBlogs.size());
        assertEquals(blog.getId(), savedBlogs.get(0).getId());
        assertEquals(blog.getTitle(), savedBlogs.get(0).getTitle());
        assertEquals(blog.getImageUrl(), savedBlogs.get(0).getImageUrl());
        assertEquals(blog.getAuthor().getUsername(), savedBlogs.get(0).getAuthor());
        assertEquals(blog.getAuthor().getImageURL(), savedBlogs.get(0).getAuthor_image_url());
        assertEquals(blog.getCreatedOn(), savedBlogs.get(0).getCreatedOn());
    }

    @Test
    public void getUserSavedBlogsExceptionTest() {
        when(jwtService.extractUserEmailFromToken(token)).thenReturn(user.getEmail());
        when(userRepository.findByEmail(user.getEmail())).thenThrow(new RuntimeException("Database error"));

        ResponseEntity<Object> response = userService.getUserSavedBlogs(token);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals(false, ((MetaBlogResponse<?>) Objects.requireNonNull(response.getBody())).getSuccess());
        assertEquals("Database error", ((MetaBlogResponse<?>) response.getBody()).getMessage());
    }

    @Test
    public void saveBlogSuccessTest() {
        when(jwtService.extractUserEmailFromToken(token)).thenReturn(user.getEmail());
        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));
        when(blogRepository.findById(blog.getId())).thenReturn(Optional.of(blog));

        ResponseEntity<Object> response = userService.saveBlog(blog.getId(), token);

        verify(userRepository).save(user);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(true, ((MetaBlogResponse<?>) Objects.requireNonNull(response.getBody())).getSuccess());
        assertTrue(user.getSavedBlogs().contains(blog));
    }

    @Test
    public void removeSavedBlogSuccessTest() {
        user.getSavedBlogs().add(blog);
        when(jwtService.extractUserEmailFromToken(token)).thenReturn(user.getEmail());
        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));

        ResponseEntity<Object> response = userService.removeSavedBlog(blog.getId(), token);

        verify(userRepository).save(user);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(true, ((MetaBlogResponse<?>) Objects.requireNonNull(response.getBody())).getSuccess());
        assertFalse(user.getSavedBlogs().contains(blog));
    }

    @Test
    public void GetUserBlogsSuccessTest() {
        user.getBlogs().add(blog);
        when(jwtService.extractUserEmailFromToken(token)).thenReturn(user.getEmail());
        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));

        ResponseEntity<Object> response = userService.getUserBlogs(token);

        verify(userRepository).findByEmail(user.getEmail());
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(true, ((MetaBlogResponse<?>) Objects.requireNonNull(response.getBody())).getSuccess());
    }

    @Test
    public void GetUserBlogsUserNotFoundTest() {
        when(jwtService.extractUserEmailFromToken(token)).thenReturn(user.getEmail());
        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.empty());

        ResponseEntity<Object> response = userService.getUserBlogs(token);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals(false, ((MetaBlogResponse<?>) Objects.requireNonNull(response.getBody())).getSuccess());
    }

    @Test
    public void GetUserSavedBlogsSuccessTest() {
        user.getSavedBlogs().add(blog);
        when(jwtService.extractUserEmailFromToken(token)).thenReturn(user.getEmail());
        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));

        ResponseEntity<Object> response = userService.getUserSavedBlogs(token);

        verify(userRepository).findByEmail(user.getEmail());
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(true, ((MetaBlogResponse<?>) Objects.requireNonNull(response.getBody())).getSuccess());
    }

    @Test
    public void SaveBlogAlreadySavedTest() {
        user.getSavedBlogs().add(blog);
        when(jwtService.extractUserEmailFromToken(token)).thenReturn(user.getEmail());
        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));
        when(blogRepository.findById(blog.getId())).thenReturn(Optional.of(blog));

        ResponseEntity<Object> response = userService.saveBlog(blog.getId(), token);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals(false, ((MetaBlogResponse<?>) Objects.requireNonNull(response.getBody())).getSuccess());
        assertEquals("Blog is already saved.", ((MetaBlogResponse<?>) response.getBody()).getMessage());
    }

    @Test
    public void SaveBlogBlogNotFoundTest() {
        user.getBlogs().clear();
        when(jwtService.extractUserEmailFromToken(token)).thenReturn(user.getEmail());
        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));
        when(blogRepository.findById(1L)).thenReturn(Optional.empty());

        ResponseEntity<Object> response = userService.saveBlog(1L, token);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals(false, ((MetaBlogResponse<?>) Objects.requireNonNull(response.getBody())).getSuccess());
        assertEquals("Blog not found.", ((MetaBlogResponse<?>) response.getBody()).getMessage());

        assertFalse(user.getSavedBlogs().contains(blog));
    }

    @Test
    public void RemoveSavedBlogSavedBlogNotFoundTest() {
        user.getSavedBlogs().clear();
        when(jwtService.extractUserEmailFromToken(token)).thenReturn(user.getEmail());
        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));

        ResponseEntity<Object> response = userService.removeSavedBlog(1L, token);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals(false, ((MetaBlogResponse<?>) Objects.requireNonNull(response.getBody())).getSuccess());
        assertEquals("Saved blog not found.", ((MetaBlogResponse<?>) response.getBody()).getMessage());
    }

    @Test
    public void GetUserSavedBlogsUserNotFoundTest() {
        when(jwtService.extractUserEmailFromToken(token)).thenReturn(user.getEmail());
        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.empty());

        ResponseEntity<Object> response = userService.getUserSavedBlogs(token);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals(false, ((MetaBlogResponse<?>) Objects.requireNonNull(response.getBody())).getSuccess());
    }

    @Test
    public void SaveBlogSuccessTest() {
        user.getBlogs().clear();
        when(jwtService.extractUserEmailFromToken(token)).thenReturn(user.getEmail());
        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));
        when(blogRepository.findById(blog.getId())).thenReturn(Optional.of(blog));

        ResponseEntity<Object> response = userService.saveBlog(blog.getId(), token);

        verify(userRepository).save(user);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(true, ((MetaBlogResponse<?>) Objects.requireNonNull(response.getBody())).getSuccess());

        assertTrue(user.getSavedBlogs().contains(blog));

    }

    @Test
    public void SaveBlogUserNotFoundTest() {
        when(jwtService.extractUserEmailFromToken(token)).thenReturn(user.getEmail());
        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.empty());

        ResponseEntity<Object> response = userService.saveBlog(blog.getId(), token);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals(false, ((MetaBlogResponse<?>) Objects.requireNonNull(response.getBody())).getSuccess());
    }

    @Test
    public void RemoveSavedBlogSuccessTest() {
        user.getSavedBlogs().add(blog);
        when(jwtService.extractUserEmailFromToken(token)).thenReturn(user.getEmail());
        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));

        ResponseEntity<Object> response = userService.removeSavedBlog(blog.getId(), token);

        verify(userRepository).save(user);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(true, ((MetaBlogResponse<?>) Objects.requireNonNull(response.getBody())).getSuccess());
    }

    @Test
    public void RemoveSavedBlogUserNotFoundTest() {
        when(jwtService.extractUserEmailFromToken(token)).thenReturn(user.getEmail());
        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.empty());

        ResponseEntity<Object> response = userService.removeSavedBlog(blog.getId(), token);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals(false, ((MetaBlogResponse<?>) Objects.requireNonNull(response.getBody())).getSuccess());
    }
}
