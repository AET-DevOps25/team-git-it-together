package com.gitittogether.skillForge.server.course.dto.request.course;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import jakarta.validation.constraints.NotBlank;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EnrolledUserInfoRequest {
    @NotBlank
    private String userId;
    @Builder.Default
    private float progress = 0.0f;
    @Builder.Default
    private List<String> skills = new ArrayList<>();
}
