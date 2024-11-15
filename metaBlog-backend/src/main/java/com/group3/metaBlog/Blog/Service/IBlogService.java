package com.group3.metaBlog.Blog.Service;

import com.group3.metaBlog.Blog.DTO.BlogRequestDto;
import org.springframework.http.ResponseEntity;

public interface IBlogService {
    ResponseEntity<Object> createBlog(BlogRequestDto blogRequestDto, String token);
    ResponseEntity<Object> getAllBlogs();
    ResponseEntity<Object> getBlogsByUser(String token);
    ResponseEntity<Object> searchBlogsByTitle(String title);
    ResponseEntity<Object> getBlogById(Long id);
}
