package com.group3.metaBlog.Comment.Controller;

import com.group3.metaBlog.Comment.DataTransferObject.CreateCommentDto;
import com.group3.metaBlog.Comment.Service.ICommentService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/comments")
@AllArgsConstructor
public class CommentController {

    private final ICommentService commentService;

    @PostMapping
    public ResponseEntity<Object> createComment(@RequestBody CreateCommentDto request, @RequestHeader("Authorization") String token){
        return commentService.createComment(request, token);
    }

    @GetMapping("/{blogId}")
    public ResponseEntity<Object> getCommentsByBlog(@PathVariable Long blogId) {
        return commentService.getCommentsByBlog(blogId);
    }
}
