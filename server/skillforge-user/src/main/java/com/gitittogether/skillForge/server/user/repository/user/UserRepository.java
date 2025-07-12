package com.gitittogether.skillForge.server.user.repository.user;

import com.gitittogether.skillForge.server.user.model.user.User;
import jakarta.validation.constraints.NotBlank;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends MongoRepository<User, String> {

    Optional<User> findByUsername(@NotBlank String username);

    Optional<User> findByEmail(@NotBlank String email);

    boolean existsByEmail(String email);

    boolean existsByUsername(String username);

    List<User> findBySkills(String skill);

    List<User> findBySkillsInProgress(String skill);

    List<User> findByEnrolledCourseIdsContaining(String courseId);

    List<User> findByCompletedCourseIdsContaining(String courseId);

    List<User> findByBookmarkedCourseIdsContaining(String courseId);

    List<User> findUserByUsernameContainingIgnoreCase(String username);

    List<User> findUserByEmailContainingIgnoreCase(String email);

}
