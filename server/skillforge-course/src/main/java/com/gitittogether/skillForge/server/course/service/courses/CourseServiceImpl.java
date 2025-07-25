package com.gitittogether.skillForge.server.course.service.courses;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.gitittogether.skillForge.server.course.dto.request.course.CourseRequest;
import com.gitittogether.skillForge.server.course.dto.request.course.CourseUpdateRequest;
import com.gitittogether.skillForge.server.course.dto.request.course.EnrolledUserInfoRequest;
import com.gitittogether.skillForge.server.course.dto.request.course.LearningPathRequest;
import com.gitittogether.skillForge.server.course.dto.response.course.CourseResponse;
import com.gitittogether.skillForge.server.course.dto.response.course.CourseSummaryResponse;
import com.gitittogether.skillForge.server.course.dto.response.course.EnrolledUserInfoResponse;
import com.gitittogether.skillForge.server.course.dto.response.utils.EmbedResult;
import com.gitittogether.skillForge.server.course.dto.response.utils.PromptResponse;
import com.gitittogether.skillForge.server.course.exception.ResourceNotFoundException;
import com.gitittogether.skillForge.server.course.mapper.course.CourseMapper;
import com.gitittogether.skillForge.server.course.mapper.course.EnrolledUserInfoMapper;
import com.gitittogether.skillForge.server.course.mapper.course.ModuleMapper;
import com.gitittogether.skillForge.server.course.model.course.Course;
import com.gitittogether.skillForge.server.course.model.course.EnrolledUserInfo;
import com.gitittogether.skillForge.server.course.model.course.Module;
import com.gitittogether.skillForge.server.course.model.utils.Language;
import com.gitittogether.skillForge.server.course.model.utils.Level;
import com.gitittogether.skillForge.server.course.repository.course.CourseRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class CourseServiceImpl implements CourseService {
    // Shared cache value containing (userId, CourseRequest) to have last generated course for a given user
    static final ConcurrentHashMap<String, CourseRequest> LAST_GENERATED_COURSES = new ConcurrentHashMap<>();

    private final CourseRepository courseRepository;
    private final RestTemplate restTemplate = new RestTemplate();
    @Value("${user.service.uri:http://localhost:8082}")
    private String userServiceUri;
    @Value("${genai.service.uri:http://localhost:8888}")
    private String genaiServiceUri;
    @Autowired
    private MongoTemplate mongoTemplate;

    @Override
    @Transactional
    public CourseResponse createCourse(CourseRequest request) {
        log.info("Creating new course: {}", request.getTitle());

        // Check if course with same title already exists
        log.info(courseRepository.findByTitle(request.getTitle()).toString());
        if (!courseRepository.findByTitle(request.getTitle()).isEmpty()) {
            log.warn("Course with title '{}' already exists", request.getTitle());
            throw new IllegalArgumentException("Course with this title already exists");
        }
        Course course = CourseMapper.requestToCourse(request);
        // Ensure correct module order (starting from 0)
        orderModulesAndLessons(course.getModules());
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
    public List<CourseSummaryResponse> getAllCourses() {
        log.info("Fetching all courses");

        List<Course> courses = courseRepository.findAll();
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

        if (request.getTitle() != null && !request.getTitle().isBlank())
            existingCourse.setTitle(request.getTitle());
        if (request.getDescription() != null && !request.getDescription().isBlank())
            existingCourse.setDescription(request.getDescription());
        if (request.getInstructor() != null && !request.getInstructor().isBlank())
            existingCourse.setInstructor(request.getInstructor());

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
        }

        // Ensure correct module and lesson order
        if (existingCourse.getModules() != null) {
            orderModulesAndLessons(existingCourse.getModules());
        }

        // Calculate total number of lessons for the course
        Objects.requireNonNull(existingCourse.getModules(), "Modules in the course must not be null");
        int totalLessons = existingCourse.getModules().stream()
                .mapToInt(Module::getNumberOfLessons)
                .sum();

        // Handle enrolledUsers (add new users and update existing users)
        if (request.getEnrolledUsers() != null && !request.getEnrolledUsers().isEmpty()) {
            List<EnrolledUserInfo> existingUsers = existingCourse.getEnrolledUsers();
            Map<String, EnrolledUserInfo> existingUsersMap = existingUsers.stream()
                    .collect(Collectors.toMap(EnrolledUserInfo::getUserId, u -> u));

            for (EnrolledUserInfoRequest requestUser : request.getEnrolledUsers()) {
                EnrolledUserInfo existingUser = existingUsersMap.get(requestUser.getUserId());

                if (existingUser != null) {
                    // Update existing user's currentLesson
                    existingUser.setCurrentLesson(requestUser.getCurrentLesson());
                    log.info("Updated user {} currentLesson to {}", requestUser.getUserId(), requestUser.getCurrentLesson());
                } else {
                    // Add new user
                    EnrolledUserInfo newUser = EnrolledUserInfoMapper.requestToEnrolledUserInfo(requestUser);
                    newUser.setTotalNumberOfLessons(totalLessons);
                    existingUsers.add(newUser);
                    log.info("Added new user {} to course", requestUser.getUserId());
                }
            }

            existingCourse.setEnrolledUsers(existingUsers);
        }

        // Update totalNumberOfLessons for existing users who might have 0
        existingCourse.getEnrolledUsers().stream()
                .filter(u -> u.getTotalNumberOfLessons() == 0)
                .forEach(u -> u.setTotalNumberOfLessons(totalLessons));

        // Calculate and update progress for all enrolled users based on currentLesson
        updateProgressForAllEnrolledUsers(existingCourse);

        if (request.getNumberOfEnrolledUsers() != null)
            existingCourse.setNumberOfEnrolledUsers(request.getNumberOfEnrolledUsers());
        if (request.getLevel() != null) existingCourse.setLevel(request.getLevel());
        if (request.getThumbnailUrl() != null) existingCourse.setThumbnailUrl(request.getThumbnailUrl());
        if (request.getLanguage() != null) existingCourse.setLanguage(request.getLanguage());

        // Handle booleans conditionally - only update if explicitly provided
        // Note: For boolean fields, we need to check if they were explicitly set in the request
        // Since boolean defaults to false, we need a different approach
        // For now, we'll preserve the existing values unless the request explicitly changes them
        // This prevents accidental changes when updating user progress

        // Rating is optional, so only update if provided
        if (request.getRating() != 0.0) existingCourse.setRating(request.getRating());
        Course savedCourse = courseRepository.save(existingCourse);
        log.info("Updated course with ID: {}", savedCourse.getId());
        return CourseMapper.toCourseResponse(savedCourse);
    }

    @Override
    @Transactional
    public CourseResponse updateCoursePartial(String courseId, CourseUpdateRequest request) {
        log.info("Updating course partially: {}", courseId);

        Course existingCourse = courseRepository.findById(courseId)
                .orElseThrow(() -> new ResourceNotFoundException("Course not found with ID: " + courseId));

        // Only update fields that are provided (not null)
        if (request.getTitle() != null && !request.getTitle().isBlank())
            existingCourse.setTitle(request.getTitle());
        if (request.getDescription() != null && !request.getDescription().isBlank())
            existingCourse.setDescription(request.getDescription());
        if (request.getInstructor() != null && !request.getInstructor().isBlank())
            existingCourse.setInstructor(request.getInstructor());

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
        }

        // Ensure correct module and lesson order
        if (existingCourse.getModules() != null) {
            orderModulesAndLessons(existingCourse.getModules());
        }

        // Calculate total number of lessons for the course
        Objects.requireNonNull(existingCourse.getModules(), "Modules in the course must not be null");
        int totalLessons = existingCourse.getModules().stream()
                .mapToInt(Module::getNumberOfLessons)
                .sum();

        // Handle enrolledUsers (add new users and update existing users)
        if (request.getEnrolledUsers() != null && !request.getEnrolledUsers().isEmpty()) {
            List<EnrolledUserInfo> existingUsers = existingCourse.getEnrolledUsers();
            Map<String, EnrolledUserInfo> existingUsersMap = existingUsers.stream()
                    .collect(Collectors.toMap(EnrolledUserInfo::getUserId, u -> u));

            for (EnrolledUserInfoRequest requestUser : request.getEnrolledUsers()) {
                EnrolledUserInfo existingUser = existingUsersMap.get(requestUser.getUserId());

                if (existingUser != null) {
                    // Update existing user's currentLesson
                    existingUser.setCurrentLesson(requestUser.getCurrentLesson());
                    log.info("Updated user {} currentLesson to {}", requestUser.getUserId(), requestUser.getCurrentLesson());
                } else {
                    // Add new user
                    EnrolledUserInfo newUser = EnrolledUserInfoMapper.requestToEnrolledUserInfo(requestUser);
                    newUser.setTotalNumberOfLessons(totalLessons);
                    existingUsers.add(newUser);
                    log.info("Added new user {} to course", requestUser.getUserId());
                }
            }

            existingCourse.setEnrolledUsers(existingUsers);
        }

        // Update totalNumberOfLessons for existing users who might have 0
        existingCourse.getEnrolledUsers().stream()
                .filter(u -> u.getTotalNumberOfLessons() == 0)
                .forEach(u -> u.setTotalNumberOfLessons(totalLessons));

        // Calculate and update progress for all enrolled users based on currentLesson
        updateProgressForAllEnrolledUsers(existingCourse);

        if (request.getNumberOfEnrolledUsers() != null)
            existingCourse.setNumberOfEnrolledUsers(request.getNumberOfEnrolledUsers());
        if (request.getLevel() != null) existingCourse.setLevel(request.getLevel());
        if (request.getThumbnailUrl() != null) existingCourse.setThumbnailUrl(request.getThumbnailUrl());
        if (request.getLanguage() != null) existingCourse.setLanguage(request.getLanguage());

        // Handle booleans conditionally - only update if explicitly provided
        if (request.getPublished() != null) existingCourse.setPublished(request.getPublished());
        if (request.getIsPublic() != null) existingCourse.setIsPublic(request.getIsPublic());

        // Rating is optional, so only update if provided
        if (request.getRating() != 0.0) existingCourse.setRating(request.getRating());

        Course savedCourse = courseRepository.save(existingCourse);
        log.info("Updated course partially with ID: {}", savedCourse.getId());
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

        // Calculate total number of lessons
        int totalLessons = course.getModules().stream()
                .mapToInt(Module::getNumberOfLessons)
                .sum();

        // Create EnrolledUserInfo
        EnrolledUserInfo enrolledUser = EnrolledUserInfo.builder()
                .userId(userId)
                .progress(0.0f)
                .skills(new ArrayList<>(course.getSkills()))
                .currentLesson(0)
                .totalNumberOfLessons(totalLessons)
                .build();
        course.getEnrolledUsers().add(enrolledUser);
        course.setNumberOfEnrolledUsers(course.getNumberOfEnrolledUsers() + 1);
        Course savedCourse = courseRepository.save(course);

        // Call user service to update enrolledCourseIds
        try {
            String enrollUrl = userServiceUri + "/api/v1/users/" + userId + "/enroll/" + courseId;
            HttpHeaders headers = new HttpHeaders();
            headers.set("X-Service-Key", "course-service-key");
            HttpEntity<List<String>> request = new HttpEntity<>(course.getSkills(), headers);
            restTemplate.postForEntity(enrollUrl, request, Void.class);
            log.info("Called user service to add course {} to user {}'s enrolled courses", courseId, userId);
        } catch (Exception e) {
            log.error("Failed to update user's enrolled courses in user service: {}", e.getMessage(), e);
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
            course.setNumberOfEnrolledUsers(Math.max(0, course.getNumberOfEnrolledUsers() - 1));
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
        return publicCourses.stream()
                .map(CourseMapper::toCourseSummaryResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<CourseSummaryResponse> getPublishedCourses() {
        log.info("Fetching public and published courses for landing page");

        List<Course> publicPublishedCourses = courseRepository.findByPublishedTrue();
        return publicPublishedCourses.stream()
                .map(CourseMapper::toCourseSummaryResponse)
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
    public List<CourseResponse> advancedSearch(String instructor, Level level, Language language, String skill, String category, String title, Boolean isPublished, Boolean isPublic) {
        Query query = new Query();
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
        if (isPublished != null) {
            query.addCriteria(Criteria.where("published").is(isPublished));
        }
        if (isPublic != null) {
            query.addCriteria(Criteria.where("isPublic").is(isPublic));
        }
        List<Course> courses = mongoTemplate.find(query, Course.class);
        return courses.stream().map(CourseMapper::toCourseResponse).collect(Collectors.toList());
    }

    @Override
    public CourseRequest generateCourseFromGenAi(LearningPathRequest req, String userId, String authHeader) {
        // 1. Get skills from user-service, fallback to skills from the request
        List<String> effectiveSkills = req.existingSkills();
        String prompt = req.prompt();

        try {
            String profileUrl = userServiceUri + "/api/v1/users/" + userId + "/profile";
            HttpHeaders headers = new HttpHeaders();
            if (authHeader == null || authHeader.isBlank()) {
                log.warn("No auth header provided, using service key only for user profile request");
                headers.set("X-Service-Key", "course-service-key");
            } else {
                log.info("Using provided auth header for user profile request");
                headers.set("Authorization", authHeader);
                headers.set("X-Service-Key", "course-service-key");
            }
            HttpEntity<Void> entity = new HttpEntity<>(headers);

            ResponseEntity<String> profileResp = restTemplate.exchange(profileUrl, HttpMethod.GET, entity, String.class);

            if (profileResp.getStatusCode().is2xxSuccessful() && profileResp.getBody() != null) {
                String profileJson = profileResp.getBody();
                log.info("User profile fetched via user-service: {}", profileJson);
                ObjectMapper mapper = new ObjectMapper();
                JsonNode root = mapper.readTree(profileJson);
                JsonNode skillsNode = root.get("skills");
                if (skillsNode != null && skillsNode.isArray() && !skillsNode.isEmpty()) {
                    effectiveSkills = mapper.convertValue(skillsNode, new TypeReference<List<String>>() {
                    });
                }
            } else {
                log.warn("User service returned non-OK status: {}", profileResp.getStatusCode());
            }
        } catch (Exception ex) {
            log.warn("Could not fetch or parse skills from user profile for {}: {}", userId, ex.getMessage());
            // fallback: effectiveSkills already set to req.existingSkills()
        }
        log.info("▶️ Calling GenAI to generate learning-path course (prompt='{}') with effective skills={}", prompt, effectiveSkills);

        try {
            // 2. Build request payload for GenAI service
            Map<String, Object> payload = new HashMap<>();
            payload.put("prompt", prompt);
            payload.put("existing_skills", effectiveSkills == null ? List.of() : effectiveSkills);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<Map<String, Object>> httpReq = new HttpEntity<>(payload, headers);

            String endpoint = genaiServiceUri + "/api/v1/rag/generate-course";
            ResponseEntity<String> genAiResp = restTemplate.postForEntity(endpoint, httpReq, String.class);

            if (!genAiResp.getStatusCode().is2xxSuccessful() || genAiResp.getBody() == null) {
                log.error("GenAI responded with status={} body={}", genAiResp.getStatusCode(), genAiResp.getBody());
                throw new IllegalStateException("GenAI service failed with statusCode: " + genAiResp.getStatusCode() + "or returned no courseRequest");
            }

            String rawJson = genAiResp.getBody();
            ObjectMapper mapper = new ObjectMapper();
            mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

            CourseRequest courseReq = mapper.readValue(rawJson, CourseRequest.class);
            // Ensure manual review before publishing
            courseReq.setPublished(false);
            courseReq.setIsPublic(false);

            // Store the preview for user confirmation, not persist to DB
            LAST_GENERATED_COURSES.put(userId, courseReq);

            log.info("✅ Generated course from GenAI: {}", courseReq.getTitle());
            return courseReq;
        } catch (IllegalArgumentException e) {
            log.error("❌ generateFromGenAi failed due to duplicate course title: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("❌ generateFromGenAi failed: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to generate course via GenAI", e);
        }
    }

    @Override
    @Transactional
    public CourseResponse confirmCourseGeneration(String userId) {
        try {
            CourseRequest request = LAST_GENERATED_COURSES.get(userId);
            if (request == null) {
                // No course was generated for this user - we nullify the request
                log.warn("No course request found for user {}", userId);
                return null;
            }
            log.info("Confirming course generation for: {}", request.getTitle());
            // Create and save the course
            // Ensure the request has published set to false and public set to false
            request.setPublished(false);
            request.setIsPublic(false);
            CourseResponse persisted = this.createCourse(request);
            log.info("✅ Generated and persisted course id={}", persisted.getId());
            log.info("Now enrolling user {} in the newly created course {}", userId, persisted.getId());
            this.enrollUserInCourse(persisted.getId(), userId);
            log.info("✅ Enrolled user {} in course {}", userId, persisted.getId());
            // clear the last generated course for this user
            LAST_GENERATED_COURSES.remove(userId);
            return persisted;
        } catch (Exception e) {
            log.error("❌ confirmCourseGeneration failed: {}", e.getMessage(), e);
            // We re-throw the exception to indicate failure
            throw e;
        }
    }

    @Override
    @Transactional
    public String generateResponseFromGenAi(String prompt) {
        try {
            Map<String, Object> payload = new HashMap<>();
            payload.put("prompt", prompt);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            HttpEntity<Map<String, Object>> httpReq = new HttpEntity<>(payload, headers);

            String endpoint = genaiServiceUri + "/api/v1/generate";
            ResponseEntity<PromptResponse> genAiResp = restTemplate.postForEntity(endpoint, httpReq, PromptResponse.class);

            if (!genAiResp.getStatusCode().is2xxSuccessful() || genAiResp.getBody() == null) {
                log.error("GenAI responded with status={} body={}", genAiResp.getStatusCode(), genAiResp.getBody());
                throw new IllegalStateException("GenAI service failed");
            }
            PromptResponse body = genAiResp.getBody();
            return body != null ? body.getGenerated_text() : null;
        } catch (Exception ex) {
            log.error("Failed to generate response from GenAI", ex);
            throw new RuntimeException("Failed to generate response from GenAI", ex);
        }
    }

    @Override
    public EmbedResult crawlWebForCourseContent(String url) {
        log.info("Crawling web for course content at URL: {}", url);
        try {
            String endpoint = genaiServiceUri + "/api/v1/embed";
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            Map<String, String> payload = new HashMap<>();
            payload.put("url", url);

            HttpEntity<Map<String, String>> request = new HttpEntity<>(payload, headers);

            ResponseEntity<String> response = restTemplate.postForEntity(endpoint, request, String.class);

            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                String body = response.getBody();
                ObjectMapper mapper = new ObjectMapper();
                JsonNode json = mapper.readTree(body);
                String message = json.has("message") ? json.get("message").asText() : null;
                Integer chunks = json.has("chunks_embedded") ? json.get("chunks_embedded").asInt() : null;

                if (message != null && message.toLowerCase().contains("success") && chunks != null && chunks > 0) {
                    log.info("Successfully crawled and embedded content for URL: {} ({} chunks)", url, chunks);
                    return EmbedResult.builder()
                            .success(true)
                            .url(url)
                            .chunksEmbedded(chunks)
                            .message(message)
                            .build();
                } else {
                    log.error("Crawling finished but not successful: message={}, chunks_embedded={}", message, chunks);
                    return EmbedResult.builder()
                            .success(false)
                            .url(url)
                            .message(message)
                            .chunksEmbedded(chunks)
                            .error("Embedding did not complete successfully")
                            .build();
                }
            } else {
                String body = response.getBody();
                log.error("Failed to crawl web for course content. Status code: {}, body: {}", response.getStatusCode(), body);
                return EmbedResult.builder()
                        .success(false)
                        .url(url)
                        .error("HTTP status: " + response.getStatusCode() + ", body: " + body)
                        .build();
            }
        } catch (Exception e) {
            log.error("Error while crawling web for course content: {}", e.getMessage(), e);
            return EmbedResult.builder()
                    .success(false)
                    .url(url)
                    .error(e.getMessage())
                    .build();
        }
    }

    /**
     * Utility method to update progress for all enrolled users in a course
     *
     * @param course The course to update progress for
     */
    private void updateProgressForAllEnrolledUsers(Course course) {
        course.getEnrolledUsers().forEach(enrolledUser -> {
            if (enrolledUser.getTotalNumberOfLessons() > 0) {
                float progressPercentage = (float) enrolledUser.getCurrentLesson() / enrolledUser.getTotalNumberOfLessons() * 100.0f;
                enrolledUser.setProgress(Math.min(progressPercentage, 100.0f)); // Cap at 100%
            }
        });
    }

    /**
     * Utility method to order modules and lessons within a course
     *
     * @param modules List of modules to order
     */
    private void orderModulesAndLessons(List<Module> modules) {
        if (modules != null) {
            for (int i = 0; i < modules.size(); i++) {
                modules.get(i).setOrder(i);
            }
            // Ensure correct lesson order within each module (starting from 0)
            int startingOrder = 0;
            for (Module module : modules) {
                module.setLessonOrder(startingOrder);
                startingOrder += module.getNumberOfLessons();
            }
        }
    }

}