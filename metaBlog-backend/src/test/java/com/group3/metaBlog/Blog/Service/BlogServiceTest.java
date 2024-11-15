package com.group3.metaBlog.Blog.Service;

import com.group3.metaBlog.Blog.DTO.BlogRequestDto;
import com.group3.metaBlog.Blog.DTO.BlogResponseDto;
import com.group3.metaBlog.Blog.Model.Blog;
import com.group3.metaBlog.Blog.Repository.IBlogRepository;
import com.group3.metaBlog.Enum.BlogStatus;
import com.group3.metaBlog.Image.Model.Image;
import com.group3.metaBlog.Image.Service.ImageService;
import com.group3.metaBlog.Jwt.ServiceLayer.JwtService;
import com.group3.metaBlog.User.Model.User;
import com.group3.metaBlog.User.Repository.IUserRepository;
import com.group3.metaBlog.Utils.MetaBlogResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

class BlogServiceTest {

    @Mock
    private IBlogRepository blogRepository;

    @Mock
    private ImageService imageService;

    @Mock
    private IUserRepository userRepository;

    @Mock
    private JwtService jwtService;

    @InjectMocks
    private BlogService blogService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    MultipartFile testFile = new MockMultipartFile("image", "test.jpg", "image/jpeg", "test image content".getBytes());

    @Test
    void createBlogTest() {
        // Arrange
        BlogRequestDto requestDto = new BlogRequestDto("Test Title", "Test Content", testFile, "Test Name", "Test description");
        String token = "testToken";
        User user = new User();
        user.setEmail("test@example.com");
        user.setUsername("testUser");

        when(jwtService.extractUserEmailFromToken(token)).thenReturn("test@example.com");
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(user));
        when(blogRepository.existsByTitleAndContent(anyString(), anyString())).thenReturn(false);
        Image image = new Image();
        image.setUrl("testUrl");
        when(imageService.uploadImage(testFile)).thenReturn(image);

