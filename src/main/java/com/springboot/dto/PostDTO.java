package com.springboot.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class PostDTO {
    private String content;
    private LocalDateTime createdAt;
    private int commentCount;
    private int likeCount;
    private Long userId;
    private String imageUrl;
}
