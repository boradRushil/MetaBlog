package com.group3.metaBlog.Blog.DTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class BlogResponseDto {
    private Long id;
    private String title;
    private String content;
    private String imageUrl;
    private String author;
    private Double createdOn;
    private String author_image_url;
    private String status;
    private String description;
    private String name;
}
