# Public Courses

## API Endpoints

### Get Public Courses
```
GET /api/v1/courses/public
```
Returns all courses marked as public (regardless of published status).

**Response:**
```json
[
  {
    "id": "course-id",
    "title": "Course Title",
    "description": "Course Description",
    "instructor": "Instructor Name",
    "isPublic": true,
    "published": true,
    "language": "EN",
    "level": "BEGINNER",
    "rating": 4.5,
    "numberOfEnrolledUsers": 100,
    "thumbnailUrl": "https://example.com/thumbnail.jpg",
    "skills": [],
    "modules": [],
    "categories": []
  }
]
```

### Get Public and Published Courses
```
GET /api/v1/courses/public/published
```
Returns only courses that are both public AND published (ready for enrollment).

**Response:** Same as above, but only includes courses where `isPublic: true` AND `published: true`.

## Use Cases

### Landing Page Display
Use `/api/v1/courses/public` to get all courses that should be visible on your landing page, regardless of whether they're ready for enrollment.

### Featured Courses
Use `/api/v1/courses/public/published` to get courses that are both public and ready for enrollment - perfect for a "Featured Courses" section.

## Course States

| isPublic | published | Description |
|----------|-----------|-------------|
| false | false | Private draft course |
| false | true | Private published course (enrollable but not showcased) |
| true | false | Public draft course (showcased but not enrollable) |
| true | true | Public published course (showcased and enrollable) |

## Implementation Details

### Repository Methods
- `findByIsPublicTrue()` - Find all public courses
- `findByIsPublicTrueAndPublishedTrue()` - Find public and published courses

### Service Methods
- `getPublicCourses()` - Returns all public courses
- `getPublicPublishedCourses()` - Returns public and published courses

### Controller Endpoints
- `GET /api/v1/courses/public` - Public courses endpoint
- `GET /api/v1/courses/public/published` - Public and published courses endpoint

## Migration Notes
- Existing courses will have `isPublic: false` by default
- No data migration required
- New courses can be created with `isPublic: true` to make them public

## Security Considerations
- Public endpoints are read-only
- No authentication required for public course listing
- Course details are limited to what should be publicly visible
- Full course content (modules, lessons) is not exposed through public endpoints 