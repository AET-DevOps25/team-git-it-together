package com.gitittogether.skillForge.server.course;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gitittogether.skillForge.server.course.controller.courses.CourseController;
import com.gitittogether.skillForge.server.course.dto.request.course.CourseRequest;
import com.gitittogether.skillForge.server.course.dto.request.course.LearningPathRequest;
import com.gitittogether.skillForge.server.course.dto.response.course.CourseResponse;
import com.gitittogether.skillForge.server.course.dto.response.course.CourseSummaryResponse;
import com.gitittogether.skillForge.server.course.dto.response.course.EnrolledUserInfoResponse;
import com.gitittogether.skillForge.server.course.dto.response.utils.EmbedResult;
import com.gitittogether.skillForge.server.course.exception.GlobalExceptionHandler;
import com.gitittogether.skillForge.server.course.model.utils.Language;
import com.gitittogether.skillForge.server.course.model.utils.Level;
import com.gitittogether.skillForge.server.course.service.courses.CourseService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("CourseController Web Layer Tests")
class CourseControllerTest {

    @Mock
    private CourseService courseService;

    @InjectMocks
    private CourseController courseController;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(courseController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
        objectMapper = new ObjectMapper();
    }

    @Nested
    @DisplayName("POST /api/v1/courses - Course Creation")
    class CreateCourseTests {

        @Test
        @DisplayName("POST /api/v1/courses - success")
        void shouldCreateCourseSuccessfully() throws Exception {
            // Given
            CourseRequest request = CourseRequest.builder()
                    .title("Java Programming")
                    .description("Learn Java from scratch")
                    .instructor("john.doe")
                    .level(Level.BEGINNER)
                    .language(Language.EN)
                    .build();

            CourseResponse response = CourseResponse.builder()
                    .id("course123")
                    .title("Java Programming")
                    .description("Learn Java from scratch")
                    .instructor("john.doe")
                    .level(Level.BEGINNER)
                    .language(Language.EN)
                    .build();

            when(courseService.createCourse(any(CourseRequest.class))).thenReturn(response);

            // When & Then
            mockMvc.perform(post("/api/v1/courses")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value("course123"))
                    .andExpect(jsonPath("$.title").value("Java Programming"))
                    .andExpect(jsonPath("$.instructor").value("john.doe"));

            verify(courseService).createCourse(any(CourseRequest.class));
        }

        @Test
        @DisplayName("POST /api/v1/courses - validation error")
        void shouldReturnBadRequestForInvalidCourse() throws Exception {
            // Given
            CourseRequest request = CourseRequest.builder()
                    .description("Missing title")
                    .build();

            // When & Then
            mockMvc.perform(post("/api/v1/courses")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isBadRequest());
        }
    }

    @Nested
    @DisplayName("GET /api/v1/courses/{courseId} - Get Course")
    class GetCourseTests {

        @Test
        @DisplayName("GET /api/v1/courses/{courseId} - success")
        void shouldGetCourseSuccessfully() throws Exception {
            // Given
            CourseResponse response = CourseResponse.builder()
                    .id("course123")
                    .title("Java Programming")
                    .description("Learn Java from scratch")
                    .instructor("john.doe")
                    .build();

            when(courseService.getCourse("course123")).thenReturn(response);

            // When & Then
            mockMvc.perform(get("/api/v1/courses/course123"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value("course123"))
                    .andExpect(jsonPath("$.title").value("Java Programming"));

            verify(courseService).getCourse("course123");
        }

        @Test
        @DisplayName("GET /api/v1/courses/{courseId} - not found")
        void shouldReturnNotFoundForNonExistentCourse() throws Exception {
            // Given
            when(courseService.getCourse("nonexistent")).thenThrow(new RuntimeException("Course not found"));

            // When & Then
            mockMvc.perform(get("/api/v1/courses/nonexistent"))
                    .andExpect(status().isInternalServerError());
        }
    }

    @Nested
    @DisplayName("GET /api/v1/courses - Get All Courses")
    class GetAllCoursesTests {

