package com.gitittogether.skillForge.server.course;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gitittogether.skillForge.server.course.dto.request.course.CourseRequest;
import com.gitittogether.skillForge.server.course.dto.request.course.LearningPathRequest;
import com.gitittogether.skillForge.server.course.dto.response.course.CourseResponse;
import com.gitittogether.skillForge.server.course.dto.response.course.CourseSummaryResponse;
import com.gitittogether.skillForge.server.course.dto.response.course.EnrolledUserInfoResponse;
import com.gitittogether.skillForge.server.course.dto.response.utils.EmbedResult;
import com.gitittogether.skillForge.server.course.dto.response.utils.PromptResponse;
import com.gitittogether.skillForge.server.course.model.course.Course;
import com.gitittogether.skillForge.server.course.model.course.EnrolledUserInfo;
import com.gitittogether.skillForge.server.course.model.utils.Language;
import com.gitittogether.skillForge.server.course.model.utils.Level;
import com.gitittogether.skillForge.server.course.repository.course.CourseRepository;
import com.gitittogether.skillForge.server.course.service.courses.CourseServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("CourseService Implementation Tests")
class CourseServiceImplTest {

    @Mock
    private CourseRepository courseRepository;

    @Mock
    private MongoTemplate mongoTemplate;

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private CourseServiceImpl courseService;

    private Course sampleCourse;
    private CourseRequest sampleCourseRequest;

    @BeforeEach
    void setUp() {
        sampleCourse = Course.builder()
                .id("course123")
                .title("Java Programming")
                .description("Learn Java from scratch")
                .instructor("john.doe")
                .level(Level.BEGINNER)
                .language(Language.EN)
                .skills(new ArrayList<>(Arrays.asList("Java", "OOP")))
                .categories(new ArrayList<>(Arrays.asList("Programming", "Backend")))
                .enrolledUsers(new ArrayList<>())
                .modules(new ArrayList<>())
                .published(true)
                .isPublic(true)
                .numberOfEnrolledUsers(0)
                .rating(4.5)
                .build();

        sampleCourseRequest = CourseRequest.builder()
                .title("Java Programming")
                .description("Learn Java from scratch")
                .instructor("john.doe")
                .level(Level.BEGINNER)
                .language(Language.EN)
                .skills(Arrays.asList("Java", "OOP"))
                .categories(Arrays.asList("Programming", "Backend"))
                .published(true)
                .isPublic(true)
                .build();

        // Set external service URLs using reflection
        ReflectionTestUtils.setField(courseService, "userServiceUri", "http://localhost:8082");
        ReflectionTestUtils.setField(courseService, "genaiServiceUri", "http://localhost:8888");
        
        // Mock the RestTemplate that's created directly in the service
        ReflectionTestUtils.setField(courseService, "restTemplate", restTemplate);
        ReflectionTestUtils.setField(courseService, "mongoTemplate", mongoTemplate);
    }

    @Nested
    @DisplayName("getCourse")
    class GetCourseTests {

        @Test
        @DisplayName("Should get course successfully")
        void shouldGetCourseSuccessfully() {
            // Given
            when(courseRepository.findById("course123")).thenReturn(Optional.of(sampleCourse));

            // When
            CourseResponse result = courseService.getCourse("course123");

            // Then
            assertThat(result).isNotNull();
            assertThat(result.getId()).isEqualTo("course123");
            assertThat(result.getTitle()).isEqualTo("Java Programming");

            verify(courseRepository).findById("course123");
        }

        @Test
        @DisplayName("Should throw exception when course not found")
        void shouldThrowExceptionWhenCourseNotFound() {
            // Given
            when(courseRepository.findById("nonexistent")).thenReturn(Optional.empty());

            // When & Then
            assertThatThrownBy(() -> courseService.getCourse("nonexistent"))
                    .isInstanceOf(RuntimeException.class)
                    .hasMessageContaining("Course not found");

            verify(courseRepository).findById("nonexistent");
        }
    }

    @Nested
    @DisplayName("getAllCourses")
    class GetAllCoursesTests {

        @Test
        @DisplayName("Should get all courses successfully")
        void shouldGetAllCoursesSuccessfully() {
            // Given
            List<Course> courses = Arrays.asList(sampleCourse);
            when(courseRepository.findAll()).thenReturn(courses);

            // When
            List<CourseSummaryResponse> result = courseService.getAllCourses();

            // Then
            assertThat(result).isNotNull();
            assertThat(result).hasSize(1);
            assertThat(result.get(0).getId()).isEqualTo("course123");

            verify(courseRepository).findAll();
        }

