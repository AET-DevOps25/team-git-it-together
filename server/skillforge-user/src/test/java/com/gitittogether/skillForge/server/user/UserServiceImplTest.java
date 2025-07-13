package com.gitittogether.skillForge.server.user;

import com.gitittogether.skillForge.server.user.config.JwtUtils;
import com.gitittogether.skillForge.server.user.dto.request.user.UserLoginRequest;
import com.gitittogether.skillForge.server.user.dto.request.user.UserProfileUpdateRequest;
import com.gitittogether.skillForge.server.user.dto.request.user.UserRegisterRequest;
import com.gitittogether.skillForge.server.user.dto.response.user.UserLoginResponse;
import com.gitittogether.skillForge.server.user.dto.response.user.UserProfileResponse;
import com.gitittogether.skillForge.server.user.dto.response.user.UserRegisterResponse;
import com.gitittogether.skillForge.server.user.exception.ResourceNotFoundException;
import com.gitittogether.skillForge.server.user.exception.WrongPasswordException;
import com.gitittogether.skillForge.server.user.model.user.User;
import com.gitittogether.skillForge.server.user.repository.user.UserRepository;
import com.gitittogether.skillForge.server.user.service.user.UserServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("UserService Implementation Tests")
class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtUtils jwtUtils;

    @InjectMocks
    private UserServiceImpl userService;

    private User testUser;
    private UserRegisterRequest registerRequest;
    private UserLoginRequest loginRequest;
    private UserProfileUpdateRequest updateRequest;

    @BeforeEach
    void setUp() {
        testUser = User.builder()
                .id("user123")
                .firstName("John")
                .lastName("Doe")
                .username("johndoe")
                .email("john.doe@example.com")
                .passwordHash("encodedPassword123")
                .profilePictureUrl("https://example.com/avatar.jpg")
                .bio("Software Developer")
                .skills(new ArrayList<>())
                .skillsInProgress(new ArrayList<>())
                .bookmarkedCourseIds(new ArrayList<>())
                .enrolledCourseIds(new ArrayList<>())
                .completedCourseIds(new ArrayList<>())
                .build();

        registerRequest = UserRegisterRequest.builder()
                .firstName("John")
                .lastName("Doe")
                .username("johndoe")
                .email("john.doe@example.com")
                .password("password123")
                .build();

        loginRequest = UserLoginRequest.builder()
                .username("johndoe")
                .password("password123")
                .build();

        updateRequest = UserProfileUpdateRequest.builder()
                .firstName("Jane")
                .lastName("Smith")
                .bio("Updated bio")
                .build();
    }

    @Test
    @DisplayName("Should create user successfully")
    void shouldCreateUserSuccessfully() {
        // Given
        when(userRepository.existsByUsername(anyString())).thenReturn(false);
        when(userRepository.existsByEmail(anyString())).thenReturn(false);
        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword123");
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        // When
        UserRegisterResponse response = userService.registerUser(registerRequest);

        // Then
        assertThat(response).isNotNull();
        assertThat(response.getId()).isEqualTo("user123");
        assertThat(response.getFirstName()).isEqualTo("John");
        assertThat(response.getLastName()).isEqualTo("Doe");
        assertThat(response.getUsername()).isEqualTo("johndoe");
        assertThat(response.getEmail()).isEqualTo("john.doe@example.com");

        verify(userRepository).existsByUsername("johndoe");
        verify(userRepository).existsByEmail("john.doe@example.com");
        verify(passwordEncoder).encode("password123");
        verify(userRepository).save(any(User.class));
    }

    @Test
    @DisplayName("Should throw exception when username already exists")
    void shouldThrowExceptionWhenUsernameAlreadyExists() {
        // Given
        when(userRepository.existsByUsername(anyString())).thenReturn(true);

        // When & Then
        assertThatThrownBy(() -> userService.registerUser(registerRequest))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Username already exists");

        verify(userRepository).existsByUsername("johndoe");
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    @DisplayName("Should throw exception when email already exists")
    void shouldThrowExceptionWhenEmailAlreadyExists() {
        // Given
        when(userRepository.existsByUsername(anyString())).thenReturn(false);
        when(userRepository.existsByEmail(anyString())).thenReturn(true);

        // When & Then
        assertThatThrownBy(() -> userService.registerUser(registerRequest))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Email already exists");

        verify(userRepository).existsByUsername("johndoe");
        verify(userRepository).existsByEmail("john.doe@example.com");
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    @DisplayName("Should find user by ID successfully")
    void shouldFindUserByIdSuccessfully() {
        // Given
        when(userRepository.findById("user123")).thenReturn(Optional.of(testUser));

        // When
        UserProfileResponse response = userService.getUser("user123");

        // Then
        assertThat(response).isNotNull();
        assertThat(response.getId()).isEqualTo("user123");
        assertThat(response.getFirstName()).isEqualTo("John");
        assertThat(response.getLastName()).isEqualTo("Doe");
        assertThat(response.getUsername()).isEqualTo("johndoe");
        assertThat(response.getEmail()).isEqualTo("john.doe@example.com");

        verify(userRepository).findById("user123");
    }

    @Test
    @DisplayName("Should throw ResourceNotFoundException when user not found by ID")
    void shouldThrowResourceNotFoundExceptionWhenUserNotFoundById() {
        // Given
        when(userRepository.findById("nonexistent")).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> userService.getUser("nonexistent"))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("User not found");

        verify(userRepository).findById("nonexistent");
    }

    @Test
    @DisplayName("Should authenticate user successfully")
    void shouldAuthenticateUserSuccessfully() {
        // Given
        when(userRepository.findByUsername("johndoe")).thenReturn(Optional.of(testUser));
        when(passwordEncoder.matches("password123", "encodedPassword123")).thenReturn(true);
        when(jwtUtils.generateToken("user123", "johndoe")).thenReturn("jwt-token-123");

        // When
        UserLoginResponse response = userService.authenticateUser(loginRequest);

        // Then
        assertThat(response).isNotNull();
        assertThat(response.getId()).isEqualTo("user123");
        assertThat(response.getUsername()).isEqualTo("johndoe");
        assertThat(response.getJwtToken()).isEqualTo("jwt-token-123");

        verify(userRepository).findByUsername("johndoe");
        verify(passwordEncoder).matches("password123", "encodedPassword123");
        verify(jwtUtils).generateToken("user123", "johndoe");
    }

    @Test
    @DisplayName("Should throw ResourceNotFoundException when user not found during authentication")
    void shouldThrowResourceNotFoundExceptionWhenUserNotFoundDuringAuthentication() {
        // Given
        when(userRepository.findByUsername("nonexistent")).thenReturn(Optional.empty());

        UserLoginRequest nonExistentLoginRequest = UserLoginRequest.builder()
                .username("nonexistent")
                .password("password123")
                .build();

        // When & Then
        assertThatThrownBy(() -> userService.authenticateUser(nonExistentLoginRequest))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("User not found");

        verify(userRepository).findByUsername("nonexistent");
        verify(passwordEncoder, never()).matches(anyString(), anyString());
    }

    @Test
    @DisplayName("Should throw WrongPasswordException when password is incorrect")
    void shouldThrowWrongPasswordExceptionWhenPasswordIsIncorrect() {
        // Given
        when(userRepository.findByUsername("johndoe")).thenReturn(Optional.of(testUser));
        when(passwordEncoder.matches("wrongpassword", "encodedPassword123")).thenReturn(false);

        UserLoginRequest wrongPasswordRequest = UserLoginRequest.builder()
                .username("johndoe")
                .password("wrongpassword")
                .build();

        // When & Then
        assertThatThrownBy(() -> userService.authenticateUser(wrongPasswordRequest))
                .isInstanceOf(WrongPasswordException.class)
                .hasMessage("Invalid password");

        verify(userRepository).findByUsername("johndoe");
        verify(passwordEncoder).matches("wrongpassword", "encodedPassword123");
        verify(jwtUtils, never()).generateToken(anyString(), anyString());
    }

    @Test
    @DisplayName("Should update user successfully")
    void shouldUpdateUserSuccessfully() {
        // Given
        User updatedUser = User.builder()
                .id("user123")
                .firstName("Jane")
                .lastName("Smith")
                .username("johndoe")
                .email("john.doe@example.com")
                .passwordHash("encodedPassword123")
                .bio("Updated bio")
                .build();

        when(userRepository.findById("user123")).thenReturn(Optional.of(testUser));
        when(userRepository.save(any(User.class))).thenReturn(updatedUser);

        // When
        UserProfileResponse response = userService.updateUser("user123", updateRequest);

        // Then
        assertThat(response).isNotNull();
        assertThat(response.getId()).isEqualTo("user123");
        assertThat(response.getFirstName()).isEqualTo("Jane");
        assertThat(response.getLastName()).isEqualTo("Smith");
        assertThat(response.getBio()).isEqualTo("Updated bio");

        verify(userRepository).findById("user123");
        verify(userRepository).save(any(User.class));
    }

    @Test
    @DisplayName("Should throw ResourceNotFoundException when updating non-existent user")
    void shouldThrowResourceNotFoundExceptionWhenUpdatingNonExistentUser() {
        // Given
        when(userRepository.findById("nonexistent")).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> userService.updateUser("nonexistent", updateRequest))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("User not found");

        verify(userRepository).findById("nonexistent");
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    @DisplayName("Should delete user successfully")
    void shouldDeleteUserSuccessfully() {
        // Given
        when(userRepository.findById("user123")).thenReturn(Optional.of(testUser));
        doNothing().when(userRepository).delete(testUser);

        // When
        boolean result = userService.deleteUser("user123");

        // Then
        assertThat(result).isTrue();

        verify(userRepository).findById("user123");
        verify(userRepository).delete(testUser);
    }

    @Test
    @DisplayName("Should throw ResourceNotFoundException when deleting non-existent user")
    void shouldThrowResourceNotFoundExceptionWhenDeletingNonExistentUser() {
        // Given
        when(userRepository.findById("nonexistent")).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> userService.deleteUser("nonexistent"))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("User not found");

        verify(userRepository).findById("nonexistent");
        verify(userRepository, never()).delete(any(User.class));
    }

    @Test
    @DisplayName("Should throw IllegalArgumentException when deleting user with null ID")
    void shouldThrowIllegalArgumentExceptionWhenDeletingUserWithNullId() {
        // When & Then
        assertThatThrownBy(() -> userService.deleteUser(null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("User ID cannot be null or empty");

        verify(userRepository, never()).findById(anyString());
        verify(userRepository, never()).delete(any(User.class));
    }

    @Test
    @DisplayName("Should throw IllegalArgumentException when deleting user with empty ID")
    void shouldThrowIllegalArgumentExceptionWhenDeletingUserWithEmptyId() {
        // When & Then
        assertThatThrownBy(() -> userService.deleteUser(""))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("User ID cannot be null or empty");

        verify(userRepository, never()).findById(anyString());
        verify(userRepository, never()).delete(any(User.class));
    }

    @Test
    @DisplayName("Should bookmark course successfully")
    void shouldBookmarkCourseSuccessfully() {
        // Given
        when(userRepository.findById("user123")).thenReturn(Optional.of(testUser));
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        // When
        userService.bookmarkCourse("user123", "course456");

        // Then
        verify(userRepository).findById("user123");
        verify(userRepository).save(any(User.class));
    }

    @Test
    @DisplayName("Should throw IllegalArgumentException when bookmarking already bookmarked course")
    void shouldThrowIllegalArgumentExceptionWhenBookmarkingAlreadyBookmarkedCourse() {
        // Given
        testUser.getBookmarkedCourseIds().add("course456");
        when(userRepository.findById("user123")).thenReturn(Optional.of(testUser));

        // When & Then
        assertThatThrownBy(() -> userService.bookmarkCourse("user123", "course456"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Course is already bookmarked");

        verify(userRepository).findById("user123");
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    @DisplayName("Should get bookmarked course IDs successfully")
    void shouldGetBookmarkedCourseIdsSuccessfully() {
        // Given
        testUser.getBookmarkedCourseIds().add("course123");
        testUser.getBookmarkedCourseIds().add("course456");
        when(userRepository.findById("user123")).thenReturn(Optional.of(testUser));

        // When
        List<String> bookmarkedCourses = userService.getBookmarkedCourseIds("user123");

        // Then
        assertThat(bookmarkedCourses).hasSize(2);
        assertThat(bookmarkedCourses).contains("course123", "course456");

        verify(userRepository).findById("user123");
    }

    @Test
    @DisplayName("Should return empty list when user has no bookmarked courses")
    void shouldReturnEmptyListWhenUserHasNoBookmarkedCourses() {
        // Given
        when(userRepository.findById("user123")).thenReturn(Optional.of(testUser));

        // When
        List<String> bookmarkedCourses = userService.getBookmarkedCourseIds("user123");

        // Then
        assertThat(bookmarkedCourses).isEmpty();

        verify(userRepository).findById("user123");
    }

    @Test
    @DisplayName("Should unbookmark course successfully")
    void shouldUnbookmarkCourseSuccessfully() {
        // Given
        testUser.getBookmarkedCourseIds().add("course456");
        when(userRepository.findById("user123")).thenReturn(Optional.of(testUser));
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        // When
        userService.unbookmarkCourse("user123", "course456");

        // Then
        verify(userRepository).findById("user123");
        verify(userRepository).save(any(User.class));
    }

    @Test
    @DisplayName("Should throw IllegalArgumentException when unbookmarking non-bookmarked course")
    void shouldThrowIllegalArgumentExceptionWhenUnbookmarkingNonBookmarkedCourse() {
        // Given
        when(userRepository.findById("user123")).thenReturn(Optional.of(testUser));

        // When & Then
        assertThatThrownBy(() -> userService.unbookmarkCourse("user123", "course456"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Course is not bookmarked");

        verify(userRepository).findById("user123");
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void shouldBookmarkCourseSuccessfully_service() {
        // Given
        when(userRepository.findById("user123")).thenReturn(Optional.of(testUser));
        when(userRepository.save(any(User.class))).thenReturn(testUser);
        // When
        userService.bookmarkCourse("user123", "course456");
        // Then
        assertThat(testUser.getBookmarkedCourseIds()).contains("course456");
        verify(userRepository).save(any(User.class));
    }

    @Test
    void shouldThrowWhenBookmarkingAlreadyBookmarkedCourse() {
        testUser.getBookmarkedCourseIds().add("course456");
        when(userRepository.findById("user123")).thenReturn(Optional.of(testUser));
        assertThatThrownBy(() -> userService.bookmarkCourse("user123", "course456"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Course is already bookmarked");
    }

    @Test
    void shouldThrowWhenBookmarkingForNonexistentUser() {
        when(userRepository.findById("user123")).thenReturn(Optional.empty());
        assertThatThrownBy(() -> userService.bookmarkCourse("user123", "course456"))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("User not found");
    }

    @Test
    void shouldUnbookmarkCourseSuccessfully_service() {
        testUser.getBookmarkedCourseIds().add("course456");
        when(userRepository.findById("user123")).thenReturn(Optional.of(testUser));
        when(userRepository.save(any(User.class))).thenReturn(testUser);
        userService.unbookmarkCourse("user123", "course456");
        assertThat(testUser.getBookmarkedCourseIds()).doesNotContain("course456");
        verify(userRepository).save(any(User.class));
    }

    @Test
    void shouldThrowWhenUnbookmarkingNotBookmarkedCourse() {
        when(userRepository.findById("user123")).thenReturn(Optional.of(testUser));
        assertThatThrownBy(() -> userService.unbookmarkCourse("user123", "course456"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Course is not bookmarked");
    }

    @Test
    void shouldThrowWhenUnbookmarkingForNonexistentUser() {
        when(userRepository.findById("user123")).thenReturn(Optional.empty());
        assertThatThrownBy(() -> userService.unbookmarkCourse("user123", "course456"))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("User not found");
    }

    @Test
    void shouldEnrollUserInCourseSuccessfully_service() {
        when(userRepository.findById("user123")).thenReturn(Optional.of(testUser));
        when(userRepository.save(any(User.class))).thenReturn(testUser);
        userService.enrollUserInCourse("user123", "course456", java.util.List.of("skill1"));
        assertThat(testUser.getEnrolledCourseIds()).contains("course456");
        assertThat(testUser.getSkillsInProgress()).contains("skill1");
        verify(userRepository).save(any(User.class));
    }

    @Test
    void shouldNotDuplicateEnrollment() {
        testUser.getEnrolledCourseIds().add("course456");
        when(userRepository.findById("user123")).thenReturn(Optional.of(testUser));
        userService.enrollUserInCourse("user123", "course456", java.util.List.of("skill1"));
        assertThat(testUser.getEnrolledCourseIds()).containsExactly("course456");
    }

    @Test
    void shouldThrowWhenEnrollingNonexistentUser() {
        when(userRepository.findById("user123")).thenReturn(Optional.empty());
        assertThatThrownBy(() -> userService.enrollUserInCourse("user123", "course456", java.util.List.of("skill1")))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("User not found");
    }

    @Test
    void shouldUnenrollUserFromCourseSuccessfully_service() {
        testUser.getEnrolledCourseIds().add("course456");
        testUser.getSkillsInProgress().add("skill1");
        when(userRepository.findById("user123")).thenReturn(Optional.of(testUser));
        when(userRepository.save(any(User.class))).thenReturn(testUser);
        userService.unenrollUserFromCourse("user123", "course456", java.util.List.of("skill1"));
        assertThat(testUser.getEnrolledCourseIds()).doesNotContain("course456");
        verify(userRepository).save(any(User.class));
    }

    @Test
    void shouldNotFailWhenUnenrollingNotEnrolledCourse() {
        when(userRepository.findById("user123")).thenReturn(Optional.of(testUser));
        userService.unenrollUserFromCourse("user123", "course456", java.util.List.of("skill1"));
        assertThat(testUser.getEnrolledCourseIds()).doesNotContain("course456");
    }

    @Test
    void shouldThrowWhenUnenrollingNonexistentUser() {
        when(userRepository.findById("user123")).thenReturn(Optional.empty());
        assertThatThrownBy(() -> userService.unenrollUserFromCourse("user123", "course456", java.util.List.of("skill1")))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("User not found");
    }
} 