        @Test
        @DisplayName("GET /api/v1/courses - success")
        void shouldGetAllCoursesSuccessfully() throws Exception {
            // Given
            List<CourseSummaryResponse> responses = Arrays.asList(
                    CourseSummaryResponse.builder().id("course1").title("Java Programming").build(),
                    CourseSummaryResponse.builder().id("course2").title("Python Basics").build()
            );

            when(courseService.getAllCourses()).thenReturn(responses);

            // When & Then
            mockMvc.perform(get("/api/v1/courses"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$[0].id").value("course1"))
                    .andExpect(jsonPath("$[0].title").value("Java Programming"))
                    .andExpect(jsonPath("$[1].id").value("course2"))
                    .andExpect(jsonPath("$[1].title").value("Python Basics"));

            verify(courseService).getAllCourses();
        }
    }

    @Nested
    @DisplayName("GET /api/v1/courses/public - Get Public Courses")
    class GetPublicCoursesTests {

        @Test
        @DisplayName("GET /api/v1/courses/public - success")
        void shouldGetPublicCoursesSuccessfully() throws Exception {
            // Given
            List<CourseSummaryResponse> responses = Arrays.asList(
                    CourseSummaryResponse.builder().id("course1").title("Java Programming").isPublic(true).build(),
                    CourseSummaryResponse.builder().id("course2").title("Python Basics").isPublic(true).build()
            );

            when(courseService.getPublicCourses()).thenReturn(responses);

            // When & Then
            mockMvc.perform(get("/api/v1/courses/public"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$[0].isPublic").value(true))
                    .andExpect(jsonPath("$[1].isPublic").value(true));

            verify(courseService).getPublicCourses();
        }
    }

    @Nested
    @DisplayName("PUT /api/v1/courses/{courseId} - Update Course")
    class UpdateCourseTests {

        @Test
        @DisplayName("PUT /api/v1/courses/{courseId} - success")
        void shouldUpdateCourseSuccessfully() throws Exception {
            // Given
            CourseRequest request = CourseRequest.builder()
                    .title("Updated Java Programming")
                    .description("Updated description")
                    .instructor("john.doe")
                    .build();

            CourseResponse response = CourseResponse.builder()
                    .id("course123")
                    .title("Updated Java Programming")
                    .description("Updated description")
                    .instructor("john.doe")
                    .build();

            when(courseService.updateCourse(eq("course123"), any(CourseRequest.class))).thenReturn(response);

            // When & Then
            mockMvc.perform(put("/api/v1/courses/course123")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.title").value("Updated Java Programming"));

            verify(courseService).updateCourse(eq("course123"), any(CourseRequest.class));
        }
    }

    @Nested
    @DisplayName("DELETE /api/v1/courses/{courseId} - Delete Course")
    class DeleteCourseTests {

        @Test
        @DisplayName("DELETE /api/v1/courses/{courseId} - success")
        void shouldDeleteCourseSuccessfully() throws Exception {
            // Given
            doNothing().when(courseService).deleteCourse("course123");

            // When & Then
            mockMvc.perform(delete("/api/v1/courses/course123"))
                    .andExpect(status().isNoContent());

            verify(courseService).deleteCourse("course123");
        }
    }

    @Nested
    @DisplayName("POST /api/v1/courses/{courseId}/enroll/{userId} - Enroll User")
    class EnrollUserTests {

        @Test
        @DisplayName("POST /api/v1/courses/{courseId}/enroll/{userId} - success")
        void shouldEnrollUserSuccessfully() throws Exception {
            // Given
            CourseResponse response = CourseResponse.builder()
                    .id("course123")
                    .title("Java Programming")
                    .numberOfEnrolledUsers(1)
                    .build();

            when(courseService.enrollUserInCourse("course123", "user123")).thenReturn(response);

            // When & Then
            mockMvc.perform(post("/api/v1/courses/course123/enroll/user123"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.numberOfEnrolledUsers").value(1));

            verify(courseService).enrollUserInCourse("course123", "user123");
        }

        @Test
        @DisplayName("POST /api/v1/courses/{courseId}/enroll/{userId} - error")
        void shouldReturnErrorWhenEnrollFails() throws Exception {
            // Given
            when(courseService.enrollUserInCourse("course123", "user123"))
                    .thenThrow(new RuntimeException("Enroll failed"));

            // When & Then
            mockMvc.perform(post("/api/v1/courses/course123/enroll/user123"))
                    .andExpect(status().isInternalServerError());
        }
    }

    @Nested
    @DisplayName("DELETE /api/v1/courses/{courseId}/enroll/{userId} - Unenroll User")
    class UnenrollUserTests {

        @Test
        @DisplayName("DELETE /api/v1/courses/{courseId}/enroll/{userId} - success")
        void shouldUnenrollUserSuccessfully() throws Exception {
            // Given
            doNothing().when(courseService).unenrollUserFromCourse("course123", "user123");

            // When & Then
            mockMvc.perform(delete("/api/v1/courses/course123/enroll/user123"))
                    .andExpect(status().isNoContent());

            verify(courseService).unenrollUserFromCourse("course123", "user123");
        }

        @Test
        @DisplayName("DELETE /api/v1/courses/{courseId}/enroll/{userId} - error")
        void shouldReturnErrorWhenUnenrollFails() throws Exception {
            // Given
            doThrow(new RuntimeException("Unenroll failed"))
                    .when(courseService).unenrollUserFromCourse("course123", "user123");

            // When & Then
            mockMvc.perform(delete("/api/v1/courses/course123/enroll/user123"))
                    .andExpect(status().isInternalServerError());
        }
    }

    @Nested
    @DisplayName("POST /api/v1/courses/{courseId}/complete/{userId} - Complete Course")
    class CompleteCourseTests {

        @Test
        @DisplayName("POST /api/v1/courses/{courseId}/complete/{userId} - success")
        void shouldCompleteCourseSuccessfully() throws Exception {
            // Given
            doNothing().when(courseService).completeCourseForUser("course123", "user123");

            // When & Then
            mockMvc.perform(post("/api/v1/courses/course123/complete/user123"))
                    .andExpect(status().isOk());

            verify(courseService).completeCourseForUser("course123", "user123");
        }
    }

    @Nested
    @DisplayName("GET /api/v1/courses/user/{userId}/enrolled - Get User Enrolled Courses")
    class GetUserEnrolledCoursesTests {

        @Test
        @DisplayName("GET /api/v1/courses/user/{userId}/enrolled - success")
        void shouldGetUserEnrolledCoursesSuccessfully() throws Exception {
            // Given
            List<EnrolledUserInfoResponse> responses = Arrays.asList(
                    EnrolledUserInfoResponse.builder().userId("user1").progress(50.0f).build(),
                    EnrolledUserInfoResponse.builder().userId("user2").progress(75.0f).build()
            );

            when(courseService.getUserEnrolledCourses("user123")).thenReturn(responses);

            // When & Then
            mockMvc.perform(get("/api/v1/courses/user/user123/enrolled"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$[0].userId").value("user1"))
                    .andExpect(jsonPath("$[0].progress").value(50.0))
                    .andExpect(jsonPath("$[1].userId").value("user2"))
                    .andExpect(jsonPath("$[1].progress").value(75.0));

            verify(courseService).getUserEnrolledCourses("user123");
        }
    }

    @Nested
    @DisplayName("POST /api/v1/courses/{courseId}/bookmark/{userId} - Bookmark Course")
    class BookmarkCourseTests {

        @Test
        @DisplayName("POST /api/v1/courses/{courseId}/bookmark/{userId} - success")
        void shouldBookmarkCourseSuccessfully() throws Exception {
            // Given
            doNothing().when(courseService).bookmarkCourse("course123", "user123");

            // When & Then
            mockMvc.perform(post("/api/v1/courses/course123/bookmark/user123"))
                    .andExpect(status().isOk());

            verify(courseService).bookmarkCourse("course123", "user123");
        }

        @Test
        @DisplayName("POST /api/v1/courses/{courseId}/bookmark/{userId} - error")
        void shouldReturnErrorWhenBookmarkingCourseFails() throws Exception {
            // Given
            doThrow(new RuntimeException("Bookmark failed"))
                    .when(courseService).bookmarkCourse("course123", "user123");

            // When & Then
            mockMvc.perform(post("/api/v1/courses/course123/bookmark/user123"))
                    .andExpect(status().isInternalServerError());
        }
    }

    @Nested
    @DisplayName("DELETE /api/v1/courses/{courseId}/bookmark/{userId} - Unbookmark Course")
    class UnbookmarkCourseTests {

        @Test
        @DisplayName("DELETE /api/v1/courses/{courseId}/bookmark/{userId} - success")
        void shouldUnbookmarkCourseSuccessfully() throws Exception {
            // Given
            doNothing().when(courseService).unbookmarkCourse("course123", "user123");

            // When & Then
            mockMvc.perform(delete("/api/v1/courses/course123/bookmark/user123"))
                    .andExpect(status().isNoContent());

            verify(courseService).unbookmarkCourse("course123", "user123");
        }

        @Test
        @DisplayName("DELETE /api/v1/courses/{courseId}/bookmark/{userId} - error")
        void shouldReturnErrorWhenUnbookmarkingCourseFails() throws Exception {
            // Given
            doThrow(new RuntimeException("Unbookmark failed"))
                    .when(courseService).unbookmarkCourse("course123", "user123");

            // When & Then
            mockMvc.perform(delete("/api/v1/courses/course123/bookmark/user123"))
                    .andExpect(status().isInternalServerError());
        }
    }

    @Nested
    @DisplayName("GET /api/v1/courses/search/instructor/{instructor} - Search by Instructor")
    class SearchByInstructorTests {

        @Test
        @DisplayName("GET /api/v1/courses/search/instructor/{instructor} - success")
        void shouldSearchByInstructorSuccessfully() throws Exception {
            // Given
            List<CourseResponse> responses = Arrays.asList(
                    CourseResponse.builder().id("course1").title("Java Programming").instructor("john.doe").build(),
                    CourseResponse.builder().id("course2").title("Advanced Java").instructor("john.doe").build()
            );

            when(courseService.getCoursesByInstructor("john.doe")).thenReturn(responses);

            // When & Then
            mockMvc.perform(get("/api/v1/courses/search/instructor/john.doe"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$[0].instructor").value("john.doe"))
                    .andExpect(jsonPath("$[1].instructor").value("john.doe"));

            verify(courseService).getCoursesByInstructor("john.doe");
        }
    }

    @Nested
    @DisplayName("GET /api/v1/courses/search/level/{level} - Search by Level")
    class SearchByLevelTests {

        @Test
        @DisplayName("GET /api/v1/courses/search/level/{level} - success")
        void shouldSearchByLevelSuccessfully() throws Exception {
            // Given
            List<CourseResponse> responses = Arrays.asList(
                    CourseResponse.builder().id("course1").title("Java Basics").level(Level.BEGINNER).build(),
                    CourseResponse.builder().id("course2").title("Python Basics").level(Level.BEGINNER).build()
            );

            when(courseService.getCoursesByLevel(Level.BEGINNER)).thenReturn(responses);

            // When & Then
            mockMvc.perform(get("/api/v1/courses/search/level/BEGINNER"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$[0].level").value("BEGINNER"))
                    .andExpect(jsonPath("$[1].level").value("BEGINNER"));

            verify(courseService).getCoursesByLevel(Level.BEGINNER);
        }
    }

    @Nested
    @DisplayName("GET /api/v1/courses/search/language/{language} - Search by Language")
    class SearchByLanguageTests {

        @Test
        @DisplayName("GET /api/v1/courses/search/language/{language} - success")
        void shouldSearchByLanguageSuccessfully() throws Exception {
            // Given
            List<CourseResponse> responses = Arrays.asList(
                    CourseResponse.builder().id("course1").title("Java Programming").language(Language.EN).build(),
                    CourseResponse.builder().id("course2").title("Python Basics").language(Language.EN).build()
            );

            when(courseService.getCoursesByLanguage(Language.EN)).thenReturn(responses);

            // When & Then
            mockMvc.perform(get("/api/v1/courses/search/language/EN"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$[0].language").value("EN"))
                    .andExpect(jsonPath("$[1].language").value("EN"));

            verify(courseService).getCoursesByLanguage(Language.EN);
        }
    }

    @Nested
    @DisplayName("GET /api/v1/courses/search/skill/{skillName} - Search by Skill")
    class SearchBySkillTests {

        @Test
        @DisplayName("GET /api/v1/courses/search/skill/{skillName} - success")
        void shouldSearchBySkillSuccessfully() throws Exception {
            // Given
            List<CourseResponse> responses = Arrays.asList(
                    CourseResponse.builder().id("course1").title("Java Programming").skills(Arrays.asList("Java", "OOP")).build(),
                    CourseResponse.builder().id("course2").title("Advanced Java").skills(Arrays.asList("Java", "Spring")).build()
            );

            when(courseService.getCoursesBySkill("Java")).thenReturn(responses);

            // When & Then
            mockMvc.perform(get("/api/v1/courses/search/skill/Java"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$[0].skills[0]").value("Java"))
                    .andExpect(jsonPath("$[1].skills[0]").value("Java"));

            verify(courseService).getCoursesBySkill("Java");
        }
    }

    @Nested
    @DisplayName("GET /api/v1/courses/search/category/{categoryName} - Search by Category")
    class SearchByCategoryTests {

        @Test
        @DisplayName("GET /api/v1/courses/search/category/{categoryName} - success")
        void shouldSearchByCategorySuccessfully() throws Exception {
            // Given
            List<CourseResponse> responses = Arrays.asList(
                    CourseResponse.builder().id("course1").title("Java Programming").categories(Arrays.asList("Programming", "Backend")).build(),
                    CourseResponse.builder().id("course2").title("Python Basics").categories(Arrays.asList("Programming", "Data Science")).build()
            );

            when(courseService.getCoursesByCategory("Programming")).thenReturn(responses);

            // When & Then
            mockMvc.perform(get("/api/v1/courses/search/category/Programming"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$[0].categories[0]").value("Programming"))
                    .andExpect(jsonPath("$[1].categories[0]").value("Programming"));

            verify(courseService).getCoursesByCategory("Programming");
        }
    }

    @Nested
    @DisplayName("GET /api/v1/courses/search/title/{title} - Search by Title")
    class SearchByTitleTests {

        @Test
        @DisplayName("GET /api/v1/courses/search/title/{title} - success")
        void shouldSearchByTitleSuccessfully() throws Exception {
            // Given
            List<CourseResponse> responses = Arrays.asList(
                    CourseResponse.builder().id("course1").title("Java Programming").build(),
                    CourseResponse.builder().id("course2").title("Java Basics").build()
            );

            when(courseService.searchCoursesByTitleFuzzy("Java")).thenReturn(responses);

            // When & Then
            mockMvc.perform(get("/api/v1/courses/search/title/Java"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$[0].title").value("Java Programming"))
                    .andExpect(jsonPath("$[1].title").value("Java Basics"));

            verify(courseService).searchCoursesByTitleFuzzy("Java");
        }
    }

    @Nested
    @DisplayName("POST /api/v1/courses/generate/learning_path/{userId} - Generate Course")
    class GenerateCourseTests {

        @Test
        @DisplayName("POST /api/v1/courses/generate/learning_path/{userId} - success")
        void shouldGenerateCourseSuccessfully() throws Exception {
            // Given
            LearningPathRequest request = new LearningPathRequest(
                    "Learn Java Programming",
                    Arrays.asList("Programming", "OOP")
            );

            CourseRequest response = CourseRequest.builder()
                    .title("Java Programming")
                    .description("Learn Java from scratch")
                    .instructor("ai.instructor")
                    .build();

            when(courseService.generateCourseFromGenAi(any(LearningPathRequest.class), anyString(), anyString()))
                    .thenReturn(response);

            // When & Then
            mockMvc.perform(post("/api/v1/courses/generate/learning_path/user123")
                            .contentType(MediaType.APPLICATION_JSON)
                            .header("Authorization", "Bearer test-token")
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.title").value("Java Programming"));

            verify(courseService).generateCourseFromGenAi(any(LearningPathRequest.class), anyString(), anyString());
        }
    }

    @Nested
    @DisplayName("POST /api/v1/courses/generate/prompt - Generate Response")
    class GenerateResponseTests {

        @Test
        @DisplayName("POST /api/v1/courses/generate/prompt - success")
        void shouldGenerateResponseSuccessfully() throws Exception {
            // Given
            String prompt = "Explain Java programming";
            String response = "Java is a programming language...";

            when(courseService.generateResponseFromGenAi(anyString())).thenReturn(response);

            // When & Then
            mockMvc.perform(post("/api/v1/courses/generate/prompt")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(prompt)))
                    .andExpect(status().isOk())
                    .andExpect(content().string("Java is a programming language..."));

            verify(courseService).generateResponseFromGenAi(anyString());
        }
    }

    @Nested
    @DisplayName("POST /api/v1/courses/crawl/url - Crawl URL")
    class CrawlUrlTests {

        @Test
        @DisplayName("POST /api/v1/courses/crawl/url - success")
        void shouldCrawlUrlSuccessfully() throws Exception {
            // Given
            Map<String, String> urlPayload = Map.of("url", "https://example.com");
            EmbedResult response = EmbedResult.builder()
                    .success(true)
                    .message("Crawling completed successfully")
                    .build();

            when(courseService.crawlWebForCourseContent("https://example.com")).thenReturn(response);

            // When & Then
            mockMvc.perform(post("/api/v1/courses/crawl/url")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(urlPayload)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.success").value(true))
                    .andExpect(jsonPath("$.message").value("Crawling completed successfully"));

            verify(courseService).crawlWebForCourseContent("https://example.com");
        }
    }
}
