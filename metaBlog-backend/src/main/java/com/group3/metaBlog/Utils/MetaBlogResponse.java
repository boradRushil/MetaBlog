package com.group3.metaBlog.Utils;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class MetaBlogResponse<T> {
    private Boolean success;
    private String message;
    private T data;

}
