package com.group3.metaBlog.Comment.Repository;

import com.group3.metaBlog.Comment.Model.Comment;
import com.group3.metaBlog.Blog.Model.Blog;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ICommentRepository extends JpaRepository<Comment, Long> {
    List<Comment> findByBlog(Blog blog);
}
