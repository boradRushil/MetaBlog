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
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class CommentService implements ICommentService {

    private final ICommentRepository commentRepository;
    private final IBlogRepository blogRepository;
    private final IUserRepository userRepository;
    private final JwtService jwtService;
    private final Logger logger = LoggerFactory.getLogger(CommentService.class);

    @Override
    public ResponseEntity<Object> createComment(CreateCommentDto request, String token) {
        try {
            logger.info("Creating comment for blogId: {}", request.getBlogId());

            String userEmail = jwtService.extractUserEmailFromToken(token.split(" ")[1]);

            Optional<Blog> blogOptional = blogRepository.findById(request.getBlogId());
            if (blogOptional.isEmpty()) {
                logger.error("Blog not found with id: {}", request.getBlogId());
                throw new MetaBlogException("Blog not found.");
            }

            Optional<User> userOptional = userRepository.findByEmail(userEmail);
            if (userOptional.isEmpty()) {
                logger.error("User not found with email: {}", userEmail);
                throw new MetaBlogException("User not found.");
            }

            Blog blog = blogOptional.get();
            User user = userOptional.get();

            Comment comment = Comment.builder()
                    .content(request.getContent())
                    .blog(blog)
                    .user(user)
                    .createdOn(System.currentTimeMillis() / 1000.0)
                    .build();

            commentRepository.save(comment);


            return ResponseEntity.ok().body(MetaBlogResponse.builder()
                    .success(true)
                    .message("Comment created successfully.")
                    .data(CommentResponseDTO.builder()
                            .id(comment.getId())
                            .content(comment.getContent())
                            .createdOn(comment.getCreatedOn())
                            .author(comment.getUser().getUsername())
                            .author_image_url(comment.getUser().getImageURL())
                            .build())
                    .build());
        } catch (MetaBlogException e) {
            logger.error("Error creating comment: {}", e.getMessage());
            return ResponseEntity.badRequest().body(MetaBlogResponse.builder()
                    .success(false)
                    .message(e.getMessage())
                    .build());
        }
    }

    @Override
    public ResponseEntity<Object> getCommentsByBlog(Long blogId) {
        try {
            logger.info("Fetching comments for blogId: {}", blogId);

            Optional<Blog> blogOptional = blogRepository.findById(blogId);
            if (blogOptional.isEmpty()) {
                logger.error("Blog not found with id: {}", blogId);
                throw new MetaBlogException("Blog not found.");
            }

            Blog blog = blogOptional.get();
            List<Comment> comments = commentRepository.findByBlog(blog);

            List<CommentResponseDTO> responseDTOs = comments.stream().map(comment -> CommentResponseDTO.builder()
                    .id(comment.getId())
                    .content(comment.getContent())
                    .createdOn(comment.getCreatedOn())
                    .author(comment.getUser().getUsername())
                    .author_image_url(comment.getUser().getImageURL())
                    .build()).toList();

            return ResponseEntity.ok().body(MetaBlogResponse.builder()
                    .success(true)
                    .message("Comments fetched successfully.")
                    .data(responseDTOs)
                    .build());
        } catch (MetaBlogException e) {
            logger.error("Error fetching comments: {}", e.getMessage());
            return ResponseEntity.badRequest().body(MetaBlogResponse.builder()
                    .success(false)
                    .message(e.getMessage())
                    .build());
        }
    }
}
