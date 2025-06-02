package com.gitittogether.skillForge.server.repository.user;

import com.gitittogether.skillForge.server.model.user.User;
import jakarta.validation.constraints.NotBlank;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface UserRepository extends MongoRepository<User, String> {

    Optional<User> findByUsername(@NotBlank String username);

    Optional<User> findByEmail(@NotBlank String email);

    boolean existsByEmail(String email);

    boolean existsByUsername(String username);

}
