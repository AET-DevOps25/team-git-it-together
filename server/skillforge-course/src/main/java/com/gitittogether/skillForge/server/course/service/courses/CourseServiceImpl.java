package com.gitittogether.skillForge.server.course.service.courses;

import com.gitittogether.skillForge.server.course.dto.request.course.CourseRequest;
import com.gitittogether.skillForge.server.course.dto.response.course.CourseResponse;
import com.gitittogether.skillForge.server.course.dto.response.course.EnrolledCourseResponse;
import com.gitittogether.skillForge.server.course.exception.ResourceNotFoundException;
import com.gitittogether.skillForge.server.course.mapper.course.CourseMapper;
import com.gitittogether.skillForge.server.course.mapper.course.EnrolledCourseMapper;
import com.gitittogether.skillForge.server.course.model.course.Course;
import com.gitittogether.skillForge.server.course.model.course.EnrolledCourse;
import com.gitittogether.skillForge.server.course.model.course.UserBookmark;
import com.gitittogether.skillForge.server.course.repository.course.CourseRepository;
import com.gitittogether.skillForge.server.course.repository.course.UserBookmarkRepository;
import com.gitittogether.skillForge.server.course.repository.course.UserCourseRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class CourseServiceImpl implements CourseService {

    private final CourseRepository courseRepository;
    private final UserCourseRepository userCourseRepository;
    private final UserBookmarkRepository userBookmarkRepository;

    @Override
    @Transactional
    public CourseResponse createCourse(CourseRequest request) {
        log.info("Creating new course: {}", request.getTitle());
        
        Course course = CourseMapper.requestToCourse(request);
        Course savedCourse = courseRepository.save(course);
        
        log.info("Created course with ID: {}", savedCourse.getId());
        return CourseMapper.toCourseResponse(savedCourse);
    }

    @Override
    public CourseResponse getCourse(String courseId) {
        log.info("Fetching course: {}", courseId);
        
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new ResourceNotFoundException("Course not found with ID: " + courseId));
        
        return CourseMapper.toCourseResponse(course);
    }

    @Override
    public List<CourseResponse> getAllCourses() {
        log.info("Fetching all courses");
        
        List<Course> courses = courseRepository.findAll();
        return courses.stream()
                .map(CourseMapper::toCourseResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public CourseResponse updateCourse(String courseId, CourseRequest request) {
        log.info("Updating course: {}", courseId);
        
        Course existingCourse = courseRepository.findById(courseId)
                .orElseThrow(() -> new ResourceNotFoundException("Course not found with ID: " + courseId));
        
        Course updatedCourse = CourseMapper.requestToCourse(request);
        updatedCourse.setId(courseId);
        Course savedCourse = courseRepository.save(updatedCourse);
        
        log.info("Updated course with ID: {}", savedCourse.getId());
        return CourseMapper.toCourseResponse(savedCourse);
    }

    @Override
    @Transactional
    public void deleteCourse(String courseId) {
        log.info("Deleting course: {}", courseId);
        
        if (!courseRepository.existsById(courseId)) {
            throw new ResourceNotFoundException("Course not found with ID: " + courseId);
        }
        
        // Delete all user enrollments for this course
        userCourseRepository.deleteByCourseId(courseId);
        
        // Delete all bookmarks for this course
        userBookmarkRepository.deleteByCourseId(courseId);
        
        courseRepository.deleteById(courseId);
        log.info("Deleted course with ID: {}", courseId);
    }

    @Override
    @Transactional
    public EnrolledCourseResponse enrollUserInCourse(String courseId, String userId) {
        log.info("Enrolling user {} in course {}", userId, courseId);
        
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new ResourceNotFoundException("Course not found with ID: " + courseId));
        
        // Check if user is already enrolled
        if (userCourseRepository.existsByCourseIdAndProgressUserId(courseId, userId)) {
            log.warn("User {} is already enrolled in course {}", userId, courseId);
            throw new IllegalArgumentException("User is already enrolled in this course");
        }
        
        EnrolledCourse enrolledCourse = EnrolledCourse.builder()
                .course(course)
                .progress(com.gitittogether.skillForge.server.course.model.course.CourseProgress.builder()
                        .courseId(courseId)
                        .userId(userId)
                        .enrolledAt(LocalDateTime.now())
                        .lastAccessedAt(LocalDateTime.now())
                        .build())
                .build();
        
        EnrolledCourse savedEnrolledCourse = userCourseRepository.save(enrolledCourse);
        
        // Update course enrollment count
        course.setNumberOfEnrolledUsers(course.getNumberOfEnrolledUsers() + 1);
        courseRepository.save(course);
        
        log.info("Enrolled user {} in course {}", userId, courseId);
        return EnrolledCourseMapper.toEnrolledCourseResponse(savedEnrolledCourse);
    }

    @Override
    @Transactional
    public void unenrollUserFromCourse(String courseId, String userId) {
        log.info("Unenrolling user {} from course {}", userId, courseId);
        
        EnrolledCourse enrolledCourse = userCourseRepository.findByCourseIdAndProgressUserId(courseId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("User is not enrolled in course"));
        
        userCourseRepository.delete(enrolledCourse);
        
        // Update course enrollment count
        Course course = courseRepository.findById(courseId).orElse(null);
        if (course != null) {
            course.setNumberOfEnrolledUsers(Math.max(0, course.getNumberOfEnrolledUsers() - 1));
            courseRepository.save(course);
        }
        
        log.info("Unenrolled user {} from course {}", userId, courseId);
    }

    @Override
    @Transactional
    public void bookmarkCourseForUser(String courseId, String userId) {
        log.info("Bookmarking course {} for user {}", courseId, userId);
        
        // Verify course exists
        if (!courseRepository.existsById(courseId)) {
            throw new ResourceNotFoundException("Course not found with ID: " + courseId);
        }
        
        // Check if already bookmarked
        if (userBookmarkRepository.existsByUserIdAndCourseId(userId, courseId)) {
            log.warn("User {} has already bookmarked course {}", userId, courseId);
            throw new IllegalArgumentException("Course is already bookmarked");
        }
        
        UserBookmark bookmark = UserBookmark.builder()
                .userId(userId)
                .courseId(courseId)
                .bookmarkedAt(LocalDateTime.now())
                .build();
        
        userBookmarkRepository.save(bookmark);
        log.info("Bookmarked course {} for user {}", courseId, userId);
    }

    @Override
    @Transactional
    public void unbookmarkCourseForUser(String courseId, String userId) {
        log.info("Unbookmarking course {} for user {}", courseId, userId);
        
        if (!userBookmarkRepository.existsByUserIdAndCourseId(userId, courseId)) {
            log.warn("Course {} is not bookmarked for user {}", courseId, userId);
            throw new IllegalArgumentException("Course is not bookmarked");
        }
        
        userBookmarkRepository.deleteByUserIdAndCourseId(userId, courseId);
        log.info("Unbookmarked course {} for user {}", courseId, userId);
    }

    @Override
    @Transactional
    public void completeCourseForUser(String courseId, String userId) {
        log.info("Completing course {} for user {}", courseId, userId);
        
        EnrolledCourse enrolledCourse = userCourseRepository.findByCourseIdAndProgressUserId(courseId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("User is not enrolled in course"));
        
        enrolledCourse.getProgress().setCompleted(true);
        enrolledCourse.getProgress().setCompletedAt(LocalDateTime.now());
        enrolledCourse.getProgress().setProgress(100.0);
        
        userCourseRepository.save(enrolledCourse);
        log.info("Completed course {} for user {}", courseId, userId);
    }

    @Override
    public List<EnrolledCourseResponse> getUserEnrolledCourses(String userId) {
        log.info("Fetching enrolled courses for user: {}", userId);
        
        List<EnrolledCourse> enrolledCourses = userCourseRepository.findByProgressUserId(userId);
        return enrolledCourses.stream()
                .map(EnrolledCourseMapper::toEnrolledCourseResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<CourseResponse> getUserBookmarkedCourses(String userId) {
        log.info("Fetching bookmarked courses for user: {}", userId);
        
        List<UserBookmark> bookmarks = userBookmarkRepository.findByUserId(userId);
        List<String> courseIds = bookmarks.stream()
                .map(UserBookmark::getCourseId)
                .collect(Collectors.toList());
        
        List<Course> courses = courseRepository.findAllById(courseIds);
        return courses.stream()
                .map(CourseMapper::toCourseResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<EnrolledCourseResponse> getUserCompletedCourses(String userId) {
        log.info("Fetching completed courses for user: {}", userId);
        
        List<EnrolledCourse> completedCourses = userCourseRepository.findByProgressUserIdAndProgressCompletedTrue(userId);
        return completedCourses.stream()
                .map(EnrolledCourseMapper::toEnrolledCourseResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<CourseResponse> getPublicCourses() {
        log.info("Fetching public courses for landing page");
        
        List<Course> publicCourses = courseRepository.findByIsPublicTrue();
        return publicCourses.stream()
                .map(CourseMapper::toCourseResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<CourseResponse> getPublicPublishedCourses() {
        log.info("Fetching public and published courses for landing page");
        
        List<Course> publicPublishedCourses = courseRepository.findByIsPublicTrueAndPublishedTrue();
        return publicPublishedCourses.stream()
                .map(CourseMapper::toCourseResponse)
                .collect(Collectors.toList());
    }
} 