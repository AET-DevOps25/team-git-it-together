package com.gitittogether.skillForge.server.dto.request.course;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CategoryRequest {
    @NonNull
    private String id;
    @NonNull
    private String name;
    private String description;
}
