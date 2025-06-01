package com.gitittogether.skillForge.server.dto.response;

import lombok.*;

@Data
@AllArgsConstructor
public class UserRegisterResponse {
    private String id;
    private String firstName;
    private String lastName;
    private String username;
    private String email;
}