        @Test
        @DisplayName("Should return empty list when no courses exist")
        void shouldReturnEmptyListWhenNoCoursesExist() {
            // Given
            when(courseRepository.findAll()).thenReturn(Arrays.asList());

            // When
            List<CourseSummaryResponse> result = courseService.getAllCourses();

            // Then
            assertThat(result).isNotNull();
            assertThat(result).isEmpty();

            verify(courseRepository).findAll();
        }
    }

    @Nested
    @DisplayName("getPublicCourses")
    class GetPublicCoursesTests {

        @Test
        @DisplayName("Should get public courses successfully")
        void shouldGetPublicCoursesSuccessfully() {
            // Given
            List<Course> publicCourses = Arrays.asList(sampleCourse);
            when(courseRepository.findByIsPublicTrue()).thenReturn(publicCourses);

            // When
            List<CourseSummaryResponse> result = courseService.getPublicCourses();

            // Then
            assertThat(result).isNotNull();
            assertThat(result).hasSize(1);
            assertThat(result.get(0).getIsPublic()).isTrue();

            verify(courseRepository).findByIsPublicTrue();
        }
    }

    @Nested
    @DisplayName("getPublishedCourses")
    class GetPublishedCoursesTests {

        @Test
        @DisplayName("Should get published courses successfully")
        void shouldGetPublishedCoursesSuccessfully() {
            // Given
            List<Course> publishedCourses = Arrays.asList(sampleCourse);
            when(courseRepository.findByPublishedTrue()).thenReturn(publishedCourses);

            // When
            List<CourseSummaryResponse> result = courseService.getPublishedCourses();

            // Then
            assertThat(result).isNotNull();
            assertThat(result).hasSize(1);
            assertThat(result.get(0).getPublished()).isTrue();

            verify(courseRepository).findByPublishedTrue();
        }
    }

    @Nested
    @DisplayName("deleteCourse")
    class DeleteCourseTests {

        @Test
        @DisplayName("Should delete course successfully")
        void shouldDeleteCourseSuccessfully() {
            // Given
            when(courseRepository.existsById("course123")).thenReturn(true);
            doNothing().when(courseRepository).deleteById("course123");

            // When
            courseService.deleteCourse("course123");

            // Then
            verify(courseRepository).existsById("course123");
            verify(courseRepository).deleteById("course123");
        }

        @Test
        @DisplayName("Should throw exception when course not found for deletion")
        void shouldThrowExceptionWhenCourseNotFoundForDeletion() {
            // Given
            when(courseRepository.existsById("nonexistent")).thenReturn(false);

            // When & Then
            assertThatThrownBy(() -> courseService.deleteCourse("nonexistent"))
                    .isInstanceOf(RuntimeException.class)
                    .hasMessageContaining("Course not found");

            verify(courseRepository).existsById("nonexistent");
            verify(courseRepository, never()).deleteById(any());
        }
    }

    @Nested
    @DisplayName("enrollUserInCourse")
    class EnrollUserInCourseTests {

        @Test
        @DisplayName("Should enroll user successfully")
        void shouldEnrollUserSuccessfully() {
            // Given
            when(courseRepository.findById("course123")).thenReturn(Optional.of(sampleCourse));
            when(courseRepository.save(any(Course.class))).thenReturn(sampleCourse);
            when(restTemplate.postForEntity(anyString(), any(HttpEntity.class), eq(Void.class)))
                    .thenReturn(new ResponseEntity<>(HttpStatus.OK));

            // When
            CourseResponse result = courseService.enrollUserInCourse("course123", "user123");

            // Then
            assertThat(result).isNotNull();
            verify(courseRepository).findById("course123");
            verify(courseRepository).save(any(Course.class));
            verify(restTemplate).postForEntity(anyString(), any(HttpEntity.class), eq(Void.class));
        }

        @Test
        @DisplayName("Should throw exception when course not found for enrollment")
        void shouldThrowExceptionWhenCourseNotFoundForEnrollment() {
            // Given
            when(courseRepository.findById("nonexistent")).thenReturn(Optional.empty());

            // When & Then
            assertThatThrownBy(() -> courseService.enrollUserInCourse("nonexistent", "user123"))
                    .isInstanceOf(RuntimeException.class)
                    .hasMessageContaining("Course not found");

            verify(courseRepository).findById("nonexistent");
            verify(courseRepository, never()).save(any());
            verify(restTemplate, never()).postForEntity(anyString(), any(HttpEntity.class), eq(Void.class));
        }

