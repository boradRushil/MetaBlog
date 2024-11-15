package com.group3.metaBlog.Comment.Service;

import com.group3.metaBlog.Comment.DataTransferObject.CreateCommentDto;
import org.springframework.http.ResponseEntity;

public interface ICommentService {
    ResponseEntity<Object> createComment(CreateCommentDto request, String token);

    ResponseEntity<Object> getCommentsByBlog(Long blogId);
}
