# SkillForge Backend Technical Documentation

## Table of Contents
1. [Architecture Overview](#architecture-overview)
2. [User Login Flow](#user-login-flow)
3. [User Browses Courses](#user-browses-courses)
4. [User Enrolls in Course](#user-enrolls-in-course)
5. [User Attends Course & Progress Tracking](#user-attends-course--progress-tracking)
6. [User Unenrolls from Course](#user-unenrolls-from-course)
7. [User Bookmarks Course](#user-bookmarks-course)
8. [Anonymous User Browses Public Courses](#anonymous-user-browses-public-courses)
9. [Security & Error Handling](#security--error-handling)
10. [Best Practices](#best-practices)

## Architecture Overview

### Microservices Architecture
```
┌─────────────────┐    ┌─────────────────┐    ┌─────────────────┐
│   API Gateway   │    │  User Service   │    │ Course Service  │
│                 │    │                 │    │                 │
│ • Authentication│    │ • User Auth     │    │ • Course CRUD   │
│ • Routing       │    │ • User Profile  │    │ • Enrollment    │
│ • Rate Limiting │    │ • JWT Issuance  │    │ • Progress      │
│ • CORS          │    │ • User Mgmt     │    │ • Bookmarks     │
└─────────────────┘    └─────────────────┘    └─────────────────┘
         │                       │                       │
         └───────────────────────┼───────────────────────┘
                                 │
                    ┌─────────────────┐
                    │   MongoDB       │
                    │                 │
                    │ • Users         │
                    │ • Courses       │
                    │ • Enrollments   │
                    │ • Progress      │
                    │ • Bookmarks     │
                    └─────────────────┘
```

### Service Responsibilities

| Service | Primary Responsibilities | Database Collections |
|---------|------------------------|---------------------|
| **API Gateway** | Authentication, Routing, Rate Limiting, CORS | None |
| **User Service** | User Authentication, Profile Management, JWT | users |
| **Course Service** | Course Management, Enrollment, Progress, Bookmarks | courses, enrollments, progress, bookmarks |

---

## User Login Flow

### Sequence Diagram
```
Client                    Gateway                User Service           MongoDB
  │                         │                        │                     │
  │ POST /api/auth/login    │                        │                     │
  │ {username, password}    │                        │                     │
  │─────────────────────────>│                        │                     │
  │                         │                        │                     │
  │                         │ POST /api/v1/auth/login│                     │
  │                         │ {username, password}   │                     │
  │                         │────────────────────────>│                     │
  │                         │                        │                     │
  │                         │                        │ Find User by Username│
  │                         │                        │─────────────────────>│
  │                         │                        │                     │
  │                         │                        │<────────────────────│
  │                         │                        │                     │
  │                         │                        │ Verify Password     │
  │                         │                        │ Generate JWT        │
  │                         │                        │                     │
  │                         │<────────────────────────│                     │
  │                         │                        │                     │
  │<────────────────────────│                        │                     │
  │                         │                        │                     │
```

### Detailed Flow

#### 1. Client Request
```http
POST /api/auth/login
Content-Type: application/json

{
  "username": "john.doe",
  "password": "securePassword123"
}
```

#### 2. API Gateway Processing
- **Route**: `/api/auth/login` → User Service `/api/v1/auth/login`
- **Authentication**: None required (login endpoint)
- **Rate Limiting**: Applied (5 requests per minute)
- **CORS**: Handled

#### 3. User Service Processing
```java
@PostMapping("/api/v1/auth/login")
public ResponseEntity<UserLoginResponse> authenticateUser(@RequestBody UserLoginRequest request) {
    // 1. Validate request
    // 2. Find user by username
    User user = userRepository.findByUsername(request.getUsername())
        .orElseThrow(() -> new ResourceNotFoundException("User not found"));
    
    // 3. Verify password
    if (!passwordEncoder.matches(request.getPassword(), user.getPasswordHash())) {
        throw new WrongPasswordException("Invalid password");
    }
    
    // 4. Generate JWT token
    String token = jwtUtils.generateToken(user.getUsername());
    
    // 5. Return response
    return ResponseEntity.ok(UserMapper.toUserLoginResponse(user, token));
}
```

#### 4. JWT Token Structure
```json
{
  "header": {
    "alg": "HS256",
    "typ": "JWT"
  },
  "payload": {
    "sub": "john.doe",
    "userId": "user123",
    "iat": 1640995200,
    "exp": 1640998800
  }
}
```

#### 5. Response
```http
HTTP/1.1 200 OK
Content-Type: application/json

{
  "id": "user123",
  "firstName": "John",
  "lastName": "Doe",
  "username": "john.doe",
  "email": "john.doe@example.com",
  "profilePictureUrl": "https://example.com/avatar.jpg",
  "jwtToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
}
```

---

## User Browses Courses

### Sequence Diagram
```
Client                    Gateway                Course Service         MongoDB
  │                         │                        │                     │
  │ GET /api/courses        │                        │                     │
  │ Authorization: Bearer   │                        │                     │
  │ <JWT_TOKEN>             │                        │                     │
  │─────────────────────────>│                        │                     │
  │                         │                        │                     │
  │                         │ Validate JWT            │                     │
  │                         │ Extract User Info       │                     │
  │                         │                        │                     │
  │                         │ GET /api/v1/courses    │                     │
  │                         │ X-User-ID: user123     │                     │
  │                         │────────────────────────>│                     │
  │                         │                        │                     │
  │                         │                        │ Find All Courses    │
  │                         │                        │─────────────────────>│
  │                         │                        │                     │
  │                         │                        │<────────────────────│
  │                         │                        │                     │
  │                         │<────────────────────────│                     │
  │                         │                        │                     │
  │<────────────────────────│                        │                     │
  │                         │                        │                     │
```

### Detailed Flow

#### 1. Client Request
```http
GET /api/courses
Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...
```

#### 2. API Gateway Processing
```java
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    
    @Override
    protected void doFilterInternal(HttpServletRequest request, 
                                  HttpServletResponse response, 
                                  FilterChain filterChain) {
        
        // 1. Extract JWT from Authorization header
        String jwt = extractJwtFromRequest(request);
        
        // 2. Validate JWT
        if (jwt != null && jwtUtils.validateToken(jwt)) {
            // 3. Extract user information
            String username = jwtUtils.getUsernameFromToken(jwt);
            String userId = jwtUtils.getUserIdFromToken(jwt);
            
            // 4. Add user info to request headers
            request.setAttribute("X-User-ID", userId);
            request.setAttribute("X-Username", username);
        }
        
        filterChain.doFilter(request, response);
    }
}
```

#### 3. Course Service Processing
```java
@GetMapping("/api/v1/courses")
public ResponseEntity<List<CourseResponse>> getAllCourses(
        @RequestHeader(value = "X-User-ID", required = false) String userId) {
    
    log.info("Fetching all courses for user: {}", userId);
    
    // 1. Get all courses
    List<Course> courses = courseRepository.findAll();
    
    // 2. Transform to DTOs
    List<CourseResponse> responses = courses.stream()
        .map(CourseMapper::toCourseResponse)
        .collect(Collectors.toList());
    
    return ResponseEntity.ok(responses);
}
```

#### 4. Response
```json
[
  {
    "id": "course123",
    "title": "JavaScript Fundamentals",
    "description": "Learn the basics of JavaScript",
    "instructor": "John Doe",
    "isPublic": true,
    "published": true,
    "language": "EN",
    "level": "BEGINNER",
    "rating": 4.5,
    "numberOfEnrolledUsers": 150,
    "thumbnailUrl": "https://example.com/js-course.jpg",
    "skills": [...],
    "modules": [...],
    "categories": [...]
  }
]
```

---

## User Enrolls in Course

### Sequence Diagram
```
Client                    Gateway                Course Service         MongoDB
  │                         │                        │                     │
  │ POST /api/courses/123/  │                        │                     │
  │ enroll                  │                        │                     │
  │ Authorization: Bearer   │                        │                     │
  │ <JWT_TOKEN>             │                        │                     │
  │─────────────────────────>│                        │                     │
  │                         │                        │                     │
  │                         │ Validate JWT            │                     │
  │                         │ Extract User Info       │                     │
  │                         │                        │                     │
  │                         │ POST /api/v1/courses/   │                     │
  │                         │ 123/enroll/user123     │                     │
  │                         │────────────────────────>│                     │
  │                         │                        │                     │
  │                         │                        │ Find Course         │
  │                         │                        │─────────────────────>│
  │                         │                        │                     │
  │                         │                        │<────────────────────│
  │                         │                        │                     │
  │                         │                        │ Check Enrollment    │
  │                         │                        │─────────────────────>│
  │                         │                        │                     │
  │                         │                        │<────────────────────│
  │                         │                        │                     │
  │                         │                        │ Create Enrollment   │
  │                         │                        │─────────────────────>│
  │                         │                        │                     │
  │                         │                        │ Update Course Count │
  │                         │                        │─────────────────────>│
  │                         │                        │                     │
  │                         │<────────────────────────│                     │
  │                         │                        │                     │
  │<────────────────────────│                        │                     │
  │                         │                        │                     │
```

### Detailed Flow

#### 1. Client Request
```http
POST /api/courses/course123/enroll
Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...
```

#### 2. API Gateway Processing
- **Route**: `/api/courses/{courseId}/enroll` → Course Service `/api/v1/courses/{courseId}/enroll/{userId}`
- **Authentication**: JWT validation required
- **User Extraction**: User ID extracted from JWT and added to path

#### 3. Course Service Processing
```java
@PostMapping("/{courseId}/enroll/{userId}")
@Transactional
public ResponseEntity<EnrolledCourseResponse> enrollUserInCourse(
        @PathVariable String courseId, 
        @PathVariable String userId) {
    
    log.info("Enrolling user {} in course {}", userId, courseId);
    
    // 1. Find course
    Course course = courseRepository.findById(courseId)
        .orElseThrow(() -> new ResourceNotFoundException("Course not found"));
    
    // 2. Check if already enrolled
    if (userCourseRepository.existsByCourseIdAndProgressUserId(courseId, userId)) {
        throw new IllegalArgumentException("User is already enrolled");
    }
    
    // 3. Create enrollment
    EnrolledCourse enrolledCourse = EnrolledCourse.builder()
        .course(course)
        .progress(CourseProgress.builder()
            .courseId(courseId)
            .userId(userId)
            .enrolledAt(LocalDateTime.now())
            .lastAccessedAt(LocalDateTime.now())
            .build())
        .build();
    
    // 4. Save enrollment
    EnrolledCourse savedEnrolledCourse = userCourseRepository.save(enrolledCourse);
    
    // 5. Update course enrollment count
    course.setNumberOfEnrolledUsers(course.getNumberOfEnrolledUsers() + 1);
    courseRepository.save(course);
    
    return ResponseEntity.ok(EnrolledCourseMapper.toEnrolledCourseResponse(savedEnrolledCourse));
}
```

#### 4. Database Operations
```javascript
// 1. Insert enrollment record
db.enrolled_courses.insertOne({
  _id: ObjectId("..."),
  course: {
    id: "course123",
    title: "JavaScript Fundamentals",
    // ... full course object
  },
  progress: {
    courseId: "course123",
    userId: "user123",
    progress: 0.0,
    enrolledAt: ISODate("2024-01-01T10:00:00Z"),
    lastAccessedAt: ISODate("2024-01-01T10:00:00Z"),
    completed: false,
    completedAt: null
  }
});

// 2. Update course enrollment count
db.courses.updateOne(
  { _id: "course123" },
  { $inc: { numberOfEnrolledUsers: 1 } }
);
```

#### 5. Response
```http
HTTP/1.1 200 OK
Content-Type: application/json

{
  "course": {
    "id": "course123",
    "title": "JavaScript Fundamentals",
    "description": "Learn the basics of JavaScript",
    "instructor": "John Doe",
    "isPublic": true,
    "published": true,
    "language": "EN",
    "level": "BEGINNER",
    "rating": 4.5,
    "numberOfEnrolledUsers": 151,
    "thumbnailUrl": "https://example.com/js-course.jpg"
  },
  "progress": {
    "courseId": "course123",
    "userId": "user123",
    "progress": 0.0,
    "enrolledAt": "2024-01-01T10:00:00Z",
    "lastAccessedAt": "2024-01-01T10:00:00Z",
    "completed": false,
    "completedAt": null
  }
}
```

---

## User Attends Course & Progress Tracking

### Sequence Diagram
```
Client                    Gateway                Course Service         MongoDB
  │                         │                        │                     │
  │ PUT /api/courses/123/   │                        │                     │
  │ progress                │                        │                     │
  │ Authorization: Bearer   │                        │                     │
  │ <JWT_TOKEN>             │                        │                     │
  │ {progress: 25.5}        │                        │                     │
  │─────────────────────────>│                        │                     │
  │                         │                        │                     │
  │                         │ Validate JWT            │                     │
  │                         │ Extract User Info       │                     │
  │                         │                        │                     │
  │                         │ PUT /api/v1/courses/    │                     │
  │                         │ 123/progress/user123    │                     │
  │                         │ {progress: 25.5}        │                     │
  │                         │────────────────────────>│                     │
  │                         │                        │                     │
  │                         │                        │ Find Enrollment     │
  │                         │                        │─────────────────────>│
  │                         │                        │                     │
  │                         │                        │<────────────────────│
  │                         │                        │                     │
  │                         │                        │ Update Progress     │
  │                         │                        │─────────────────────>│
  │                         │                        │                     │
  │                         │                        │ Check Completion    │
  │                         │                        │                     │
  │                         │<────────────────────────│                     │
  │                         │                        │                     │
  │<────────────────────────│                        │                     │
  │                         │                        │                     │
```

### Detailed Flow

#### 1. Client Request
```http
PUT /api/courses/course123/progress
Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...
Content-Type: application/json

{
  "progress": 25.5,
  "lessonId": "lesson456",
  "completed": false
}
```

#### 2. Course Service Processing
```java
@PutMapping("/{courseId}/progress/{userId}")
@Transactional
public ResponseEntity<CourseProgressResponse> updateProgress(
        @PathVariable String courseId,
        @PathVariable String userId,
        @RequestBody CourseProgressRequest request) {
    
    log.info("Updating progress for user {} in course {}: {}%", 
             userId, courseId, request.getProgress());
    
    // 1. Find enrollment
    EnrolledCourse enrolledCourse = userCourseRepository
        .findByCourseIdAndProgressUserId(courseId, userId)
        .orElseThrow(() -> new ResourceNotFoundException("Enrollment not found"));
    
    // 2. Update progress
    CourseProgress progress = enrolledCourse.getProgress();
    progress.setProgress(request.getProgress());
    progress.setLastAccessedAt(LocalDateTime.now());
    
    // 3. Check if course is completed
    if (request.getProgress() >= 100.0 && !progress.isCompleted()) {
        progress.setCompleted(true);
        progress.setCompletedAt(LocalDateTime.now());
        
        // 4. Move to completed courses (optional)
        // This could trigger additional business logic
    }
    
    // 5. Save updated progress
    userCourseRepository.save(enrolledCourse);
    
    return ResponseEntity.ok(CourseProgressMapper.toCourseProgressResponse(progress));
}
```

#### 3. Database Operations
```javascript
// Update progress
db.enrolled_courses.updateOne(
  { 
    "course.id": "course123",
    "progress.userId": "user123"
  },
  {
    $set: {
      "progress.progress": 25.5,
      "progress.lastAccessedAt": ISODate("2024-01-01T11:30:00Z"),
      "progress.completed": false
    }
  }
);

// If completed, update completion status
db.enrolled_courses.updateOne(
  { 
    "course.id": "course123",
    "progress.userId": "user123"
  },
  {
    $set: {
      "progress.completed": true,
      "progress.completedAt": ISODate("2024-01-01T11:30:00Z")
    }
  }
);
```

#### 4. Response
```http
HTTP/1.1 200 OK
Content-Type: application/json

{
  "courseId": "course123",
  "userId": "user123",
  "progress": 25.5,
  "enrolledAt": "2024-01-01T10:00:00Z",
  "lastAccessedAt": "2024-01-01T11:30:00Z",
  "completed": false,
  "completedAt": null
}
```

---

## User Unenrolls from Course

### Sequence Diagram
```
Client                    Gateway                Course Service         MongoDB
  │                         │                        │                     │
  │ DELETE /api/courses/123/│                        │                     │
  │ enroll                  │                        │                     │
  │ Authorization: Bearer   │                        │                     │
  │ <JWT_TOKEN>             │                        │                     │
  │─────────────────────────>│                        │                     │
  │                         │                        │                     │
  │                         │ Validate JWT            │                     │
  │                         │ Extract User Info       │                     │
  │                         │                        │                     │
  │                         │ DELETE /api/v1/courses/ │                     │
  │                         │ 123/enroll/user123     │                     │
  │                         │────────────────────────>│                     │
  │                         │                        │                     │
  │                         │                        │ Find Enrollment     │
  │                         │                        │─────────────────────>│
  │                         │                        │                     │
  │                         │                        │<────────────────────│
  │                         │                        │                     │
  │                         │                        │ Delete Enrollment   │
  │                         │                        │─────────────────────>│
  │                         │                        │                     │
  │                         │                        │ Update Course Count │
  │                         │                        │─────────────────────>│
  │                         │                        │                     │
  │                         │<────────────────────────│                     │
  │                         │                        │                     │
  │<────────────────────────│                        │                     │
  │                         │                        │                     │
```

### Detailed Flow

#### 1. Client Request
```http
DELETE /api/courses/course123/enroll
Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...
```

#### 2. Course Service Processing
```java
@DeleteMapping("/{courseId}/enroll/{userId}")
@Transactional
public ResponseEntity<Void> unenrollUserFromCourse(
        @PathVariable String courseId, 
        @PathVariable String userId) {
    
    log.info("Unenrolling user {} from course {}", userId, courseId);
    
    // 1. Find enrollment
    EnrolledCourse enrolledCourse = userCourseRepository
        .findByCourseIdAndProgressUserId(courseId, userId)
        .orElseThrow(() -> new ResourceNotFoundException("Enrollment not found"));
    
    // 2. Delete enrollment
    userCourseRepository.delete(enrolledCourse);
    
    // 3. Update course enrollment count
    Course course = courseRepository.findById(courseId).orElse(null);
    if (course != null) {
        course.setNumberOfEnrolledUsers(
            Math.max(0, course.getNumberOfEnrolledUsers() - 1)
        );
        courseRepository.save(course);
    }
    
    return ResponseEntity.noContent().build();
}
```

#### 3. Database Operations
```javascript
// 1. Delete enrollment
db.enrolled_courses.deleteOne({
  "course.id": "course123",
  "progress.userId": "user123"
});

// 2. Update course enrollment count
db.courses.updateOne(
  { _id: "course123" },
  { $inc: { numberOfEnrolledUsers: -1 } }
);
```

#### 4. Response
```http
HTTP/1.1 204 No Content
```

---

## User Bookmarks Course

### Sequence Diagram
```
Client                    Gateway                Course Service         MongoDB
  │                         │                        │                     │
  │ POST /api/courses/123/  │                        │                     │
  │ bookmark                │                        │                     │
  │ Authorization: Bearer   │                        │                     │
  │ <JWT_TOKEN>             │                        │                     │
  │─────────────────────────>│                        │                     │
  │                         │                        │                     │
  │                         │ Validate JWT            │                     │
  │                         │ Extract User Info       │                     │
  │                         │                        │                     │
  │                         │ POST /api/v1/courses/   │                     │
  │                         │ 123/bookmark/user123    │                     │
  │                         │────────────────────────>│                     │
  │                         │                        │                     │
  │                         │                        │ Verify Course Exists│
  │                         │                        │─────────────────────>│
  │                         │                        │                     │
  │                         │                        │<────────────────────│
  │                         │                        │                     │
  │                         │                        │ Check Existing      │
  │                         │                        │ Bookmark            │
  │                         │                        │─────────────────────>│
  │                         │                        │                     │
  │                         │                        │<────────────────────│
  │                         │                        │                     │
  │                         │                        │ Create Bookmark     │
  │                         │                        │─────────────────────>│
  │                         │                        │                     │
  │                         │<────────────────────────│                     │
  │                         │                        │                     │
  │<────────────────────────│                        │                     │
  │                         │                        │                     │
```

### Detailed Flow

#### 1. Client Request
```http
POST /api/courses/course123/bookmark
Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...
```

#### 2. Course Service Processing
```java
@PostMapping("/{courseId}/bookmark/{userId}")
@Transactional
public ResponseEntity<Void> bookmarkCourseForUser(
        @PathVariable String courseId, 
        @PathVariable String userId) {
    
    log.info("Bookmarking course {} for user {}", courseId, userId);
    
    // 1. Verify course exists
    if (!courseRepository.existsById(courseId)) {
        throw new ResourceNotFoundException("Course not found");
    }
    
    // 2. Check if already bookmarked
    if (userBookmarkRepository.existsByUserIdAndCourseId(userId, courseId)) {
        throw new IllegalArgumentException("Course is already bookmarked");
    }
    
    // 3. Create bookmark
    UserBookmark bookmark = UserBookmark.builder()
        .userId(userId)
        .courseId(courseId)
        .bookmarkedAt(LocalDateTime.now())
        .build();
    
    // 4. Save bookmark
    userBookmarkRepository.save(bookmark);
    
    return ResponseEntity.ok().build();
}
```

#### 3. Database Operations
```javascript
// Insert bookmark
db.user_bookmarks.insertOne({
  _id: ObjectId("..."),
  userId: "user123",
  courseId: "course123",
  bookmarkedAt: ISODate("2024-01-01T12:00:00Z")
});
```

#### 4. Response
```http
HTTP/1.1 200 OK
```

---

## Anonymous User Browses Public Courses

### Sequence Diagram
```
Client                    Gateway                Course Service         MongoDB
  │                         │                        │                     │
  │ GET /api/courses/public │                        │                     │
  │ (No Authorization)      │                        │                     │
  │─────────────────────────>│                        │                     │
  │                         │                        │                     │
  │                         │ No JWT - Allow Public  │                     │
  │                         │ Access                 │                     │
  │                         │                        │                     │
  │                         │ GET /api/v1/courses/   │                     │
  │                         │ public                 │                     │
  │                         │────────────────────────>│                     │
  │                         │                        │                     │
  │                         │                        │ Find Public Courses │
  │                         │                        │─────────────────────>│
  │                         │                        │                     │
  │                         │                        │<────────────────────│
  │                         │                        │                     │
  │                         │<────────────────────────│                     │
  │                         │                        │                     │
  │<────────────────────────│                        │                     │
  │                         │                        │                     │
```

### Detailed Flow

#### 1. Client Request
```http
GET /api/courses/public
```

#### 2. API Gateway Processing
```java
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    
    @Override
    protected void doFilterInternal(HttpServletRequest request, 
                                  HttpServletResponse response, 
                                  FilterChain filterChain) {
        
        // 1. Check if this is a public endpoint
        if (isPublicEndpoint(request.getRequestURI())) {
            // Allow access without authentication
            filterChain.doFilter(request, response);
            return;
        }
        
        // 2. For protected endpoints, validate JWT
        String jwt = extractJwtFromRequest(request);
        if (jwt != null && jwtUtils.validateToken(jwt)) {
            // Add user info to request
            String userId = jwtUtils.getUserIdFromToken(jwt);
            request.setAttribute("X-User-ID", userId);
        }
        
        filterChain.doFilter(request, response);
    }
    
    private boolean isPublicEndpoint(String uri) {
        return uri.startsWith("/api/courses/public") || 
               uri.startsWith("/api/courses/public/published");
    }
}
```

#### 3. Course Service Processing
```java
@GetMapping("/public")
public ResponseEntity<List<CourseResponse>> getPublicCourses() {
    log.info("Fetching public courses for landing page");
    
    // 1. Find all public courses
    List<Course> publicCourses = courseRepository.findByIsPublicTrue();
    
    // 2. Transform to DTOs
    List<CourseResponse> responses = publicCourses.stream()
        .map(CourseMapper::toCourseResponse)
        .collect(Collectors.toList());
    
    return ResponseEntity.ok(responses);
}
```

#### 4. Response
```json
[
  {
    "id": "course123",
    "title": "JavaScript Fundamentals",
    "description": "Learn the basics of JavaScript",
    "instructor": "John Doe",
    "isPublic": true,
    "published": true,
    "language": "EN",
    "level": "BEGINNER",
    "rating": 4.5,
    "numberOfEnrolledUsers": 150,
    "thumbnailUrl": "https://example.com/js-course.jpg"
  }
]
```

#### 5. Attempting to Enroll (Unauthenticated)
If an anonymous user tries to enroll:

```http
POST /api/courses/course123/enroll
```

**Response:**
```http
HTTP/1.1 401 Unauthorized
Content-Type: application/json

{
  "error": "Unauthorized",
  "message": "Authentication required",
  "timestamp": "2024-01-01T12:00:00Z"
}
```

---

## Security & Error Handling

### JWT Token Validation
```java
@Component
public class JwtUtils {
    
    @Value("${jwt.secret}")
    private String jwtSecret;
    
    @Value("${jwt.expiration}")
    private int jwtExpirationMs;
    
    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder()
                .setSigningKey(Keys.hmacShaKeyFor(jwtSecret.getBytes()))
                .build()
                .parseClaimsJws(token);
            return true;
        } catch (Exception e) {
            log.error("JWT validation error: {}", e.getMessage());
            return false;
        }
    }
    
    public String getUsernameFromToken(String token) {
        return Jwts.parserBuilder()
            .setSigningKey(Keys.hmacShaKeyFor(jwtSecret.getBytes()))
            .build()
            .parseClaimsJws(token)
            .getBody()
            .getSubject();
    }
}
```

### Error Handling
```java
@ControllerAdvice
public class GlobalExceptionHandler {
    
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiError> handleResourceNotFound(ResourceNotFoundException ex) {
        ApiError error = ApiError.builder()
            .error("Not Found")
            .message(ex.getMessage())
            .timestamp(LocalDateTime.now())
            .build();
        
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }
    
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiError> handleIllegalArgument(IllegalArgumentException ex) {
        ApiError error = ApiError.builder()
            .error("Bad Request")
            .message(ex.getMessage())
            .timestamp(LocalDateTime.now())
            .build();
        
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }
    
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ApiError> handleAccessDenied(AccessDeniedException ex) {
        ApiError error = ApiError.builder()
            .error("Forbidden")
            .message("Access denied")
            .timestamp(LocalDateTime.now())
            .build();
        
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(error);
    }
}
```

### Rate Limiting
```java
@Component
public class RateLimitingFilter extends OncePerRequestFilter {
    
    private final Map<String, RateLimitInfo> rateLimitMap = new ConcurrentHashMap<>();
    
    @Override
    protected void doFilterInternal(HttpServletRequest request, 
                                  HttpServletResponse response, 
                                  FilterChain filterChain) {
        
        String clientId = getClientId(request);
        RateLimitInfo rateLimitInfo = rateLimitMap.get(clientId);
        
        if (rateLimitInfo != null && rateLimitInfo.isExceeded()) {
            response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
            response.getWriter().write("Rate limit exceeded");
            return;
        }
        
        // Update rate limit info
        updateRateLimit(clientId);
        
        filterChain.doFilter(request, response);
    }
}
```

---

## Best Practices

### 1. Service Separation
- **User Service**: Handles authentication, user profiles, JWT issuance
- **Course Service**: Handles course management, enrollment, progress
- **API Gateway**: Handles routing, authentication, rate limiting

### 2. Security
- JWT tokens for authentication
- Rate limiting on all endpoints
- CORS configuration for frontend access
- Input validation on all DTOs
- SQL injection prevention (MongoDB)

### 3. Error Handling
- Consistent error response format
- Proper HTTP status codes
- Detailed logging for debugging
- Graceful degradation

### 4. Performance
- Database indexing on frequently queried fields
- Connection pooling
- Caching for public course data
- Pagination for large datasets

### 5. Monitoring
- Request/response logging
- Performance metrics
- Error tracking
- User activity analytics

### 6. Data Consistency
- Transactional operations for enrollment/unenrollment
- Atomic updates for progress tracking
- Proper rollback mechanisms

### 7. API Design
- RESTful conventions
- Consistent naming
- Versioning strategy
- Comprehensive documentation

This documentation provides a complete understanding of the backend architecture and flow for all user scenarios in the SkillForge platform. 