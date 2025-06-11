package com.gitittogether.skillForge.server.user.model.user;

import com.gitittogether.skillForge.server.user.model.course.Category;
import com.gitittogether.skillForge.server.user.model.course.Course;
import com.gitittogether.skillForge.server.user.model.course.EnrolledCourse;
import com.gitittogether.skillForge.server.user.model.skill.Skill;
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

    @Builder.Default
    private List<Category> interests = new ArrayList<>(); // Interests are categories the user is interested in

    // Skills
    @Builder.Default
    private List<Skill> skills = new ArrayList<>(); // List of skills the user has mastered

    @Builder.Default
    private List<Skill> skillsInProgress = new ArrayList<>(); // List of skills the user is currently learning or practicing

    // Courses
    @Builder.Default
    private List<EnrolledCourse> enrolledCourses = new ArrayList<>(); // List of courses the user is enrolled in, with progress tracking

    @Builder.Default
    private List<Course> bookmarkedCourses = new ArrayList<>();

    @Builder.Default
    private List<EnrolledCourse> completedCourses = new ArrayList<>(); // List of courses the user has completed
}
