package com.gitittogether.skillForge.server.course.controller.courses;

import com.gitittogether.skillForge.server.course.dto.request.course.CourseRequest;
import com.gitittogether.skillForge.server.course.dto.response.course.CourseResponse;
import com.gitittogether.skillForge.server.course.dto.response.course.EnrolledCourseResponse;
import com.gitittogether.skillForge.server.course.dto.response.course.CourseSummaryResponse;
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
    public ResponseEntity<EnrolledCourseResponse> enrollUserInCourse(@PathVariable String courseId, @PathVariable String userId) {
        log.info("Enrolling user {} in course {}", userId, courseId);
        EnrolledCourseResponse response = courseService.enrollUserInCourse(courseId, userId);
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
    public ResponseEntity<List<EnrolledCourseResponse>> getUserEnrolledCourses(@PathVariable String userId) {
        log.info("Fetching enrolled courses for user: {}", userId);
        List<EnrolledCourseResponse> responses = courseService.getUserEnrolledCourses(userId);
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
} 