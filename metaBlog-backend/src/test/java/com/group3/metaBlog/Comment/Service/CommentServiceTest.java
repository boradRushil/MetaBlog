package com.group3.metaBlog.Comment.Service;

import com.group3.metaBlog.Blog.Model.Blog;
import com.group3.metaBlog.Blog.Repository.IBlogRepository;
import com.group3.metaBlog.Comment.DataTransferObject.CommentResponseDTO;
import com.group3.metaBlog.Comment.DataTransferObject.CreateCommentDto;
import com.group3.metaBlog.Comment.Model.Comment;
import com.group3.metaBlog.Comment.Repository.ICommentRepository;
import com.group3.metaBlog.Exception.MetaBlogException;
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

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CommentServiceTest {

    @Mock
    private ICommentRepository commentRepository;

    @Mock
    private IBlogRepository blogRepository;

    @Mock
    private IUserRepository userRepository;

    @Mock
    private JwtService jwtService;

    @InjectMocks
    private CommentService commentService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void createCommentSuccessTest() {
        CreateCommentDto request = new CreateCommentDto("Test content", 1L);
        String token = "Bearer testToken";
        String userEmail = "test@example.com";
        Blog blog = new Blog();
        blog.setId(1L);
        User user = new User();
        user.setEmail(userEmail);

        when(jwtService.extractUserEmailFromToken("testToken")).thenReturn(userEmail);
        when(blogRepository.findById(1L)).thenReturn(Optional.of(blog));
        when(userRepository.findByEmail(userEmail)).thenReturn(Optional.of(user));
        when(commentRepository.save(any(Comment.class))).thenAnswer(i -> i.getArguments()[0]);

        ResponseEntity<Object> response = commentService.createComment(request, token);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(((MetaBlogResponse) response.getBody()).getSuccess());
        verify(commentRepository, times(1)).save(any(Comment.class));
    }

    @Test
    void createCommentBlogNotFoundTest() {
        CreateCommentDto request = new CreateCommentDto("Test content", 1L);
        String token = "Bearer testToken";
        String userEmail = "test@example.com";

        when(jwtService.extractUserEmailFromToken("testToken")).thenReturn(userEmail);
        when(blogRepository.findById(1L)).thenReturn(Optional.empty());

        ResponseEntity<Object> response = commentService.createComment(request, token);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        MetaBlogResponse responseBody = (MetaBlogResponse) response.getBody();
        assertNotNull(responseBody);
        assertEquals("Blog not found.", responseBody.getMessage());
        assertFalse(responseBody.getSuccess());

        verify(commentRepository, never()).save(any(Comment.class));
    }

    @Test
    void createCommentUserNotFoundTest() {
        CreateCommentDto request = new CreateCommentDto("Test content", 1L);
        String token = "Bearer testToken";
        String userEmail = "test@example.com";

        when(jwtService.extractUserEmailFromToken("testToken")).thenReturn(userEmail);
        when(blogRepository.findById(1L)).thenReturn(Optional.of(new Blog()));
        when(userRepository.findByEmail(userEmail)).thenReturn(Optional.empty());

        ResponseEntity<Object> response = commentService.createComment(request, token);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        MetaBlogResponse responseBody = (MetaBlogResponse) response.getBody();
        assertNotNull(responseBody);
        assertEquals("User not found.", responseBody.getMessage());
        assertFalse(responseBody.getSuccess());

        verify(commentRepository, never()).save(any(Comment.class));
    }

    @Test
    void getCommentsByBlogSuccessTest() {
        // Arrange
        Long blogId = 1L;
        Blog blog = new Blog();
        blog.setId(blogId);

        User user1 = new User();
        user1.setUsername("user1");
        user1.setImageURL("image1.jpg");

        User user2 = new User();
        user2.setUsername("user2");
        user2.setImageURL("image2.jpg");

        Comment comment1 = new Comment();
        comment1.setId(1L);
        comment1.setContent("Comment 1");
        comment1.setCreatedOn(1234567890.0);
        comment1.setUser(user1);

        Comment comment2 = new Comment();
        comment2.setId(2L);
        comment2.setContent("Comment 2");
        comment2.setCreatedOn(1234567891.0);
        comment2.setUser(user2);

        List<Comment> comments = Arrays.asList(comment1, comment2);

        when(blogRepository.findById(blogId)).thenReturn(Optional.of(blog));
        when(commentRepository.findByBlog(blog)).thenReturn(comments);

        // Act
        ResponseEntity<Object> response = commentService.getCommentsByBlog(blogId);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(((MetaBlogResponse<?>) response.getBody()).getSuccess());
        assertEquals("Comments fetched successfully.", ((MetaBlogResponse<?>) response.getBody()).getMessage());

        List<CommentResponseDTO> responseDTOs = (List<CommentResponseDTO>) ((MetaBlogResponse<?>) response.getBody()).getData();
        assertEquals(2, responseDTOs.size());

        CommentResponseDTO dto1 = responseDTOs.get(0);
        assertEquals(1L, dto1.getId());
        assertEquals("Comment 1", dto1.getContent());
        assertEquals(1234567890.0, dto1.getCreatedOn());
        assertEquals("user1", dto1.getAuthor());
        assertEquals("image1.jpg", dto1.getAuthor_image_url());

        CommentResponseDTO dto2 = responseDTOs.get(1);
        assertEquals(2L, dto2.getId());
        assertEquals("Comment 2", dto2.getContent());
        assertEquals(1234567891.0, dto2.getCreatedOn());
        assertEquals("user2", dto2.getAuthor());
        assertEquals("image2.jpg", dto2.getAuthor_image_url());
    }

    @Test
    void getCommentsByBlogNotFoundTest() {
        Long blogId = 1L;

        when(blogRepository.findById(blogId)).thenReturn(Optional.empty());

        ResponseEntity<Object> response = commentService.getCommentsByBlog(blogId);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());

        MetaBlogResponse responseBody = (MetaBlogResponse) response.getBody();
        assertNotNull(responseBody);
        assertEquals("Blog not found.", responseBody.getMessage());
        assertFalse(responseBody.getSuccess());

        verify(commentRepository, never()).findByBlog(any(Blog.class));
    }

    @Test
    void createCommentInvalidTokenTest() {
        CreateCommentDto request = new CreateCommentDto("Test content", 1L);
        String token = "Bearer InvalidToken";

        when(jwtService.extractUserEmailFromToken("InvalidToken")).thenThrow(new MetaBlogException("Invalid token"));

        ResponseEntity<Object> response = commentService.createComment(request, token);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        MetaBlogResponse responseBody = (MetaBlogResponse) response.getBody();
        assertNotNull(responseBody);
        assertEquals("Invalid token", responseBody.getMessage());
        assertFalse(responseBody.getSuccess());

        verify(commentRepository, never()).save(any(Comment.class));
    }
}