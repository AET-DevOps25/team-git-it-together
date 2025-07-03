package com.gitittogether.skillForge.server.user.model.user;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Document(collection = "users")
public class User {
    @Id
    private String id;

    // Required fields
    @NonNull
    private String firstName;

    @NonNull
    private String lastName;

    @NonNull
    private String username;

    @NonNull
    private String email;

    @NonNull
    private String passwordHash;

    // Optional fields
    private String profilePictureUrl;
    private String bio;

    // Skills
    @Builder.Default
    private List<String> skills = new ArrayList<>(); // List of skills the user has mastered

    @Builder.Default
    private List<String> skillsInProgress = new ArrayList<>(); // List of skills the user is currently learning or practicing

    // User's bookmarked courses (user domain)
    @Builder.Default
    private List<String> bookmarkedCourseIds = new ArrayList<>(); // List of course IDs the user has bookmarked

    // User's enrolled courses (for quick lookups)
    @Builder.Default
    private List<String> enrolledCourseIds = new ArrayList<>(); // List of course IDs the user is enrolled in

    // User's completed courses (for quick lookups)
    @Builder.Default
    private List<String> completedCourseIds = new ArrayList<>(); // List of course IDs the user has completed
}
