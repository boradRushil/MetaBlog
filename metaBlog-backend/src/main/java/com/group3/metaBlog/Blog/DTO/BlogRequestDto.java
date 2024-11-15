package com.group3.metaBlog.Blog.DTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import java.io.Serializable;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class BlogRequestDto implements Serializable {
    private String title;
    private String content;
    private MultipartFile image;
    private String name;
    private String description;
}