        // Act
        ResponseEntity<Object> response = blogService.createBlog(requestDto, token);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(((MetaBlogResponse) response.getBody()).getSuccess());
        verify(blogRepository, times(1)).save(any(Blog.class));
    }

    @Test
    void createBlogUserNotFoundTest() {
        // Arrange
        BlogRequestDto requestDto = new BlogRequestDto("Test Title", "Test Content", testFile, "Test Name", "Test description");
        String token = "testToken";

        when(jwtService.extractUserEmailFromToken(token)).thenReturn("test@example.com");
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.empty());

        // Act
        ResponseEntity<Object> response = blogService.createBlog(requestDto, token);

        // Assert
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertFalse(((MetaBlogResponse) response.getBody()).getSuccess());
    }

    @Test
    void createBlogAlreadyExistsTest() {
        // Arrange
        BlogRequestDto requestDto = new BlogRequestDto("Test Title", "Test Content", testFile, "Test Name", "Test description");
        String token = "testToken";
        User user = new User();
        user.setEmail("test@example.com");

        when(jwtService.extractUserEmailFromToken(token)).thenReturn("test@example.com");
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(user));
        when(blogRepository.existsByTitleAndContent(anyString(), anyString())).thenReturn(true);

        // Act
        ResponseEntity<Object> response = blogService.createBlog(requestDto, token);

        // Assert
        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        assertFalse(((MetaBlogResponse) response.getBody()).getSuccess());
    }

    @Test
    void createBlogExceptionTest() {
        // Arrange
        BlogRequestDto requestDto = new BlogRequestDto("Test Title", "Test Content", testFile, "Test Name", "Test description");
        String token = "testToken";

        when(jwtService.extractUserEmailFromToken(token)).thenThrow(new RuntimeException("Test exception"));

        // Act
        ResponseEntity<Object> response = blogService.createBlog(requestDto, token);

        // Assert
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertFalse(((MetaBlogResponse) response.getBody()).getSuccess());
    }

    @Test
    void getAllBlogsTest() {
        // Arrange
        List<Blog> blogs = Arrays.asList(
                createBlog(1L, "Title 1", "Content 1", BlogStatus.APPROVED),
                createBlog(2L, "Title 2", "Content 2", BlogStatus.APPROVED)
        );
        when(blogRepository.findAll()).thenReturn(blogs);

        // Act
        ResponseEntity<Object> response = blogService.getAllBlogs();

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(((MetaBlogResponse) response.getBody()).getSuccess());
        assertEquals(2, ((List<BlogResponseDto>) ((MetaBlogResponse) response.getBody()).getData()).size());
    }

    @Test
    void getAllBlogsExceptionTest() {
        // Arrange
        when(blogRepository.findAll()).thenThrow(new RuntimeException("Test exception"));

        // Act
        ResponseEntity<Object> response = blogService.getAllBlogs();

        // Assert
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertFalse(((MetaBlogResponse) response.getBody()).getSuccess());
    }

    @Test
    void getBlogsByUserTest() {
        // Arrange
        String token = "testToken";
        User user = new User();
        user.setId(1L);
        user.setEmail("test@example.com");
        user.setUsername("testUser");

        List<Blog> blogs = Arrays.asList(
                createBlog(1L, "Title 1", "Content 1", BlogStatus.APPROVED),
                createBlog(2L, "Title 2", "Content 2", BlogStatus.APPROVED)
        );

        when(jwtService.extractUserEmailFromToken(token)).thenReturn("test@example.com");
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(user));
        when(blogRepository.findByAuthorId(1L)).thenReturn(blogs);

        // Act
        ResponseEntity<Object> response = blogService.getBlogsByUser(token);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(((MetaBlogResponse) response.getBody()).getSuccess());
        assertEquals(2, ((List<BlogResponseDto>) ((MetaBlogResponse) response.getBody()).getData()).size());
    }

    @Test
    void getBlogsByUserNotFoundTest() {
        // Arrange
        String token = "testToken";

        when(jwtService.extractUserEmailFromToken(token)).thenReturn("test@example.com");
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.empty());

        // Act
        ResponseEntity<Object> response = blogService.getBlogsByUser(token);

        // Assert
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertFalse(((MetaBlogResponse) response.getBody()).getSuccess());
    }

    @Test
    void getBlogsByUserExceptionTest() {
        // Arrange
        String token = "testToken";

        when(jwtService.extractUserEmailFromToken(token)).thenThrow(new RuntimeException("Test exception"));

        // Act
        ResponseEntity<Object> response = blogService.getBlogsByUser(token);

        // Assert
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertFalse(((MetaBlogResponse) response.getBody()).getSuccess());
    }

    @Test
    void searchBlogsByTitleTest() {
        // Arrange
        String title = "Test";
        List<Blog> blogs = Arrays.asList(
                createBlog(1L, "Test Title 1", "Content 1", BlogStatus.APPROVED),
                createBlog(2L, "Test Title 2", "Content 2", BlogStatus.APPROVED)
        );

        when(blogRepository.findByTitleContaining(title)).thenReturn(blogs);

        // Act
        ResponseEntity<Object> response = blogService.searchBlogsByTitle(title);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(((MetaBlogResponse) response.getBody()).getSuccess());
        assertEquals(2, ((List<BlogResponseDto>) ((MetaBlogResponse) response.getBody()).getData()).size());
        verify(blogRepository, times(2)).save(any(Blog.class)); // Verify view count increment
    }

    @Test
    void searchBlogsByTitleExceptionTest() {
        // Arrange
        String title = "Test";

        when(blogRepository.findByTitleContaining(title)).thenThrow(new RuntimeException("Test exception"));

        // Act
        ResponseEntity<Object> response = blogService.searchBlogsByTitle(title);

        // Assert
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertFalse(((MetaBlogResponse) response.getBody()).getSuccess());
    }

    @Test
    void getBlogByIdTest() {
        // Arrange
        Long id = 1L;
        Blog blog = createBlog(id, "Title", "Content", BlogStatus.APPROVED);

        when(blogRepository.findById(id)).thenReturn(Optional.of(blog));

        // Act
        ResponseEntity<Object> response = blogService.getBlogById(id);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(((MetaBlogResponse) response.getBody()).getSuccess());
        BlogResponseDto responseDto = (BlogResponseDto) ((MetaBlogResponse) response.getBody()).getData();
        assertEquals(id, responseDto.getId());
    }

    @Test
    void getBlogByIdNotFoundTest() {
        // Arrange
        Long id = 1L;

        when(blogRepository.findById(id)).thenReturn(Optional.empty());

        // Act
        ResponseEntity<Object> response = blogService.getBlogById(id);

        // Assert
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertFalse(((MetaBlogResponse) response.getBody()).getSuccess());
    }

    @Test
    void getBlogByIdExceptionTest() {
        // Arrange
        Long id = 1L;

        when(blogRepository.findById(id)).thenThrow(new RuntimeException("Test exception"));

        // Act
        ResponseEntity<Object> response = blogService.getBlogById(id);

        // Assert
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertFalse(((MetaBlogResponse) response.getBody()).getSuccess());
    }

    private Blog createBlog(Long id, String title, String content, BlogStatus status) {
        Blog blog = new Blog();
        blog.setId(id);
        blog.setTitle(title);
        blog.setContent(content);
        blog.setImageUrl("test.jpg");
        blog.setCreatedOn((double) System.currentTimeMillis());
        blog.setStatus(status);
        User author = new User();
        author.setUsername("testUser");
        blog.setAuthor(author);
        blog.setViewCount(0);
        return blog;
    }
}