        @Test
        @DisplayName("Should throw exception when user already enrolled")
        void shouldThrowExceptionWhenUserAlreadyEnrolled() {
            // Given
            EnrolledUserInfo existingEnrollment = EnrolledUserInfo.builder()
                    .userId("user123")
                    .progress(50.0f)
                    .build();

            Course courseWithExistingEnrollment = Course.builder()
                    .id("course123")
                    .title("Java Programming")
                    .description("Learn Java from scratch")
                    .instructor("john.doe")
                    .enrolledUsers(new ArrayList<>(Arrays.asList(existingEnrollment)))
                    .modules(new ArrayList<>())
                    .numberOfEnrolledUsers(1)
                    .build();

            when(courseRepository.findById("course123")).thenReturn(Optional.of(courseWithExistingEnrollment));

            // When & Then
            assertThatThrownBy(() -> courseService.enrollUserInCourse("course123", "user123"))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("User is already enrolled");

            verify(courseRepository).findById("course123");
            verify(courseRepository, never()).save(any());
        }
    }

    @Nested
    @DisplayName("unenrollUserFromCourse")
    class UnenrollUserFromCourseTests {

        @Test
        @DisplayName("Should unenroll user successfully")
        void shouldUnenrollUserSuccessfully() {
            // Given
            EnrolledUserInfo enrolledUser = EnrolledUserInfo.builder()
                    .userId("user123")
                    .progress(50.0f)
                    .build();

            Course courseWithEnrollment = Course.builder()
                    .id("course123")
                    .title("Java Programming")
                    .description("Learn Java from scratch")
                    .instructor("john.doe")
                    .enrolledUsers(new ArrayList<>(Arrays.asList(enrolledUser)))
                    .modules(new ArrayList<>())
                    .numberOfEnrolledUsers(1)
                    .build();

            when(courseRepository.findById("course123")).thenReturn(Optional.of(courseWithEnrollment));
            when(courseRepository.save(any(Course.class))).thenReturn(courseWithEnrollment);
            when(restTemplate.exchange(anyString(), eq(HttpMethod.DELETE), any(HttpEntity.class), eq(Void.class)))
                    .thenReturn(new ResponseEntity<>(HttpStatus.OK));

            // When
            courseService.unenrollUserFromCourse("course123", "user123");

            // Then
            verify(courseRepository).findById("course123");
            verify(courseRepository).save(any(Course.class));
            verify(restTemplate).exchange(anyString(), eq(HttpMethod.DELETE), any(HttpEntity.class), eq(Void.class));
        }

        @Test
        @DisplayName("Should throw exception when course not found for unenrollment")
        void shouldThrowExceptionWhenCourseNotFoundForUnenrollment() {
            // Given
            when(courseRepository.findById("nonexistent")).thenReturn(Optional.empty());

            // When & Then
            assertThatThrownBy(() -> courseService.unenrollUserFromCourse("nonexistent", "user123"))
                    .isInstanceOf(RuntimeException.class)
                    .hasMessageContaining("Course not found");

            verify(courseRepository).findById("nonexistent");
            verify(courseRepository, never()).save(any());
        }

        @Test
        @DisplayName("Should throw exception when user not enrolled")
        void shouldThrowExceptionWhenUserNotEnrolled() {
            // Given
            when(courseRepository.findById("course123")).thenReturn(Optional.of(sampleCourse));

            // When & Then
            assertThatThrownBy(() -> courseService.unenrollUserFromCourse("course123", "user123"))
                    .isInstanceOf(RuntimeException.class)
                    .hasMessageContaining("User is not enrolled");

            verify(courseRepository).findById("course123");
            verify(courseRepository, never()).save(any());
        }
    }

    @Nested
    @DisplayName("completeCourseForUser")
    class CompleteCourseForUserTests {

