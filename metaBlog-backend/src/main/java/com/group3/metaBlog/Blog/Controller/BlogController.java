package com.group3.metaBlog.Blog.Controller;

import com.group3.metaBlog.Blog.DTO.BlogRequestDto;
import com.group3.metaBlog.Blog.Service.IBlogService;
import com.group3.metaBlog.Utils.MetaBlogResponse;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/blogs")
@AllArgsConstructor
public class BlogController {

    private static final Logger logger = LoggerFactory.getLogger(BlogController.class);
    private final IBlogService blogService;

    @PostMapping(path = "/create-blog")
    public ResponseEntity<Object> createBlog(@ModelAttribute BlogRequestDto blogRequestDto, @RequestHeader("Authorization") String token) {
        if (token == null || token.isEmpty()) {
            logger.error("Token not provided");
            return ResponseEntity.status(401).body(MetaBlogResponse.builder()
                    .success(false)
                    .message("Token not provided")
                    .build());
        }

        logger.info("Creating blog with token: {}", token);
        return blogService.createBlog(blogRequestDto, token.replace("Bearer ", ""));
    }

    @GetMapping("/all-blogs")
    public ResponseEntity<Object> getAllBlogs() {
        return blogService.getAllBlogs();
    }

    @GetMapping("/my-blogs")
    public ResponseEntity<Object> getBlogsByUser(@RequestHeader("Authorization") String token) {
        if (token == null || token.isEmpty()) {
            logger.error("Token not provided");
            return ResponseEntity.status(401).body(MetaBlogResponse.builder()
                    .success(false)
                    .message("Token not provided")
                    .build());
        }

        logger.info("Fetching blogs for user with token: {}", token);
        return blogService.getBlogsByUser(token.replace("Bearer ", ""));
    }

    @GetMapping("/search")
    public ResponseEntity<Object> searchBlogsByTitle(@RequestParam String title) {
        return blogService.searchBlogsByTitle(title);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> getBlogById(@PathVariable Long id) {
        return blogService.getBlogById(id);
    }
}
