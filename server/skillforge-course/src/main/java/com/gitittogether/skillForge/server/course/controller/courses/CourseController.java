package com.gitittogether.skillForge.server.course.controller.courses;

import com.gitittogether.skillForge.server.course.dto.request.course.CourseRequest;
import com.gitittogether.skillForge.server.course.dto.response.course.CourseResponse;
import com.gitittogether.skillForge.server.course.dto.response.course.CourseSummaryResponse;
import com.gitittogether.skillForge.server.course.dto.response.course.EnrolledUserInfoResponse;
import com.gitittogether.skillForge.server.course.model.utils.Language;
import com.gitittogether.skillForge.server.course.model.utils.Level;
import com.gitittogether.skillForge.server.course.service.courses.CourseService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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

    @GetMapping("/public/published")
    public ResponseEntity<List<CourseResponse>> getPublicPublishedCourses() {
        log.info("Fetching public and published courses for landing page");
        List<CourseResponse> responses = courseService.getPublicPublishedCourses();
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
            @RequestParam(required = false) String title
    ) {
        log.info("Advanced search: instructor={}, level={}, language={}, skill={}, category={}, title={}", instructor, level, language, skill, category, title);
        List<CourseResponse> responses = courseService.advancedSearch(instructor, level, language, skill, category, title);
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

    @GetMapping("/generate")
    public ResponseEntity<String> generateCourse(@RequestParam List<String> skills, @RequestParam String userId) {
        // !!! TODO CHANGE RETURN TYPE TO COURSE RESPONSE WHEN IMPLEMENTED !!!
        // TODO: 1. Validation of the request parameters
        // TODO: 2. Mapping of the request to match the request on the Genai side (optional)
        // TODO 3. Call the Genai service to generate the course
        // Course generatedCourse = genAiService.generateCourse(skills, userId); // Check how we call other services in enrollment for example
        // TODO 4. save the course as private and not published (we can directly call the createCourse method)
        // TODO 5. enroll the user in the course (call the enrollUserInCourse method)
        log.info("Generating course with skills: {}", skills);
        return ResponseEntity.ok("Course generation is not yet implemented");
    }
} 