        @Test
        @DisplayName("Should complete course for user successfully")
        void shouldCompleteCourseForUserSuccessfully() {
            // Given
            EnrolledUserInfo enrolledUser = EnrolledUserInfo.builder()
                    .userId("user123")
                    .progress(50.0f)
                    .build();

            Course courseWithEnrollment = Course.builder()
                    .id("course123")
                    .title("Java Programming")
                    .description("Learn Java from scratch")
                    .instructor("john.doe")
                    .enrolledUsers(new ArrayList<>(Arrays.asList(enrolledUser)))
                    .modules(new ArrayList<>())
                    .numberOfEnrolledUsers(1)
                    .build();

            when(courseRepository.findById("course123")).thenReturn(Optional.of(courseWithEnrollment));
            when(courseRepository.save(any(Course.class))).thenReturn(courseWithEnrollment);
            when(restTemplate.postForEntity(anyString(), any(HttpEntity.class), eq(Void.class)))
                    .thenReturn(new ResponseEntity<>(HttpStatus.OK));

            // When
            courseService.completeCourseForUser("course123", "user123");

            // Then
            verify(courseRepository).findById("course123");
            verify(courseRepository).save(any(Course.class));
            verify(restTemplate).postForEntity(anyString(), any(HttpEntity.class), eq(Void.class));
        }

        @Test
        @DisplayName("Should throw exception when course not found for completion")
        void shouldThrowExceptionWhenCourseNotFoundForCompletion() {
            // Given
            when(courseRepository.findById("nonexistent")).thenReturn(Optional.empty());

            // When & Then
            assertThatThrownBy(() -> courseService.completeCourseForUser("nonexistent", "user123"))
                    .isInstanceOf(RuntimeException.class)
                    .hasMessageContaining("Course not found");

            verify(courseRepository).findById("nonexistent");
            verify(courseRepository, never()).save(any());
        }

        @Test
        @DisplayName("Should throw exception when user not enrolled for completion")
        void shouldThrowExceptionWhenUserNotEnrolledForCompletion() {
            // Given
            when(courseRepository.findById("course123")).thenReturn(Optional.of(sampleCourse));

            // When & Then
            assertThatThrownBy(() -> courseService.completeCourseForUser("course123", "user123"))
                    .isInstanceOf(RuntimeException.class)
                    .hasMessageContaining("User is not enrolled");

            verify(courseRepository).findById("course123");
            verify(courseRepository, never()).save(any());
        }
    }

    @Nested
    @DisplayName("bookmarkCourse")
    class BookmarkCourseTests {

        @Test
        @DisplayName("Should bookmark course successfully")
        void shouldBookmarkCourseSuccessfully() {
            // Given
            when(courseRepository.existsById("course123")).thenReturn(true);
            when(restTemplate.postForEntity(anyString(), any(HttpEntity.class), eq(Void.class)))
                    .thenReturn(new ResponseEntity<>(HttpStatus.OK));

            // When
            courseService.bookmarkCourse("course123", "user123");

            // Then
            verify(courseRepository).existsById("course123");
            verify(restTemplate).postForEntity(anyString(), any(HttpEntity.class), eq(Void.class));
        }

        @Test
        @DisplayName("Should throw exception when course not found for bookmarking")
        void shouldThrowExceptionWhenCourseNotFoundForBookmarking() {
            // Given
            when(courseRepository.existsById("nonexistent")).thenReturn(false);

            // When & Then
            assertThatThrownBy(() -> courseService.bookmarkCourse("nonexistent", "user123"))
                    .isInstanceOf(RuntimeException.class)
                    .hasMessageContaining("Course not found");

            verify(courseRepository).existsById("nonexistent");
            verify(restTemplate, never()).postForEntity(anyString(), any(HttpEntity.class), eq(Void.class));
        }
    }

    @Nested
    @DisplayName("unbookmarkCourse")
    class UnbookmarkCourseTests {

        @Test
        @DisplayName("Should unbookmark course successfully")
        void shouldUnbookmarkCourseSuccessfully() {
            // Given
            when(courseRepository.existsById("course123")).thenReturn(true);
            when(restTemplate.exchange(anyString(), eq(HttpMethod.DELETE), any(HttpEntity.class), eq(Void.class)))
                    .thenReturn(new ResponseEntity<>(HttpStatus.OK));

            // When
            courseService.unbookmarkCourse("course123", "user123");

            // Then
            verify(courseRepository).existsById("course123");
            verify(restTemplate).exchange(anyString(), eq(HttpMethod.DELETE), any(HttpEntity.class), eq(Void.class));
        }

        @Test
        @DisplayName("Should throw exception when course not found for unbookmarking")
        void shouldThrowExceptionWhenCourseNotFoundForUnbookmarking() {
            // Given
            when(courseRepository.existsById("nonexistent")).thenReturn(false);

            // When & Then
            assertThatThrownBy(() -> courseService.unbookmarkCourse("nonexistent", "user123"))
                    .isInstanceOf(RuntimeException.class)
                    .hasMessageContaining("Course not found");

            verify(courseRepository).existsById("nonexistent");
            verify(restTemplate, never()).exchange(anyString(), eq(HttpMethod.DELETE), any(HttpEntity.class), eq(Void.class));
        }
    }

