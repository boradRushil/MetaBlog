package com.group3.metaBlog.Blog.Repository;

import com.group3.metaBlog.Blog.Model.Blog;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface IBlogRepository extends JpaRepository<Blog, Long> {
    List<Blog> findByTitleContaining(String title);

    boolean existsByTitleAndContent(String title, String content);

    List<Blog> findByAuthorId(Long authorId);
}
