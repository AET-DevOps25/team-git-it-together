package com.gitittogether.skillForge.server.course.service.courses;
import com.gitittogether.skillForge.server.course.dto.request.course.LearningPathRequest;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.gitittogether.skillForge.server.course.dto.request.course.CourseRequest;
import com.gitittogether.skillForge.server.course.dto.request.course.EnrolledUserInfoRequest;
import com.gitittogether.skillForge.server.course.dto.request.course.LearningPathRequest;
import com.gitittogether.skillForge.server.course.dto.response.course.CourseResponse;
import com.gitittogether.skillForge.server.course.dto.response.course.CourseSummaryResponse;
import com.gitittogether.skillForge.server.course.dto.response.course.EnrolledUserInfoResponse;
import com.gitittogether.skillForge.server.course.dto.response.course.PromptResponse;
import com.gitittogether.skillForge.server.course.dto.response.utils.EmbedResult;
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
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class CourseServiceImpl implements CourseService {

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
        if (course.getModules() != null) {
            for (int i = 0; i < course.getModules().size(); i++) {
                course.getModules().get(i).setOrder(i);
            }
            // Ensure correct lesson order within each module (starting from 0)
            int startingOrder = 0;
            for (Module module : course.getModules()) {
                module.setLessonOrder(startingOrder);
                startingOrder += module.getNumberOfLessons();
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
            for (int i = 0; i < existingCourse.getModules().size(); i++) {
                Module mod = existingCourse.getModules().get(i);
                mod.setOrder(i);
            }
            // Ensure correct lesson order within each module (starting from 0)
            int startingOrder = 0;
            for (Module module : existingCourse.getModules()) {
                module.setLessonOrder(startingOrder);
                startingOrder += module.getNumberOfLessons();
            }
        }

        // Calculate total number of lessons for the course
        assert existingCourse.getModules() != null;
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
        updateProgressForAllEnrolledUsers(existingCourse, totalLessons);

        if (request.getNumberOfEnrolledUsers() != null)
            existingCourse.setNumberOfEnrolledUsers(request.getNumberOfEnrolledUsers());
        if (request.getLevel() != null) existingCourse.setLevel(request.getLevel());
        if (request.getThumbnailUrl() != null) existingCourse.setThumbnailUrl(request.getThumbnailUrl());
        if (request.getLanguage() != null) existingCourse.setLanguage(request.getLanguage());

        // Handle booleans as objects if you want partial update; here forced to always update
        existingCourse.setPublished(request.isPublished());
        existingCourse.setPublic(request.isPublic());
        // Rating is optional, so only update if provided
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
    public List<CourseResponse> advancedSearch(String instructor, Level level, Language language, String skill, String category, String title, boolean isPublished, boolean isPublic) {
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
        if (isPublished) {
            query.addCriteria(Criteria.where("published").is(true));
        }
        if (isPublic) {
            query.addCriteria(Criteria.where("public").is(true));
        }
        List<Course> courses = mongoTemplate.find(query, Course.class);
        return courses.stream().map(CourseMapper::toCourseResponse).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public CourseResponse generateFromGenAi(LearningPathRequest req) {
        log.info("▶️ Calling GenAI to generate learning-path course (prompt='{}') with existing skills={}", req.prompt(), req.existingSkills());

        try {
            // Build request payload for GenAI service
            Map<String, Object> payload = new HashMap<>();
            payload.put("prompt", req.prompt());
            payload.put("existing_skills", req.existingSkills() == null ? List.of() : req.existingSkills());

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<Map<String, Object>> httpReq = new HttpEntity<>(payload, headers);

            String endpoint = genaiServiceUri + "/api/v1/rag/generate-course";
            ResponseEntity<String> genAiResp = restTemplate.postForEntity(endpoint, httpReq, String.class);

            if (!genAiResp.getStatusCode().is2xxSuccessful() || genAiResp.getBody() == null) {
                log.error("GenAI responded with status={} body={}", genAiResp.getStatusCode(), genAiResp.getBody());
                throw new IllegalStateException("GenAI service failed");
            }

            String rawJson = genAiResp.getBody();
            ObjectMapper mapper = new ObjectMapper();
            mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

            CourseRequest courseReq = mapper.readValue(rawJson, CourseRequest.class);
            // Ensure manual review before publishing
            courseReq.setPublished(false);
            courseReq.setPublic(false);

            // Persist via existing logic
            CourseResponse persisted = this.createCourse(courseReq);
            log.info("✅ Generated and persisted course id={}", persisted.getId());
            return persisted;
        } catch (IllegalArgumentException e) {
            // Specifically handle duplicate course title error
            log.error("❌ generateFromGenAi failed due to duplicate course title: {}", e.getMessage());
            throw e; // Preserve the original exception with its message
        } catch (Exception e) {
            log.error("❌ generateFromGenAi failed: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to generate course via GenAI", e);
        }
    }
}