    @Nested
    @DisplayName("Search Methods")
    class SearchMethodsTests {

        @Test
        @DisplayName("Should search courses by instructor successfully")
        void shouldSearchCoursesByInstructorSuccessfully() {
            // Given
            List<Course> courses = Arrays.asList(sampleCourse);
            when(courseRepository.findByInstructor("john.doe")).thenReturn(courses);

            // When
            List<CourseResponse> result = courseService.getCoursesByInstructor("john.doe");

            // Then
            assertThat(result).isNotNull();
            assertThat(result).hasSize(1);
            assertThat(result.get(0).getInstructor()).isEqualTo("john.doe");

            verify(courseRepository).findByInstructor("john.doe");
        }

        @Test
        @DisplayName("Should search courses by level successfully")
        void shouldSearchCoursesByLevelSuccessfully() {
            // Given
            List<Course> courses = Arrays.asList(sampleCourse);
            when(courseRepository.findByLevel(Level.BEGINNER)).thenReturn(courses);

            // When
            List<CourseResponse> result = courseService.getCoursesByLevel(Level.BEGINNER);

            // Then
            assertThat(result).isNotNull();
            assertThat(result).hasSize(1);
            assertThat(result.get(0).getLevel()).isEqualTo(Level.BEGINNER);

            verify(courseRepository).findByLevel(Level.BEGINNER);
        }

        @Test
        @DisplayName("Should search courses by language successfully")
        void shouldSearchCoursesByLanguageSuccessfully() {
            // Given
            List<Course> courses = Arrays.asList(sampleCourse);
            when(courseRepository.findByLanguage(Language.EN)).thenReturn(courses);

            // When
            List<CourseResponse> result = courseService.getCoursesByLanguage(Language.EN);

            // Then
            assertThat(result).isNotNull();
            assertThat(result).hasSize(1);
            assertThat(result.get(0).getLanguage()).isEqualTo(Language.EN);

            verify(courseRepository).findByLanguage(Language.EN);
        }

        @Test
        @DisplayName("Should search courses by skill successfully")
        void shouldSearchCoursesBySkillSuccessfully() {
            // Given
            List<Course> courses = Arrays.asList(sampleCourse);
            when(courseRepository.findBySkillsContainingIgnoreCase("Java")).thenReturn(courses);

            // When
            List<CourseResponse> result = courseService.getCoursesBySkill("Java");

            // Then
            assertThat(result).isNotNull();
            assertThat(result).hasSize(1);
            assertThat(result.get(0).getSkills()).contains("Java");

            verify(courseRepository).findBySkillsContainingIgnoreCase("Java");
        }

        @Test
        @DisplayName("Should search courses by category successfully")
        void shouldSearchCoursesByCategorySuccessfully() {
            // Given
            List<Course> courses = Arrays.asList(sampleCourse);
            when(courseRepository.findByCategoriesContainingIgnoreCase("Programming")).thenReturn(courses);

            // When
            List<CourseResponse> result = courseService.getCoursesByCategory("Programming");

            // Then
            assertThat(result).isNotNull();
            assertThat(result).hasSize(1);
            assertThat(result.get(0).getCategories()).contains("Programming");

            verify(courseRepository).findByCategoriesContainingIgnoreCase("Programming");
        }

        @Test
        @DisplayName("Should search courses by title fuzzy successfully")
        void shouldSearchCoursesByTitleFuzzySuccessfully() {
            // Given
            List<Course> courses = Arrays.asList(sampleCourse);
            when(courseRepository.findByTitleContainingIgnoreCase("Java")).thenReturn(courses);

            // When
            List<CourseResponse> result = courseService.searchCoursesByTitleFuzzy("Java");

            // Then
            assertThat(result).isNotNull();
            assertThat(result).hasSize(1);
            assertThat(result.get(0).getTitle()).contains("Java");

            verify(courseRepository).findByTitleContainingIgnoreCase("Java");
        }
    }

    @Nested
    @DisplayName("advancedSearch")
    class AdvancedSearchTests {

