package com.gitittogether.skillForge.server.course.model.course;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Document(collection = "user_bookmarks")
public class UserBookmark {

    @Id
    private String id;

    @NonNull
    private String userId;

    @NonNull
    private String courseId;

    @Builder.Default
    private LocalDateTime bookmarkedAt = LocalDateTime.now();
} 