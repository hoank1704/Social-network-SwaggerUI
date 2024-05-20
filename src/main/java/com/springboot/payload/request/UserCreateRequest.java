package com.springboot.payload.request;

import com.springboot.entities.ERole;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserCreateRequest {
    private String username;
    private String password;
    private String email;
    //private byte[] avatar;
    private Date birthDate;
    private String job;
    private String location;
    //private Set<Role> roles = new HashSet<>();
    private ERole role;
}
