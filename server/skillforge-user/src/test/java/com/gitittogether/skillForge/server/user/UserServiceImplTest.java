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


    @Test
    @DisplayName("Should complete course successfully")
    void shouldCompleteCourseSuccessfully() {
        // Given
        User userWithEnrollment = User.builder()
                .id("user123")
                .firstName("John")
                .lastName("Doe")
                .username("johndoe")
                .email("john.doe@example.com")
                .passwordHash("encodedPassword123")
                .enrolledCourseIds(new ArrayList<>(List.of("course456")))
                .skillsInProgress(new ArrayList<>(List.of("skill1", "skill2")))
                .skills(new ArrayList<>())
                .completedCourseIds(new ArrayList<>())
                .build();
        when(userRepository.findById("user123")).thenReturn(Optional.of(userWithEnrollment));
        when(userRepository.save(any(User.class))).thenReturn(userWithEnrollment);

        // When
        userService.completeCourse("user123", "course456", List.of("skill1", "skill2"));

        // Then
        verify(userRepository).findById("user123");
        verify(userRepository).save(any(User.class));
    }

    @Test
    @DisplayName("Should handle course completion when already completed")
    void shouldHandleCourseCompletionWhenAlreadyCompleted() {
        // Given
        User userWithCompletedCourse = User.builder()
                .id("user123")
                .firstName("John")
                .lastName("Doe")
                .username("johndoe")
                .email("john.doe@example.com")
                .passwordHash("encodedPassword123")
                .completedCourseIds(List.of("course456"))
                .build();
        when(userRepository.findById("user123")).thenReturn(Optional.of(userWithCompletedCourse));

        // When
        userService.completeCourse("user123", "course456", List.of("skill1"));

        // Then
        verify(userRepository).findById("user123");
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    @DisplayName("Should get user skills successfully")
    void shouldGetUserSkillsSuccessfully() {
        // Given
        User userWithSkills = User.builder()
                .id("user123")
                .firstName("John")
                .lastName("Doe")
                .username("johndoe")
                .email("john.doe@example.com")
                .passwordHash("encodedPassword123")
                .skills(List.of("Java", "Spring", "React"))
                .build();
        when(userRepository.findById("user123")).thenReturn(Optional.of(userWithSkills));

        // When
        List<String> skills = userService.getUserSkills("user123");

        // Then
        assertThat(skills).containsExactly("Java", "Spring", "React");
        verify(userRepository).findById("user123");
    }

    @Test
    @DisplayName("Should return empty list when user has no skills")
    void shouldReturnEmptyListWhenUserHasNoSkills() {
        // Given
        User userWithoutSkills = User.builder()
                .id("user123")
                .firstName("John")
                .lastName("Doe")
                .username("johndoe")
                .email("john.doe@example.com")
                .passwordHash("encodedPassword123")
                .skills(null)
                .build();
        when(userRepository.findById("user123")).thenReturn(Optional.of(userWithoutSkills));

        // When
        List<String> skills = userService.getUserSkills("user123");

        // Then
        assertThat(skills).isEmpty();
        verify(userRepository).findById("user123");
    }

    @Test
    @DisplayName("Should get user skills in progress successfully")
    void shouldGetUserSkillsInProgressSuccessfully() {
        // Given
        User userWithSkillsInProgress = User.builder()
                .id("user123")
                .firstName("John")
                .lastName("Doe")
                .username("johndoe")
                .email("john.doe@example.com")
                .passwordHash("encodedPassword123")
                .skillsInProgress(List.of("Python", "Django"))
                .build();
        when(userRepository.findById("user123")).thenReturn(Optional.of(userWithSkillsInProgress));

        // When
        List<String> skillsInProgress = userService.getUserSkillsInProgress("user123");

        // Then
        assertThat(skillsInProgress).containsExactly("Python", "Django");
        verify(userRepository).findById("user123");
    }

    @Test
    @DisplayName("Should get user enrolled course IDs successfully")
    void shouldGetUserEnrolledCourseIdsSuccessfully() {
        // Given
        User userWithEnrollments = User.builder()
                .id("user123")
                .firstName("John")
                .lastName("Doe")
                .username("johndoe")
                .email("john.doe@example.com")
                .passwordHash("encodedPassword123")
                .enrolledCourseIds(List.of("course1", "course2", "course3"))
                .build();
        when(userRepository.findById("user123")).thenReturn(Optional.of(userWithEnrollments));

        // When
        List<String> enrolledCourses = userService.getUserEnrolledCourseIds("user123");

        // Then
        assertThat(enrolledCourses).containsExactly("course1", "course2", "course3");
        verify(userRepository).findById("user123");
    }

    @Test
    @DisplayName("Should get user completed course IDs successfully")
    void shouldGetUserCompletedCourseIdsSuccessfully() {
        // Given
        User userWithCompletedCourses = User.builder()
                .id("user123")
                .firstName("John")
                .lastName("Doe")
                .username("johndoe")
                .email("john.doe@example.com")
                .passwordHash("encodedPassword123")
                .completedCourseIds(List.of("course1", "course2"))
                .build();
        when(userRepository.findById("user123")).thenReturn(Optional.of(userWithCompletedCourses));

        // When
        List<String> completedCourses = userService.getUserCompletedCourseIds("user123");

        // Then
        assertThat(completedCourses).containsExactly("course1", "course2");
        verify(userRepository).findById("user123");
    }

    @Test
    @DisplayName("Should search users by username successfully")
    void shouldSearchUsersByUsernameSuccessfully() {
        // Given
        List<User> users = List.of(
                User.builder().id("user123").firstName("John").lastName("Doe").username("john_doe").email("john@example.com").passwordHash("hash").build(),
                User.builder().id("user456").firstName("John").lastName("Smith").username("john_smith").email("smith@example.com").passwordHash("hash").build()
        );
        when(userRepository.findUserByUsernameContainingIgnoreCase("john")).thenReturn(users);

        // When
        List<UserProfileResponse> results = userService.searchUsersByUsername("john");

        // Then
        assertThat(results).hasSize(2);
        assertThat(results.get(0).getUsername()).isEqualTo("john_doe");
        assertThat(results.get(1).getUsername()).isEqualTo("john_smith");
        verify(userRepository).findUserByUsernameContainingIgnoreCase("john");
    }

    @Test
    @DisplayName("Should return empty list when searching with blank username")
    void shouldReturnEmptyListWhenSearchingWithBlankUsername() {
        // When
        List<UserProfileResponse> results = userService.searchUsersByUsername("   ");

        // Then
        assertThat(results).isEmpty();
        verify(userRepository, never()).findUserByUsernameContainingIgnoreCase(anyString());
    }

    @Test
    @DisplayName("Should return empty list when searching with null username")
    void shouldReturnEmptyListWhenSearchingWithNullUsername() {
        // When
        List<UserProfileResponse> results = userService.searchUsersByUsername(null);

        // Then
        assertThat(results).isEmpty();
        verify(userRepository, never()).findUserByUsernameContainingIgnoreCase(anyString());
    }

    @Test
    @DisplayName("Should search users by email successfully")
    void shouldSearchUsersByEmailSuccessfully() {
        // Given
        List<User> users = List.of(
                User.builder().id("user123").firstName("John").lastName("Doe").username("johndoe").email("john@example.com").passwordHash("hash").build(),
                User.builder().id("user456").firstName("Jane").lastName("Doe").username("janedoe").email("jane@example.com").passwordHash("hash").build()
        );
        when(userRepository.findUserByEmailContainingIgnoreCase("example")).thenReturn(users);

        // When
        List<UserProfileResponse> results = userService.searchUsersByEmail("example");

        // Then
        assertThat(results).hasSize(2);
        assertThat(results.get(0).getEmail()).isEqualTo("john@example.com");
        assertThat(results.get(1).getEmail()).isEqualTo("jane@example.com");
        verify(userRepository).findUserByEmailContainingIgnoreCase("example");
    }

    @Test
    @DisplayName("Should get users with specific skill")
    void shouldGetUsersWithSpecificSkill() {
        // Given
        List<User> usersWithSkill = List.of(
                User.builder().id("user123").firstName("John").lastName("Doe").username("johndoe").email("john@example.com").passwordHash("hash").skills(List.of("Java")).build(),
                User.builder().id("user456").firstName("Jane").lastName("Doe").username("janedoe").email("jane@example.com").passwordHash("hash").skills(List.of("Java", "Spring")).build()
        );
        when(userRepository.findBySkills("Java")).thenReturn(usersWithSkill);

        // When
        List<UserProfileResponse> results = userService.getUsersWithSkill("Java");

        // Then
        assertThat(results).hasSize(2);
        verify(userRepository).findBySkills("Java");
    }

    @Test
    @DisplayName("Should get users with skill in progress")
    void shouldGetUsersWithSkillInProgress() {
        // Given
        List<User> usersWithSkillInProgress = List.of(
                User.builder().id("user123").firstName("John").lastName("Doe").username("johndoe").email("john@example.com").passwordHash("hash").skillsInProgress(List.of("Python")).build(),
                User.builder().id("user456").firstName("Jane").lastName("Doe").username("janedoe").email("jane@example.com").passwordHash("hash").skillsInProgress(List.of("Python", "Django")).build()
        );
        when(userRepository.findBySkillsInProgress("Python")).thenReturn(usersWithSkillInProgress);

        // When
        List<UserProfileResponse> results = userService.getUsersWithSkillInProgress("Python");

        // Then
        assertThat(results).hasSize(2);
        verify(userRepository).findBySkillsInProgress("Python");
    }

    @Test
    @DisplayName("Should get users enrolled in course")
    void shouldGetUsersEnrolledInCourse() {
        // Given
        List<User> usersEnrolled = List.of(
                User.builder().id("user123").firstName("John").lastName("Doe").username("johndoe").email("john@example.com").passwordHash("hash").enrolledCourseIds(List.of("course123")).build(),
                User.builder().id("user456").firstName("Jane").lastName("Doe").username("janedoe").email("jane@example.com").passwordHash("hash").enrolledCourseIds(List.of("course123", "course456")).build()
        );
        when(userRepository.findByEnrolledCourseIdsContaining("course123")).thenReturn(usersEnrolled);

        // When
        List<UserProfileResponse> results = userService.getUsersEnrolledInCourse("course123");

        // Then
        assertThat(results).hasSize(2);
        verify(userRepository).findByEnrolledCourseIdsContaining("course123");
    }

    @Test
    @DisplayName("Should get users who completed course")
    void shouldGetUsersWhoCompletedCourse() {
        // Given
        List<User> usersCompleted = List.of(
                User.builder().id("user123").firstName("John").lastName("Doe").username("johndoe").email("john@example.com").passwordHash("hash").completedCourseIds(List.of("course123")).build(),
                User.builder().id("user456").firstName("Jane").lastName("Doe").username("janedoe").email("jane@example.com").passwordHash("hash").completedCourseIds(List.of("course123", "course456")).build()
        );
        when(userRepository.findByCompletedCourseIdsContaining("course123")).thenReturn(usersCompleted);

        // When
        List<UserProfileResponse> results = userService.getUsersCompletedCourse("course123");

        // Then
        assertThat(results).hasSize(2);
        verify(userRepository).findByCompletedCourseIdsContaining("course123");
    }

    @Test
    @DisplayName("Should get users who bookmarked course")
    void shouldGetUsersWhoBookmarkedCourse() {
        // Given
        List<User> usersBookmarked = List.of(
                User.builder().id("user123").firstName("John").lastName("Doe").username("johndoe").email("john@example.com").passwordHash("hash").bookmarkedCourseIds(List.of("course123")).build(),
                User.builder().id("user456").firstName("Jane").lastName("Doe").username("janedoe").email("jane@example.com").passwordHash("hash").bookmarkedCourseIds(List.of("course123", "course456")).build()
        );
        when(userRepository.findByBookmarkedCourseIdsContaining("course123")).thenReturn(usersBookmarked);

        // When
        List<UserProfileResponse> results = userService.getUsersBookmarkedCourse("course123");

        // Then
        assertThat(results).hasSize(2);
        verify(userRepository).findByBookmarkedCourseIdsContaining("course123");
    }

    @Test
    @DisplayName("Should handle enrollment with null skills")
    void shouldHandleEnrollmentWithNullSkills() {
        // Given
        User userWithoutEnrollments = User.builder()
                .id("user123")
                .firstName("John")
                .lastName("Doe")
                .username("johndoe")
                .email("john.doe@example.com")
                .passwordHash("encodedPassword123")
                .enrolledCourseIds(new ArrayList<>())
                .skillsInProgress(new ArrayList<>())
                .build();
        when(userRepository.findById("user123")).thenReturn(Optional.of(userWithoutEnrollments));
        when(userRepository.save(any(User.class))).thenReturn(userWithoutEnrollments);

        // When
        userService.enrollUserInCourse("user123", "course456", null);

        // Then
        verify(userRepository).findById("user123");
        verify(userRepository).save(any(User.class));
    }

    @Test
    @DisplayName("Should handle enrollment with empty skills")
    void shouldHandleEnrollmentWithEmptySkills() {
        // Given
        User userWithoutEnrollments = User.builder()
                .id("user123")
                .firstName("John")
                .lastName("Doe")
                .username("johndoe")
                .email("john.doe@example.com")
                .passwordHash("encodedPassword123")
                .enrolledCourseIds(new ArrayList<>())
                .skillsInProgress(new ArrayList<>())
                .build();
        when(userRepository.findById("user123")).thenReturn(Optional.of(userWithoutEnrollments));
        when(userRepository.save(any(User.class))).thenReturn(userWithoutEnrollments);

        // When
        userService.enrollUserInCourse("user123", "course456", List.of());

        // Then
        verify(userRepository).findById("user123");
        verify(userRepository).save(any(User.class));
    }

    @Test
    @DisplayName("Should handle unenrollment with null skills")
    void shouldHandleUnenrollmentWithNullSkills() {
        // Given
        User userWithEnrollment = User.builder()
                .id("user123")
                .firstName("John")
                .lastName("Doe")
                .username("johndoe")
                .email("john.doe@example.com")
                .passwordHash("encodedPassword123")
                .enrolledCourseIds(new ArrayList<>(List.of("course456")))
                .skillsInProgress(new ArrayList<>(List.of("skill1", "skill2")))
                .build();
        when(userRepository.findById("user123")).thenReturn(Optional.of(userWithEnrollment));
        when(userRepository.save(any(User.class))).thenReturn(userWithEnrollment);

        // When
        userService.unenrollUserFromCourse("user123", "course456", null);

        // Then
        verify(userRepository).findById("user123");
        verify(userRepository).save(any(User.class));
    }

    @Test
    @DisplayName("Should handle course completion with null skills")
    void shouldHandleCourseCompletionWithNullSkills() {
        // Given
        User userWithEnrollment = User.builder()
                .id("user123")
                .firstName("John")
                .lastName("Doe")
                .username("johndoe")
                .email("john.doe@example.com")
                .passwordHash("encodedPassword123")
                .enrolledCourseIds(List.of("course456"))
                .skillsInProgress(new ArrayList<>())
                .skills(new ArrayList<>())
                .completedCourseIds(new ArrayList<>())
                .build();
        when(userRepository.findById("user123")).thenReturn(Optional.of(userWithEnrollment));
        when(userRepository.save(any(User.class))).thenReturn(userWithEnrollment);

        // When
        userService.completeCourse("user123", "course456", null);

        // Then
        verify(userRepository).findById("user123");
        verify(userRepository).save(any(User.class));
    }

    @Test
    @DisplayName("Should handle course completion with empty skills")
    void shouldHandleCourseCompletionWithEmptySkills() {
        // Given
        User userWithEnrollment = User.builder()
                .id("user123")
                .firstName("John")
                .lastName("Doe")
                .username("johndoe")
                .email("john.doe@example.com")
                .passwordHash("encodedPassword123")
                .enrolledCourseIds(List.of("course456"))
                .skillsInProgress(new ArrayList<>())
                .skills(new ArrayList<>())
                .completedCourseIds(new ArrayList<>())
                .build();
        when(userRepository.findById("user123")).thenReturn(Optional.of(userWithEnrollment));
        when(userRepository.save(any(User.class))).thenReturn(userWithEnrollment);

        // When
        userService.completeCourse("user123", "course456", List.of());

        // Then
        verify(userRepository).findById("user123");
        verify(userRepository).save(any(User.class));
    }
} 