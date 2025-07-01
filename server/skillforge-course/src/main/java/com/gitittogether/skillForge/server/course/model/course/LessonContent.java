package com.gitittogether.skillForge.server.course.model.course;

import com.gitittogether.skillForge.server.course.model.utils.LessonContentType;
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
