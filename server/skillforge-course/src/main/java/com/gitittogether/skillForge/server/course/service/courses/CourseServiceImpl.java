package com.gitittogether.skillForge.server.course.service.courses;
import com.gitittogether.skillForge.server.course.dto.request.course.LearningPathRequest;

import com.gitittogether.skillForge.server.course.dto.request.course.CourseRequest;
import com.gitittogether.skillForge.server.course.dto.response.course.CourseResponse;
import com.gitittogether.skillForge.server.course.dto.response.course.CourseSummaryResponse;
import com.gitittogether.skillForge.server.course.dto.response.course.EnrolledUserInfoResponse;
import com.gitittogether.skillForge.server.course.exception.ResourceNotFoundException;
import com.gitittogether.skillForge.server.course.mapper.course.CourseMapper;
import com.gitittogether.skillForge.server.course.mapper.course.ModuleMapper;
import com.gitittogether.skillForge.server.course.mapper.course.EnrolledUserInfoMapper;
import com.gitittogether.skillForge.server.course.model.course.Course;
import com.gitittogether.skillForge.server.course.model.course.EnrolledUserInfo;
import com.gitittogether.skillForge.server.course.model.course.Module;
import com.gitittogether.skillForge.server.course.repository.course.CourseRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.DeserializationFeature;
import java.util.Map;
import java.util.HashMap;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class CourseServiceImpl implements CourseService {

    private final CourseRepository courseRepository;
    private final RestTemplate restTemplate = new RestTemplate();
    @Value("${user.service.uri:http://localhost:8082}")
    private String userServiceUri;
    @Autowired
    private MongoTemplate mongoTemplate;

    @Override
    @Transactional
    public CourseResponse createCourse(CourseRequest request) {
        // Check if course with same title already exists
        log.info(courseRepository.findByTitle(request.getTitle()).toString());
        if (!courseRepository.findByTitle(request.getTitle()).isEmpty()) {
            log.warn("Course with title '{}' already exists", request.getTitle());
            throw new IllegalArgumentException("Course with this title already exists");
        }
        Course course = CourseMapper.requestToCourse(request);
        // Iterate through modules and set the lesson orders starting from 0 (Module 2 should continue counting from Module 1)
        if (course.getModules() != null && !course.getModules().isEmpty()) {
            int lessonOrder = 0;
            for (Module module : course.getModules()) {
                module.setLessonOrder(lessonOrder);
                lessonOrder += module.getLessons().size(); // Increment by the number of lessons in this module
            }
        }
        Course savedCourse = courseRepository.save(course);

        log.info("Created course with ID: {}", savedCourse.getId());
        return CourseMapper.toCourseResponse(savedCourse);
    }

    @Override
    public CourseResponse getCourse(String courseId) {
        log.info("Fetching course: {}", courseId);
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new ResourceNotFoundException("Course not found with ID: " + courseId));
        // This ensures that the enrollment count is consistent
        updateEnrollmentCount(course);
        return CourseMapper.toCourseResponse(course);
    }

    @Override
    public List<CourseSummaryResponse> getAllCourses() {
        log.info("Fetching all courses");
        List<Course> courses = courseRepository.findAll();

        // Fix enrollment counts for all courses
        courses.forEach(this::updateEnrollmentCount);

        return courses.stream()
                .map(CourseMapper::toCourseSummaryResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public CourseResponse updateCourse(String courseId, CourseRequest request) {
        log.info("Updating course: {}", courseId);

        Course existingCourse = courseRepository.findById(courseId)
                .orElseThrow(() -> new ResourceNotFoundException("Course not found with ID: " + courseId));

        if (request.getTitle() != null) existingCourse.setTitle(request.getTitle());
        if (request.getDescription() != null) existingCourse.setDescription(request.getDescription());
        if (request.getInstructor() != null) existingCourse.setInstructor(request.getInstructor());

        // Merge skills
        if (request.getSkills() != null && !request.getSkills().isEmpty()) {
            Set<String> mergedSkills = new HashSet<>(existingCourse.getSkills());
            mergedSkills.addAll(request.getSkills());
            existingCourse.setSkills(new ArrayList<>(mergedSkills));
        }

        // Merge categories
        if (request.getCategories() != null && !request.getCategories().isEmpty()) {
            Set<String> mergedCategories = new HashSet<>(existingCourse.getCategories());
            mergedCategories.addAll(request.getCategories());
            existingCourse.setCategories(new ArrayList<>(mergedCategories));
        }

        // Merge modules by title (add new modules only)
        if (request.getModules() != null && !request.getModules().isEmpty()) {
            List<Module> existingModules = existingCourse.getModules();
            Set<String> existingTitles = existingModules.stream().map(Module::getTitle).collect(Collectors.toSet());
            List<Module> newModules = request.getModules().stream()
                    .map(ModuleMapper::requestToModule)
                    .filter(m -> !existingTitles.contains(m.getTitle()))
                    .toList();
            existingModules.addAll(newModules);
            existingCourse.setModules(existingModules);
            // Update enrolled users' total number of lessons - this is the case, new lessons were added
            for (EnrolledUserInfo user : existingCourse.getEnrolledUsers()) {
                int totalLessons = existingModules.stream()
                        .mapToInt(Module::getNumberOfLessons)
                        .sum();
                user.setTotalNumberOfLessons(totalLessons);
            }
        }

        // Merge enrolledUsers by userId and add new users if not already enrolled
        if (request.getEnrolledUsers() != null && !request.getEnrolledUsers().isEmpty()) {
            List<EnrolledUserInfo> existingUsers = existingCourse.getEnrolledUsers();
            Map<String, EnrolledUserInfo> userMap = existingUsers.stream()
                    .collect(Collectors.toMap(EnrolledUserInfo::getUserId, u -> u));

            for (EnrolledUserInfo reqUser : request.getEnrolledUsers().stream()
                    .map(EnrolledUserInfoMapper::requestToEnrolledUserInfo)
                    .toList()) {
                if (userMap.containsKey(reqUser.getUserId())) {
                    // Update progress and skills for existing user
                    EnrolledUserInfo existing = userMap.get(reqUser.getUserId());
                    if (reqUser.getProgress() > 0 && reqUser.getProgress() <= 100)
                        existing.setProgress(reqUser.getProgress());
                    if (reqUser.getSkills() != null && !reqUser.getSkills().isEmpty())
                        existing.setSkills(reqUser.getSkills());
                    // Update current lesson if provided and if it is different from existing by 1
                    if (reqUser.getCurrentLesson() >= 0 && reqUser.getCurrentLesson() == existing.getCurrentLesson() + 1) {
                        existing.setCurrentLesson(reqUser.getCurrentLesson());
                        // Update progress accordingly
                        float progress = Math.round(((float) reqUser.getCurrentLesson() / existing.getTotalNumberOfLessons() * 100) * 100.0f) / 100.0f;
                        existing.setProgress(progress);
                    }
                } else {
                    existingUsers.add(reqUser);
                }
            }
            existingCourse.setEnrolledUsers(existingUsers);
        }

        if (request.getNumberOfEnrolledUsers() != null)
            existingCourse.setNumberOfEnrolledUsers(request.getNumberOfEnrolledUsers());
        if (request.getLevel() != null) existingCourse.setLevel(request.getLevel());
        if (request.getThumbnailUrl() != null) existingCourse.setThumbnailUrl(request.getThumbnailUrl());
        if (request.getLanguage() != null) existingCourse.setLanguage(request.getLanguage());
        existingCourse.setPublished(request.isPublished());
        existingCourse.setPublic(request.isPublic());
        if (request.getRating() != 0.0) existingCourse.setRating(request.getRating());

        Course savedCourse = courseRepository.save(existingCourse);
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
        courseRepository.deleteById(courseId);
        log.info("Deleted course with ID: {}", courseId);
    }

    @Override
    @Transactional
    public CourseResponse enrollUserInCourse(String courseId, String userId) {
        log.info("Enrolling user {} in course {}", userId, courseId);

        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new ResourceNotFoundException("Course not found with ID: " + courseId));

        // Check if user is already enrolled
        boolean alreadyEnrolled = course.getEnrolledUsers().stream().anyMatch(u -> u.getUserId().equals(userId));
        if (alreadyEnrolled) {
            log.warn("User {} is already enrolled in course {}", userId, courseId);
            throw new IllegalArgumentException("User is already enrolled in this course");
        }

        // Create EnrolledUserInfo
        EnrolledUserInfo enrolledUser = EnrolledUserInfo.builder()
                .userId(userId)
                .progress(0.0f)
                .skills(new ArrayList<>(course.getSkills()))
                .currentLesson(0)
                .totalNumberOfLessons(course.getModules().stream()
                        .mapToInt(Module::getNumberOfLessons)
                        .sum())
                .build();
        course.getEnrolledUsers().add(enrolledUser);

        // Calculate enrollment count from actual enrolled users list
        course.setNumberOfEnrolledUsers(course.getEnrolledUsers().size());

        Course savedCourse = courseRepository.save(course);

        // Call user service to update enrolledCourseIds
        try {
            String enrollUrl = userServiceUri + "/api/v1/users/" + userId + "/enroll/" + courseId;
            HttpHeaders headers = new HttpHeaders();
            headers.set("X-Service-Key", "course-service-key"); // this is our API key for user service - will be changed later
            HttpEntity<List<String>> request = new HttpEntity<>(course.getSkills(), headers);
            restTemplate.postForEntity(enrollUrl, request, Void.class);
            log.info("Called user service to add course {} to user {}'s enrolled courses", courseId, userId);
        } catch (Exception e) {
            log.error("Failed to update user's enrolled courses in user service: {}", e.getMessage(), e);
            // Rollback the enrollment if user service call fails
            course.getEnrolledUsers().removeIf(u -> u.getUserId().equals(userId));
            course.setNumberOfEnrolledUsers(course.getEnrolledUsers().size());
            courseRepository.save(course);
            throw new RuntimeException("Failed to enroll user: " + e.getMessage());
        }
        log.info("Enrolled user {} in course {}", userId, courseId);
        // Build response
        return CourseMapper.toCourseResponse(savedCourse);
    }

    @Override
    @Transactional
    public void unenrollUserFromCourse(String courseId, String userId) {
        log.info("Unenrolling user {} from course {}", userId, courseId);
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new ResourceNotFoundException("Course not found with ID: " + courseId));
        boolean removed = course.getEnrolledUsers().removeIf(u -> u.getUserId().equals(userId));
        if (removed) {
            // Calculate enrollment count from actual enrolled users list
            course.setNumberOfEnrolledUsers(course.getEnrolledUsers().size());
            courseRepository.save(course);
            // Call user service to update enrolledCourseIds
            try {
                String unenrollUrl = userServiceUri + "/api/v1/users/" + userId + "/enroll/" + courseId;
                HttpHeaders headers = new HttpHeaders();
                headers.set("X-Service-Key", "course-service-key");
                HttpEntity<List<String>> request = new HttpEntity<>(course.getSkills(), headers);
                restTemplate.exchange(unenrollUrl, HttpMethod.DELETE, request, Void.class);
                log.info("Called user service to remove course {} from user {}'s enrolled courses", courseId, userId);
            } catch (Exception e) {
                log.error("Failed to update user's enrolled courses in user service: {}", e.getMessage(), e);
                // We roll back the unenrollment if user service call fails - which is not ideal, but we need to ensure consistency
                course.getEnrolledUsers().add(EnrolledUserInfo.builder()
                        .userId(userId)
                        .progress(0.0f) // Reset progress to 0
                        .skills(new ArrayList<>(course.getSkills()))
                        .currentLesson(0)
                        .totalNumberOfLessons(course.getModules().stream()
                                .mapToInt(Module::getNumberOfLessons)
                                .sum())
                        .build());
            }
            log.info("Unenrolled user {} from course {}", userId, courseId);
        } else {
            throw new ResourceNotFoundException("User is not enrolled in course");
        }
    }

    @Override
    @Transactional
    public void completeCourseForUser(String courseId, String userId) {
        log.info("Completing course {} for user {}", courseId, userId);
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new ResourceNotFoundException("Course not found with ID: " + courseId));
        course.getEnrolledUsers().stream()
                .filter(u -> u.getUserId().equals(userId))
                .findFirst()
                .ifPresentOrElse(
                        u -> u.setProgress(100.0f),
                        () -> {
                            throw new ResourceNotFoundException("User is not enrolled in course");
                        }
                );
        // Call user service to update completedCourseIds
        try {
            String completeUrl = userServiceUri + "/api/v1/users/" + userId + "/complete/" + courseId;
            HttpHeaders headers = new HttpHeaders();
            headers.set("X-Service-Key", "course-service-key");
            HttpEntity<List<String>> request = new HttpEntity<>(course.getSkills(), headers);
            restTemplate.postForEntity(completeUrl, request, Void.class);
            log.info("Called user service to mark course {} as completed for user {}", courseId, userId);
        } catch (Exception e) {
            log.error("Failed to update user's completed courses in user service: {}", e.getMessage(), e);
        }

        log.info("Completed course {} for user {}", courseId, userId);
        courseRepository.save(course);
    }

    // Better use the route in the user service to get enrolled courses easier
    @Override
    public List<EnrolledUserInfoResponse> getUserEnrolledCourses(String userId) {
        log.info("Fetching enrolled courses for user: {}", userId);
        List<Course> allCourses = courseRepository.findAll();
        List<EnrolledUserInfoResponse> responses = new ArrayList<>();
        for (Course course : allCourses) {
            course.getEnrolledUsers().stream()
                    .filter(u -> u.getUserId().equals(userId))
                    .findFirst()
                    .ifPresent(enrolledUser -> responses.add(EnrolledUserInfoResponse.builder()
                            .userId(enrolledUser.getUserId())
                            .progress(enrolledUser.getProgress())
                            .skills(enrolledUser.getSkills())
                            .build()));
        }
        return responses;
    }

    @Override
    public List<CourseSummaryResponse> getPublicCourses() {
        log.info("Fetching public courses for landing page");

        List<Course> publicCourses = courseRepository.findByIsPublicTrue();

        // Fix enrollment counts for public courses
        publicCourses.forEach(this::updateEnrollmentCount);

        return publicCourses.stream()
                .map(CourseMapper::toCourseSummaryResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<CourseResponse> getPublicPublishedCourses() {
        log.info("Fetching public and published courses for landing page");

        List<Course> publicPublishedCourses = courseRepository.findByIsPublicTrueAndPublishedTrue();

        // Fix enrollment counts for public published courses
        publicPublishedCourses.forEach(this::updateEnrollmentCount);

        return publicPublishedCourses.stream()
                .map(CourseMapper::toCourseResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<CourseResponse> getPrivateCourses() {
        log.info("Fetching private courses for authenticated users");
        List<Course> privateCourses = courseRepository.findByIsPublicFalseAndPublishedFalse();
        // Fix enrollment counts for private courses
        privateCourses.forEach(this::updateEnrollmentCount);

        return privateCourses.stream()
                .map(CourseMapper::toCourseResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void bookmarkCourse(String courseId, String userId) {
        log.info("Bookmarking course {} for user {}", courseId, userId);

        // Verify course exists
        if (!courseRepository.existsById(courseId)) {
            throw new ResourceNotFoundException("Course not found with ID: " + courseId);
        }

        // Call user service to update bookmarkedCourseIds
        try {
            String bookmarkUrl = userServiceUri + "/api/v1/users/" + userId + "/bookmark/" + courseId;
            HttpHeaders headers = new HttpHeaders();
            headers.set("X-Service-Key", "course-service-key");
            HttpEntity<Void> request = new HttpEntity<>(headers);
            restTemplate.postForEntity(bookmarkUrl, request, Void.class);
            log.info("Called user service to bookmark course {} for user {}", courseId, userId);
        } catch (Exception e) {
            log.error("Failed to update user's bookmarked courses in user service: {}", e.getMessage(), e);
        }

        log.info("Bookmarked course {} for user {}", courseId, userId);
    }

    @Override
    @Transactional
    public void unbookmarkCourse(String courseId, String userId) {
        log.info("Unbookmarking course {} for user {}", courseId, userId);

        // Verify course exists
        if (!courseRepository.existsById(courseId)) {
            throw new ResourceNotFoundException("Course not found with ID: " + courseId);
        }

        // Call user service to update bookmarkedCourseIds
        try {
            String unbookmarkUrl = userServiceUri + "/api/v1/users/" + userId + "/bookmark/" + courseId;
            HttpHeaders headers = new HttpHeaders();
            headers.set("X-Service-Key", "course-service-key");
            HttpEntity<Void> request = new HttpEntity<>(headers);
            restTemplate.exchange(unbookmarkUrl, HttpMethod.DELETE, request, Void.class);
            log.info("Called user service to unbookmark course {} for user {}", courseId, userId);
        } catch (Exception e) {
            log.error("Failed to update user's bookmarked courses in user service: {}", e.getMessage(), e);
        }

        log.info("Unbookmarked course {} for user {}", courseId, userId);
    }

    @Override
    public List<CourseResponse> getCoursesByInstructor(String instructor) {
        return courseRepository.findByInstructor(instructor)
                .stream()
                .map(CourseMapper::toCourseResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<CourseResponse> getCoursesByLevel(com.gitittogether.skillForge.server.course.model.utils.Level level) {
        return courseRepository.findByLevel(level)
                .stream()
                .map(CourseMapper::toCourseResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<CourseResponse> getCoursesByLanguage(com.gitittogether.skillForge.server.course.model.utils.Language language) {
        return courseRepository.findByLanguage(language)
                .stream()
                .map(CourseMapper::toCourseResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<CourseResponse> getCoursesBySkill(String skillName) {
        return courseRepository.findBySkillsContainingIgnoreCase(skillName)
                .stream()
                .map(CourseMapper::toCourseResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<CourseResponse> getCoursesByCategory(String categoryName) {
        return courseRepository.findByCategoriesContainingIgnoreCase(categoryName)
                .stream()
                .map(CourseMapper::toCourseResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<CourseResponse> searchCoursesByTitleFuzzy(String title) {
        return courseRepository.findByTitleContainingIgnoreCase(title)
                .stream()
                .map(CourseMapper::toCourseResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<CourseResponse> searchCoursesByTitle(String title) {
        return courseRepository.findByTitle(title)
                .stream()
                .map(CourseMapper::toCourseResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<CourseResponse> advancedSearch(String instructor, Level level, Language language, String skill, String category, String title, boolean isAuthenticated) {
        Query query = new Query();
        if (!isAuthenticated) {
            query.addCriteria(Criteria.where("isPublic").is(true));
        }
        if (instructor != null && !instructor.isBlank()) {
            query.addCriteria(Criteria.where("instructor").is(instructor));
        }
        if (level != null) {
            query.addCriteria(Criteria.where("level").is(level));
        }
        if (language != null) {
            query.addCriteria(Criteria.where("language").is(language));
        }
        if (skill != null && !skill.isBlank()) {
            query.addCriteria(Criteria.where("skills").regex(skill, "i"));
        }
        if (category != null && !category.isBlank()) {
            query.addCriteria(Criteria.where("categories").regex(category, "i"));
        }
        if (title != null && !title.isBlank()) {
            query.addCriteria(Criteria.where("title").regex(title, "i"));
        }
        List<Course> courses = mongoTemplate.find(query, Course.class);
        return courses.stream().map(CourseMapper::toCourseResponse).collect(Collectors.toList());
    }

    /**
     * Fixes enrollment count inconsistencies by calculating it from the actual enrolled users list
     */
    private void updateEnrollmentCount(Course course) {
        int actualEnrolledCount = course.getEnrolledUsers().size();
        if (course.getNumberOfEnrolledUsers() != actualEnrolledCount) {
            course.setNumberOfEnrolledUsers(actualEnrolledCount);
            courseRepository.save(course);
        }
    }
} 