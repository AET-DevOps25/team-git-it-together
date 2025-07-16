package com.gitittogether.skillForge.server.course.dto.response.utils;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PromptResponse {
    private String prompt;
    private String generated_text;
    private String provider;

}