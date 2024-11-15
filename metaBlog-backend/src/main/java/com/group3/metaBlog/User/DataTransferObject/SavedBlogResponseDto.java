package com.group3.metaBlog.User.DataTransferObject;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SavedBlogResponseDto {
    private Long id;
    private String title;
    private String imageUrl;
    private String author;
    private String author_image_url;
    private Double createdOn;
}
