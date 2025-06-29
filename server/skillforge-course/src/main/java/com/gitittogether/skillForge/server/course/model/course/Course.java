package com.gitittogether.skillForge.server.course.model.course;

import com.gitittogether.skillForge.server.course.model.utils.Language;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Document(collection = "courses")
public class Course {

    @Id
    private String id;

    @NonNull
    private String title;

    @NonNull
    private String description;

    @NonNull
    private String instructor; // Reference to User who created the course - can be "AI" or a real user

    @Builder.Default
    private List<String> skills = new ArrayList<>(); // Skills that can be learned in this course

    @Builder.Default
    private List<Module> modules = new ArrayList<>();

    @Builder.Default
    private List<String> enrolledUserIds = new ArrayList<>();

    @Builder.Default
    private Integer numberOfEnrolledUsers = 0;

    @Builder.Default
    private List<String> categories = new ArrayList<>(); // Course categories

    @Builder.Default
    private Level level = Level.BEGINNER; // Default level is BEGINNER

    private String thumbnailUrl;

    private boolean published; // Indicates if the course is published (available for enrollment)

    @Builder.Default
    private boolean isPublic = false; // Indicates if the course is public (showcased on landing page)

    private Language language; // Language of the course content

    private double rating;
}