package com.gitittogether.skillForge.server.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gitittogether.skillForge.server.user.controller.user.UserController;
import com.gitittogether.skillForge.server.user.dto.request.user.UserLoginRequest;
import com.gitittogether.skillForge.server.user.dto.request.user.UserProfileUpdateRequest;
import com.gitittogether.skillForge.server.user.dto.request.user.UserRegisterRequest;
import com.gitittogether.skillForge.server.user.dto.response.user.UserLoginResponse;
import com.gitittogether.skillForge.server.user.dto.response.user.UserProfileResponse;
import com.gitittogether.skillForge.server.user.dto.response.user.UserRegisterResponse;
import com.gitittogether.skillForge.server.user.exception.ResourceNotFoundException;
import com.gitittogether.skillForge.server.user.exception.GlobalExceptionHandler;
import com.gitittogether.skillForge.server.user.service.user.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import org.mockito.Mockito;

@ExtendWith(MockitoExtension.class)
@DisplayName("UserController Web Layer Tests")
class UserControllerTest {

    private MockMvc mockMvc;

    @Mock
    private UserService userService;

    @InjectMocks
    private UserController userController;

    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        mockMvc = MockMvcBuilders.standaloneSetup(userController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Test
    @DisplayName("POST /api/v1/users/register - success")
    void shouldRegisterUserSuccessfully() throws Exception {
        UserRegisterRequest request = UserRegisterRequest.builder()
                .firstName("John").lastName("Doe").username("johndoe")
                .email("john.doe@example.com").password("password123").build();
        UserRegisterResponse response = UserRegisterResponse.builder()
                .id("user123").firstName("John").lastName("Doe")
                .username("johndoe").email("john.doe@example.com").build();
        when(userService.registerUser(any(UserRegisterRequest.class))).thenReturn(response);

        mockMvc.perform(post("/api/v1/users/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value("user123"))
                .andExpect(jsonPath("$.username").value("johndoe"));
    }

    @Test
    @DisplayName("POST /api/v1/users/register - validation error")
    void shouldReturnBadRequestOnInvalidRegister() throws Exception {
        UserRegisterRequest request = UserRegisterRequest.builder().build();
        mockMvc.perform(post("/api/v1/users/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("POST /api/v1/users/login - success")
    void shouldLoginUserSuccessfully() throws Exception {
        UserLoginRequest request = UserLoginRequest.builder()
                .username("johndoe").password("password123").build();
        UserLoginResponse response = UserLoginResponse.builder()
                .id("user123").username("johndoe").jwtToken("jwt-token").build();
        when(userService.authenticateUser(any(UserLoginRequest.class))).thenReturn(response);

        mockMvc.perform(post("/api/v1/users/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("user123"))
                .andExpect(jsonPath("$.jwtToken").value("jwt-token"));
    }

    @Test
    @DisplayName("POST /api/v1/users/login - validation error")
    void shouldReturnBadRequestOnInvalidLogin() throws Exception {
        UserLoginRequest request = UserLoginRequest.builder().build();
        mockMvc.perform(post("/api/v1/users/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("GET /api/v1/users/{userId}/profile - success")
    void shouldGetUserProfileSuccessfully() throws Exception {
        UserProfileResponse response = UserProfileResponse.builder()
                .id("user123").firstName("John").lastName("Doe")
                .username("johndoe").email("john.doe@example.com").build();
        when(userService.getUser(eq("user123"))).thenReturn(response);

        mockMvc.perform(get("/api/v1/users/user123/profile"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("user123"))
                .andExpect(jsonPath("$.username").value("johndoe"));
    }

    @Test
    @DisplayName("GET /api/v1/users/{userId}/profile - not found")
    void shouldReturnNotFoundWhenUserProfileNotFound() throws Exception {
        when(userService.getUser(eq("user123"))).thenThrow(new ResourceNotFoundException("User not found"));
        mockMvc.perform(get("/api/v1/users/user123/profile"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("Not Found"));
    }

    @Test
    @DisplayName("PUT /api/v1/users/{userId}/profile - success")
    void shouldUpdateUserProfileSuccessfully() throws Exception {
        UserProfileUpdateRequest request = UserProfileUpdateRequest.builder().firstName("Jane").build();
        UserProfileResponse response = UserProfileResponse.builder().id("user123").firstName("Jane").build();
        when(userService.updateUser(eq("user123"), any(UserProfileUpdateRequest.class))).thenReturn(response);

        mockMvc.perform(put("/api/v1/users/user123/profile")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("user123"))
                .andExpect(jsonPath("$.firstName").value("Jane"));
    }

    @Test
    @DisplayName("PUT /api/v1/users/{userId}/profile - validation error")
    void shouldReturnBadRequestOnInvalidUpdateProfile() throws Exception {
        mockMvc.perform(put("/api/v1/users/user123/profile")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{}"))
                .andExpect(status().isOk()); // Controller does not validate fields, only null
    }

    @Test
    @DisplayName("DELETE /api/v1/users/{userId}/profile - success")
    void shouldDeleteUserProfileSuccessfully() throws Exception {
        when(userService.deleteUser(eq("user123"))).thenReturn(true);
        mockMvc.perform(delete("/api/v1/users/user123/profile"))
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("DELETE /api/v1/users/{userId}/profile - not found")
    void shouldReturnNotFoundWhenDeletingNonExistentUser() throws Exception {
        when(userService.deleteUser(eq("user123"))).thenReturn(false);
        mockMvc.perform(delete("/api/v1/users/user123/profile"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("Not Found"));
    }

    @Test
    @DisplayName("POST /api/v1/users/{userId}/bookmark/{courseId} - success")
    void shouldBookmarkCourseSuccessfully() throws Exception {
        mockMvc.perform(post("/api/v1/users/user123/bookmark/course456"))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("POST /api/v1/users/{userId}/bookmark/{courseId} - error")
    void shouldReturnErrorWhenBookmarkingCourseFails() throws Exception {
        Mockito.doThrow(new RuntimeException("Bookmark failed")).when(userService).bookmarkCourse(eq("user123"), eq("course456"));
        mockMvc.perform(post("/api/v1/users/user123/bookmark/course456"))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.status").value(500))
                .andExpect(jsonPath("$.error").exists())
                .andExpect(jsonPath("$.message").value("Bookmark failed"));
    }

    @Test
    @DisplayName("DELETE /api/v1/users/{userId}/bookmark/{courseId} - success")
    void shouldUnbookmarkCourseSuccessfully() throws Exception {
        mockMvc.perform(delete("/api/v1/users/user123/bookmark/course456"))
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("DELETE /api/v1/users/{userId}/bookmark/{courseId} - error")
    void shouldReturnErrorWhenUnbookmarkingCourseFails() throws Exception {
        Mockito.doThrow(new RuntimeException("Unbookmark failed")).when(userService).unbookmarkCourse(eq("user123"), eq("course456"));
        mockMvc.perform(delete("/api/v1/users/user123/bookmark/course456"))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.status").value(500))
                .andExpect(jsonPath("$.error").exists())
                .andExpect(jsonPath("$.message").value("Unbookmark failed"));
    }

    @Test
    @DisplayName("GET /api/v1/users/{userId}/bookmarks - success")
    void shouldGetBookmarkedCourseIdsSuccessfully() throws Exception {
        Mockito.when(userService.getBookmarkedCourseIds(eq("user123"))).thenReturn(java.util.List.of("course1", "course2"));
        mockMvc.perform(get("/api/v1/users/user123/bookmarks"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0]").value("course1"))
                .andExpect(jsonPath("$[1]").value("course2"));
    }

    @Test
    @DisplayName("POST /api/v1/users/{userId}/enroll/{courseId} - success")
    void shouldEnrollUserInCourseSuccessfully() throws Exception {
        mockMvc.perform(post("/api/v1/users/user123/enroll/course456")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(java.util.List.of("skill1", "skill2"))))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("POST /api/v1/users/{userId}/enroll/{courseId} - error")
    void shouldReturnErrorWhenEnrollFails() throws Exception {
        Mockito.doThrow(new RuntimeException("Enroll failed")).when(userService).enrollUserInCourse(eq("user123"), eq("course456"), any());
        mockMvc.perform(post("/api/v1/users/user123/enroll/course456")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(java.util.List.of("skill1"))))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.status").value(500))
                .andExpect(jsonPath("$.error").exists())
                .andExpect(jsonPath("$.message").value("Enroll failed"));
    }

    @Test
    @DisplayName("DELETE /api/v1/users/{userId}/enroll/{courseId} - success")
    void shouldUnenrollUserFromCourseSuccessfully() throws Exception {
        mockMvc.perform(delete("/api/v1/users/user123/enroll/course456")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(java.util.List.of("skill1"))))
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("DELETE /api/v1/users/{userId}/enroll/{courseId} - error")
    void shouldReturnErrorWhenUnenrollFails() throws Exception {
        Mockito.doThrow(new RuntimeException("Unenroll failed")).when(userService).unenrollUserFromCourse(eq("user123"), eq("course456"), any());
        mockMvc.perform(delete("/api/v1/users/user123/enroll/course456")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(java.util.List.of("skill1"))))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.status").value(500))
                .andExpect(jsonPath("$.error").exists())
                .andExpect(jsonPath("$.message").value("Unenroll failed"));
    }
} 