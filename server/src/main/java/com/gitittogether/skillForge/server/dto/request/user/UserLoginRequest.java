package com.gitittogether.skillForge.server.dto.request.user;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserLoginRequest {
    @NotNull
    private String username;

    @NotNull
    private String email;

    @NotBlank
    private String password;
}