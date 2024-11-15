package com.group3.metaBlog.Comment.DataTransferObject;

import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CreateCommentDto {
    private String content;
    private Long blogId;
}
