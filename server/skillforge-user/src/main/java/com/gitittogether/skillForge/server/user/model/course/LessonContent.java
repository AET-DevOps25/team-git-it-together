package com.gitittogether.skillForge.server.user.model.course;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LessonContent {
    @NonNull
    LessonContentType type;

    @NonNull
    String content;
}