        @Test
        @DisplayName("Should perform advanced search successfully")
        void shouldPerformAdvancedSearchSuccessfully() {
            // Given
            List<Course> courses = Arrays.asList(sampleCourse);
            when(mongoTemplate.find(any(Query.class), eq(Course.class))).thenReturn(courses);

            // When
            List<CourseResponse> result = courseService.advancedSearch(
                    "john.doe", Level.BEGINNER, Language.EN, "Java", "Programming", "Java", true, true);

            // Then
            assertThat(result).isNotNull();
            assertThat(result).hasSize(1);

            verify(mongoTemplate).find(any(Query.class), eq(Course.class));
        }

        @Test
        @DisplayName("Should return empty list when no criteria match")
        void shouldReturnEmptyListWhenNoCriteriaMatch() {
            // Given
            when(mongoTemplate.find(any(Query.class), eq(Course.class))).thenReturn(Arrays.asList());

            // When
            List<CourseResponse> result = courseService.advancedSearch(
                    "nonexistent", null, null, null, null, null, null, null);

            // Then
            assertThat(result).isNotNull();
            assertThat(result).isEmpty();

            verify(mongoTemplate).find(any(Query.class), eq(Course.class));
        }
    }

    @Nested
    @DisplayName("GenAI Methods")
    class GenAIMethodsTests {

        @Test
        @DisplayName("Should generate course from GenAI successfully")
        void shouldGenerateCourseFromGenAISuccessfully() throws Exception {
            // Given
            LearningPathRequest request = new LearningPathRequest(
                "Learn Java Programming - A comprehensive Java course",
                Arrays.asList("Programming", "OOP")
            );
            String mockProfileJson = "{\"skills\":[\"Java\",\"OOP\"]}";
            CourseRequest mockCourseRequest = CourseRequest.builder()
                .title("Learn Java Programming")
                .description("A comprehensive Java course")
                .instructor("AI")
                .level(Level.BEGINNER)
                .language(Language.EN)
                .build();
            String mockCourseJson = new ObjectMapper().writeValueAsString(mockCourseRequest);
            when(restTemplate.exchange(anyString(), eq(HttpMethod.GET), any(HttpEntity.class), eq(String.class)))
                .thenReturn(new ResponseEntity<>(mockProfileJson, HttpStatus.OK));
            when(restTemplate.postForEntity(anyString(), any(HttpEntity.class), eq(String.class)))
                .thenReturn(new ResponseEntity<>(mockCourseJson, HttpStatus.OK));
            // When
            CourseRequest result = courseService.generateCourseFromGenAi(request, "user123", "Bearer token");
            // Then
            assertThat(result).isNotNull();
            assertThat(result.getTitle()).isEqualTo("Learn Java Programming");
            assertThat(result.getInstructor()).isEqualTo("AI");
            verify(restTemplate).exchange(anyString(), eq(HttpMethod.GET), any(HttpEntity.class), eq(String.class));
            verify(restTemplate).postForEntity(anyString(), any(HttpEntity.class), eq(String.class));
        }

        @Test
        @DisplayName("Should generate response from GenAI successfully")
        void shouldGenerateResponseFromGenAISuccessfully() {
            // Given
            String prompt = "Explain Java programming";
            PromptResponse mockPromptResponse = PromptResponse.builder().generated_text("Java is a programming language...").build();
            when(restTemplate.postForEntity(anyString(), any(HttpEntity.class), eq(PromptResponse.class)))
                .thenReturn(new ResponseEntity<>(mockPromptResponse, HttpStatus.OK));
            // When
            String result = courseService.generateResponseFromGenAi(prompt);
            // Then
            assertThat(result).isNotNull();
            assertThat(result).isEqualTo("Java is a programming language...");
            verify(restTemplate).postForEntity(anyString(), any(HttpEntity.class), eq(PromptResponse.class));
        }

        @Test
        @DisplayName("Should crawl web for course content successfully")
        void shouldCrawlWebForCourseContentSuccessfully() {
            // Given
            String url = "https://example.com";
            String jsonBody = "{\"message\":\"Crawling completed successfully\",\"chunks_embedded\":2}";
            when(restTemplate.postForEntity(anyString(), any(HttpEntity.class), eq(String.class)))
                .thenReturn(new ResponseEntity<>(jsonBody, HttpStatus.OK));
            // When
            EmbedResult result = courseService.crawlWebForCourseContent(url);
            // Then
            assertThat(result).isNotNull();
            assertThat(result.isSuccess()).isTrue();
            assertThat(result.getMessage()).isEqualTo("Crawling completed successfully");
            verify(restTemplate).postForEntity(anyString(), any(HttpEntity.class), eq(String.class));
        }
    }
}
