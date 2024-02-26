package com.springboot.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ReceivedMessageDTO {
    private Long messageId;
    private Long senderId;
    private String senderUsername;
    private String content;
}
