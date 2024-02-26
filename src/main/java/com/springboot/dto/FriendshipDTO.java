package com.springboot.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class FriendshipDTO {
    private Long id;
    private Long user1Id;
    private Long user2Id;
    private String status;
}
