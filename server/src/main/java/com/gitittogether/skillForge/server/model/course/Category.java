package com.gitittogether.skillForge.server.model.course;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Category {
    @NonNull
    private String id;

    @NonNull
    private String name;

    private String description;

}
