package com.gitittogether.skillForge.server.course.controller.courses;

import com.gitittogether.skillForge.server.course.dto.request.course.CourseRequest;
import com.gitittogether.skillForge.server.course.dto.request.course.LearningPathRequest;
import com.gitittogether.skillForge.server.course.dto.response.course.CourseResponse;
import com.gitittogether.skillForge.server.course.dto.response.course.CourseSummaryResponse;
import com.gitittogether.skillForge.server.course.dto.response.course.EnrolledUserInfoResponse;
import com.gitittogether.skillForge.server.course.dto.response.utils.EmbedResult;
import com.gitittogether.skillForge.server.course.model.utils.Language;
import com.gitittogether.skillForge.server.course.model.utils.Level;
import com.gitittogether.skillForge.server.course.service.courses.CourseService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/courses")
@RequiredArgsConstructor
@Slf4j
public class CourseController {

    private final CourseService courseService;

    @PostMapping
    public ResponseEntity<CourseResponse> createCourse(@RequestBody CourseRequest request) {
        log.info("Creating new course: {}", request.getTitle());
        CourseResponse response = courseService.createCourse(request);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{courseId}")
    public ResponseEntity<CourseResponse> getCourse(@PathVariable String courseId) {
        log.info("Fetching course: {}", courseId);
        CourseResponse response = courseService.getCourse(courseId);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<List<CourseSummaryResponse>> getAllCourses() {
        log.info("Fetching all courses");
        List<CourseSummaryResponse> responses = courseService.getAllCourses();
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/public")
    public ResponseEntity<List<CourseSummaryResponse>> getPublicCourses() {
        log.info("Fetching public courses for landing page");
        List<CourseSummaryResponse> responses = courseService.getPublicCourses();
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/published")
    public ResponseEntity<List<CourseSummaryResponse>> getPublishedCourses() {
        log.info("Fetching public and published courses for landing page");
        List<CourseSummaryResponse> responses = courseService.getPublishedCourses();
        return ResponseEntity.ok(responses);
    }

    @PutMapping("/{courseId}")
    public ResponseEntity<CourseResponse> updateCourse(@PathVariable String courseId, @RequestBody CourseRequest request) {
        log.info("Updating course: {}", courseId);
        CourseResponse response = courseService.updateCourse(courseId, request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{courseId}")
    public ResponseEntity<Void> deleteCourse(@PathVariable String courseId) {
        log.info("Deleting course: {}", courseId);
        courseService.deleteCourse(courseId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{courseId}/enroll/{userId}")
    public ResponseEntity<CourseResponse> enrollUserInCourse(@PathVariable String courseId, @PathVariable String userId) {
        log.info("Enrolling user {} in course {}", userId, courseId);
        CourseResponse response = courseService.enrollUserInCourse(courseId, userId);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{courseId}/enroll/{userId}")
    public ResponseEntity<Void> unenrollUserFromCourse(@PathVariable String courseId, @PathVariable String userId) {
        log.info("Unenrolling user {} from course {}", userId, courseId);
        courseService.unenrollUserFromCourse(courseId, userId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{courseId}/complete/{userId}")
    public ResponseEntity<Void> completeCourseForUser(@PathVariable String courseId, @PathVariable String userId) {
        log.info("Completing course {} for user {}", courseId, userId);
        courseService.completeCourseForUser(courseId, userId);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/user/{userId}/enrolled")
    public ResponseEntity<List<EnrolledUserInfoResponse>> getUserEnrolledCourses(@PathVariable String userId) {
        log.info("Fetching enrolled courses for user: {}", userId);
        List<EnrolledUserInfoResponse> responses = courseService.getUserEnrolledCourses(userId);
        return ResponseEntity.ok(responses);
    }

    @PostMapping("/{courseId}/bookmark/{userId}")
    public ResponseEntity<Void> bookmarkCourse(@PathVariable String courseId, @PathVariable String userId) {
        log.info("Bookmarking course {} for user {}", courseId, userId);
        courseService.bookmarkCourse(courseId, userId);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{courseId}/bookmark/{userId}")
    public ResponseEntity<Void> unbookmarkCourse(@PathVariable String courseId, @PathVariable String userId) {
        log.info("Unbookmarking course {} for user {}", courseId, userId);
        courseService.unbookmarkCourse(courseId, userId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/search")
    public ResponseEntity<List<CourseResponse>> searchCourses(
            @RequestParam(required = false) String instructor,
            @RequestParam(required = false) Level level,
            @RequestParam(required = false) Language language,
            @RequestParam(required = false) String skill,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String title,
            @RequestParam(required = false) boolean isPublished,
            @RequestParam(required = false) boolean isPublic
    ) {
        log.info("Advanced search: instructor={}, level={}, language={}, skill={}, category={}, title={}, isPublished={}, isPublic={}",
                instructor, level, language, skill, category, title, isPublished, isPublic);
        List<CourseResponse> responses = courseService.advancedSearch(instructor, level, language, skill, category, title, isPublished, isPublic);
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/search/instructor/{instructor}")
    public ResponseEntity<List<CourseResponse>> getCoursesByInstructor(@PathVariable String instructor) {
        log.info("Fetching courses by instructor: {}", instructor);
        List<CourseResponse> responses = courseService.getCoursesByInstructor(instructor);
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/search/level/{level}")
    public ResponseEntity<List<CourseResponse>> getCoursesByLevel(@PathVariable com.gitittogether.skillForge.server.course.model.utils.Level level) {
        log.info("Fetching courses by level: {}", level);
        List<CourseResponse> responses = courseService.getCoursesByLevel(level);
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/search/language/{language}")
    public ResponseEntity<List<CourseResponse>> getCoursesByLanguage(@PathVariable com.gitittogether.skillForge.server.course.model.utils.Language language) {
        log.info("Fetching courses by language: {}", language);
        List<CourseResponse> responses = courseService.getCoursesByLanguage(language);
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/search/skill/{skillName}")
    public ResponseEntity<List<CourseResponse>> getCoursesBySkill(@PathVariable String skillName) {
        log.info("Fetching courses by skill: {}", skillName);
        List<CourseResponse> responses = courseService.getCoursesBySkill(skillName);
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/search/category/{categoryName}")
    public ResponseEntity<List<CourseResponse>> getCoursesByCategory(@PathVariable String categoryName) {
        log.info("Fetching courses by category: {}", categoryName);
        List<CourseResponse> responses = courseService.getCoursesByCategory(categoryName);
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/search/title/{title}")
    public ResponseEntity<List<CourseResponse>> searchCoursesByTitle(@PathVariable String title) {
        log.info("Searching courses by fuzzy title: {}", title);
        List<CourseResponse> responses = courseService.searchCoursesByTitleFuzzy(title);
        return ResponseEntity.ok(responses);
    }

    /**
     * Generates a brand-new course via GenAI + RAG, then persists & returns it.
     * Chosen as POST because we are **creating** a new server-side resource
     */
    @PostMapping("/generate/learning_path/{userId}")
    public ResponseEntity<CourseRequest> generateCourseForUser(@PathVariable String userId, @RequestBody LearningPathRequest req, HttpServletRequest servletRequest) {
        log.info("Generating course for user: {}", userId);
        CourseRequest generated = courseService.generateCourseFromGenAi(req, userId, servletRequest.getHeader("Authorization"));
        return ResponseEntity.ok(generated);
    }

    /**
     * Confirms the generation of a course from a Learning Path request.
     * This method is called after the course has been generated and the user has reviewed it.
     * It retrieves the last generated course details, creates the course in the database, enrolls the user, and returns the course response.
     *
     * @param userId The ID of the user confirming the course generation.
     * @return CourseResponse containing the confirmed course details.
     */
    @PostMapping("/generate/learning_path/{userId}/confirm")
    public ResponseEntity<CourseResponse> confirmGeneratedCourse(@PathVariable String userId) {
        log.info("Confirming course generation for user: {}", userId);
        CourseResponse confirmed = courseService.confirmCourseGeneration(userId);
        return ResponseEntity.ok(confirmed);
    }

    /**
     * Generates a response to a given Prompt.
     * This is delegated to the GenAi Service
     *
     * @param prompt The prompt to generate a response for.
     * @return The generated response as a String.
     */
    @PostMapping("/generate/prompt")
    public ResponseEntity<String> generateResponseFromPrompt(@RequestBody String prompt) {
        log.info("Generating response to prompt: {}", prompt);
        String generatedResponse = courseService.generateResponseFromGenAi(prompt);
        return ResponseEntity.ok(generatedResponse);
    }

    /**
     * Embeds a url into the GenAI service for future retrieval.
     *
     * @param urlPayload The JSON object containing the URL to embed, e.g. {"url": "https://example.com"}
     * @return ResponseEntity with the status of the operation.
     */
    @PostMapping("/crawl/url")
    public ResponseEntity<?> crawlWebUrl(@RequestBody Map<String, String> urlPayload) {
        String url = urlPayload.get("url");
        log.info("Embedding URL into GenAI service: {}", url);

        EmbedResult result = courseService.crawlWebForCourseContent(url);

        if (result.isSuccess()) {
            return ResponseEntity.ok(result);
        } else {
            return ResponseEntity.status(HttpStatus.BAD_GATEWAY).body(result);
        }
    }

} 