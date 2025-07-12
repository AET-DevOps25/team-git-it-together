package com.gitittogether.skillForge.server.course.model.course;

import com.gitittogether.skillForge.server.course.model.utils.Language;
import com.gitittogether.skillForge.server.course.model.utils.Level;
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
    @Builder.Default
    private String instructor = "AI"; // Reference to User who created the course - can be "AI" or a real user in this case use ID of the user

    @Builder.Default
    private List<String> skills = new ArrayList<>(); // Skills that can be learned in this course

    @Builder.Default
    private List<Module> modules = new ArrayList<>();

    @Builder.Default
    private List<EnrolledUserInfo> enrolledUsers = new ArrayList<>(); // List of enrolled users with progress and skills

    @Builder.Default
    private Integer numberOfEnrolledUsers = 0;

    @Builder.Default
    private List<String> categories = new ArrayList<>(); // Course categories

    @Builder.Default
    private Level level = Level.BEGINNER; // Default level is BEGINNER

    private String thumbnailUrl;

    @Builder.Default
    private Boolean published = true; // Indicates if the course is published and available to users

    @Builder.Default
    private Boolean isPublic = true; // Indicates if the course is public (showcased on landing page)

    @Builder.Default
    private Language language = Language.EN; // Default language is English

    private double rating;
}