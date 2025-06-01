package com.gitittogether.skillForge.server.dto.response;

import lombok.*;

@Data
@AllArgsConstructor
public class UserLoginResponse {
    private String id;
    private String firstName;
    private String lastName;
    private String username;
    private String email;
    private String profilePictureUrl;
    private String jwtToken;
}