package com.group3.metaBlog.Admin.DTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AdminResponseDto {
    private Long id;
    private String title;
    private String content;
    private String imageUrl;
    private String author;
    private Double createdOn;
    private String status;
}
