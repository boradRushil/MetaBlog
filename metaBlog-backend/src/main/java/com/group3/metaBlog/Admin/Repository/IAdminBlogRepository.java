package com.group3.metaBlog.Admin.Repository;

import com.group3.metaBlog.Blog.Model.Blog;
import com.group3.metaBlog.Enum.BlogStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IAdminBlogRepository extends JpaRepository<Blog, Long> {
    List<Blog> findByStatus(BlogStatus status);
}
