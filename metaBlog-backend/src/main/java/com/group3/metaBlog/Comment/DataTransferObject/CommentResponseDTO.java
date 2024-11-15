package com.group3.metaBlog.Comment.DataTransferObject;

import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CommentResponseDTO {
    private Long id;
    private String content;
    private String author;
    private String author_image_url;
    private Double createdOn;

}
