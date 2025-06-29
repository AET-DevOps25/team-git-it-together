package com.gitittogether.skillForge.server.user.service.user;

import com.gitittogether.skillForge.server.user.config.JwtUtils;
import com.gitittogether.skillForge.server.user.dto.request.user.UserLoginRequest;
import com.gitittogether.skillForge.server.user.dto.request.user.UserProfileUpdateRequest;
import com.gitittogether.skillForge.server.user.dto.request.user.UserRegisterRequest;
import com.gitittogether.skillForge.server.user.dto.response.skill.SkillResponse;
import com.gitittogether.skillForge.server.user.dto.response.user.UserLoginResponse;
import com.gitittogether.skillForge.server.user.dto.response.user.UserProfileResponse;
import com.gitittogether.skillForge.server.user.dto.response.user.UserRegisterResponse;
import com.gitittogether.skillForge.server.user.exception.ResourceNotFoundException;
import com.gitittogether.skillForge.server.user.exception.WrongPasswordException;
import com.gitittogether.skillForge.server.user.mapper.skill.SkillMapper;
import com.gitittogether.skillForge.server.user.mapper.user.UserMapper;
import com.gitittogether.skillForge.server.user.model.user.User;
import com.gitittogether.skillForge.server.user.repository.user.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtils jwtUtils;

    @Override
    @Transactional
    public UserRegisterResponse registerUser(UserRegisterRequest request) {
        log.info("Registering new user: {}", request.getUsername());

        // Check if user already exists
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new IllegalArgumentException("Username already exists");
        }
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("Email already exists");
        }

        User user = User.builder()
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .username(request.getUsername())
                .email(request.getEmail())
                .passwordHash(passwordEncoder.encode(request.getPassword()))
                .build();

        User savedUser = userRepository.save(user);
        log.info("Registered user with ID: {}", savedUser.getId());

        return UserMapper.toUserRegisterResponse(savedUser);
    }

    @Override
    public UserLoginResponse authenticateUser(UserLoginRequest request) {
        log.info("Authenticating user: {}", request.getUsername());

        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPasswordHash())) {
            throw new WrongPasswordException("Invalid password");
        }

        String token = jwtUtils.generateToken(user.getId(), user.getUsername());
        log.info("Authenticated user: {}", user.getUsername());

        return UserMapper.toUserLoginResponse(user, token);
    }

    @Override
    public UserProfileResponse getUserProfile(String userId) {
        log.info("Fetching user profile: {}", userId);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        return UserMapper.toUserProfileResponse(user);
    }

    @Override
    @Transactional
    public UserProfileResponse updateUserProfile(String userId, UserProfileUpdateRequest request) {
        log.info("Updating user profile: {}", userId);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        // Update basic fields
        if (request.getFirstName() != null) {
            user.setFirstName(request.getFirstName());
        }
        if (request.getLastName() != null) {
            user.setLastName(request.getLastName());
        }
        if (request.getUsername() != null) {
            user.setUsername(request.getUsername());
        }
        if (request.getEmail() != null) {
            user.setEmail(request.getEmail());
        }
        if (request.getProfilePictureUrl() != null) {
            user.setProfilePictureUrl(request.getProfilePictureUrl());
        }
        if (request.getBio() != null) {
            user.setBio(request.getBio());
        }

        // Update skills
        if (request.getSkills() != null) {
            user.setSkills(
                    request.getSkills().stream()
                            .map(SkillMapper::requestToSkill)
                            .toList()
            );
        }
        if (request.getSkillsInProgress() != null) {
            user.setSkillsInProgress(
                    request.getSkillsInProgress().stream()
                            .map(SkillMapper::requestToSkill)
                            .toList()
            );
        }

        User savedUser = userRepository.save(user);
        log.info("Updated user profile: {}", savedUser.getId());

        return UserMapper.toUserProfileResponse(savedUser);
    }

    @Override
    @Transactional
    public boolean deleteUser(String userId) {
        if (isBlank(userId)) {
            throw new IllegalArgumentException("User ID cannot be null or empty");
        }
        User user = userRepository.findById(userId).orElseThrow(() -> new ResourceNotFoundException("User not found"));
        userRepository.delete(user);
        log.info("Deleted user with ID: {}", userId);
        return true;
    }

    @Transactional
    public boolean addSkillToUser(String userId, SkillResponse skillResponse) {
        if (skillResponse == null || isBlank(userId)) {
            throw new IllegalArgumentException("User ID and/or skill response cannot be null or empty");
        }
        User user = userRepository.findById(userId).orElseThrow(() -> new ResourceNotFoundException("User not found"));

        if (user.getSkills() == null) {
            user.setSkills(new ArrayList<>());
        }
        // Check if the skill is already added
        if (user.getSkills().stream()
                .anyMatch(skill -> skill.getId().equals(skillResponse.getId()))) {
            log.warn("User {} already has skill {}", userId, skillResponse.getId());
            return false; // Skill is already added
        }
        user.getSkills().add(SkillMapper.responseToSkill(skillResponse));
        userRepository.save(user);
        log.info("Added skill {} to user {}", skillResponse.getId(), userId);
        return true;
    }

    @Transactional
    public boolean removeSkillFromUser(String userId, SkillResponse skill) {
        if (skill == null || isBlank(userId)) {
            throw new IllegalArgumentException("User ID and/or skill cannot be null or empty");
        }
        User user = userRepository.findById(userId).orElseThrow(() -> new ResourceNotFoundException("User not found"));

        if (user.getSkills() != null && !user.getSkills().isEmpty()) {
            boolean removed = user.getSkills().removeIf(s -> s.getId().equals(skill.getId()));
            if (!removed) {
                log.warn("Skill {} was not found in user's skills {}", skill.getId(), userId);
                return false; // Skill was not in user's skills
            } else {
                userRepository.save(user);
                log.info("Removed skill {} from user {}", skill.getId(), userId);
                return true;
            }
        } else {
            log.warn("User {} has no skills to remove", userId);
            return false;
        }
    }

    @Transactional
    public boolean addSkillInProgress(String userId, SkillResponse skillResponse) {
        if (skillResponse == null || isBlank(userId)) {
            throw new IllegalArgumentException("User ID and/or skill response cannot be null or empty");
        }
        User user = userRepository.findById(userId).orElseThrow(() -> new ResourceNotFoundException("User not found"));

        if (user.getSkillsInProgress() == null) {
            user.setSkillsInProgress(new ArrayList<>());
        }
        // Check if the skill is already in progress
        if (user.getSkillsInProgress().stream()
                .anyMatch(skill -> skill.getId().equals(skillResponse.getId()))) {
            log.warn("User {} already has skill {} in progress", userId, skillResponse.getId());
            return false; // Skill is already in progress
        }
        user.getSkillsInProgress().add(SkillMapper.responseToSkill(skillResponse));
        userRepository.save(user);
        log.info("Added skill {} to user's in-progress skills {}", skillResponse.getId(), userId);
        return true;
    }

    @Transactional
    public boolean removeSkillInProgress(String userId, SkillResponse skill) {
        if (skill == null || isBlank(userId)) {
            throw new IllegalArgumentException("User ID and/or skill cannot be null or empty");
        }
        User user = userRepository.findById(userId).orElseThrow(() -> new ResourceNotFoundException("User not found"));

        if (user.getSkillsInProgress() != null && !user.getSkillsInProgress().isEmpty()) {
            boolean removed = user.getSkillsInProgress().removeIf(s -> s.getId().equals(skill.getId()));
            if (!removed) {
                log.warn("Skill {} was not found in user's in-progress skills {}", skill.getId(), userId);
                return false; // Skill was not in user's in-progress skills
            } else {
                userRepository.save(user);
                log.info("Removed skill {} from user's in-progress skills {}", skill.getId(), userId);
                return true;
            }
        } else {
            log.warn("User {} has no in-progress skills to remove", userId);
            return false;
        }
    }

    private boolean isBlank(String str) {
        return str == null || str.trim().isEmpty();
    }
}