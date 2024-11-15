package com.group3.metaBlog.User.DataTransferObject;

import com.group3.metaBlog.Enum.BlogStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserBlogResponseDto {
    private Long id;
    private String title;
    private String content;
    private String imageUrl;
    private String author;
    private Double createdOn;
    private Double reviewedOn;
    private int viewCount;
    private BlogStatus status;
}
