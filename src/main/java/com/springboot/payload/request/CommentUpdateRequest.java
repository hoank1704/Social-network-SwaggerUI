package com.springboot.payload.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CommentUpdateRequest {
    //private Long commentId;
    private String content;
    //private LocalDateTime createdDate;


}
