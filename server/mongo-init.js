// MongoDB Initialization Script for SkillForge Development
// This script sets up the databases and users for the microservices

print('Starting SkillForge MongoDB initialization...');

// Switch to admin database
db = db.getSiblingDB('admin');

// Create root user if it doesn't exist
try {
  db.createUser({
    user: 'skillForgeDevops25',
    pwd: 'PickleR1cK!',
    roles: [
      { role: 'root', db: 'admin' }
    ]
  });
  print('Root user created successfully');
} catch (error) {
  print('Root user already exists or error occurred:', error.message);
}

// Switch to skillForge database
db = db.getSiblingDB('skillForge');

// Create databases for microservices
print('Creating databases for microservices...');

// Create skillforge_users database
db = db.getSiblingDB('skillforge_users');
print('Created skillforge_users database');

// Create skillforge_courses database
db = db.getSiblingDB('skillforge_courses');
print('Created skillforge_courses database');

// Switch back to skillForge database
db = db.getSiblingDB('skillForge');

// Create collections for the main database
db.createCollection('users');
db.createCollection('courses');
db.createCollection('enrollments');
db.createCollection('progress');
db.createCollection('bookmarks');

print('Created collections in skillForge database');

// Create indexes for better performance
print('Creating indexes...');

// Users collection indexes
db.users.createIndex({ "username": 1 }, { unique: true });
db.users.createIndex({ "email": 1 }, { unique: true });

// Courses collection indexes
db.courses.createIndex({ "title": 1 });
db.courses.createIndex({ "isPublic": 1 });
db.courses.createIndex({ "published": 1 });
db.courses.createIndex({ "language": 1 });
db.courses.createIndex({ "level": 1 });

// Enrollments collection indexes
db.enrolled_courses.createIndex({ "progress.userId": 1 });
db.enrolled_courses.createIndex({ "course.id": 1 });
db.enrolled_courses.createIndex({ "progress.courseId": 1, "progress.userId": 1 }, { unique: true });

// Progress collection indexes
db.course_progress.createIndex({ "userId": 1 });
db.course_progress.createIndex({ "courseId": 1 });
db.course_progress.createIndex({ "userId": 1, "courseId": 1 }, { unique: true });

// Bookmarks collection indexes
db.user_bookmarks.createIndex({ "userId": 1 });
db.user_bookmarks.createIndex({ "courseId": 1 });
db.user_bookmarks.createIndex({ "userId": 1, "courseId": 1 }, { unique: true });

print('Indexes created successfully');

// Insert some sample data for development
print('Inserting sample data...');

// Sample user
db.users.insertOne({
  _id: ObjectId(),
  username: "devuser",
  email: "dev@skillforge.com",
  firstName: "Development",
  lastName: "User",
  passwordHash: "$2a$10$dummy.hash.for.development.only",
  profilePictureUrl: "https://example.com/avatar.jpg",
  createdAt: new Date(),
  updatedAt: new Date()
});

// Sample course
db.courses.insertOne({
  _id: ObjectId(),
  title: "JavaScript Fundamentals",
  description: "Learn the basics of JavaScript programming",
  instructor: "John Doe",
  isPublic: true,
  published: true,
  language: "EN",
  level: "BEGINNER",
  rating: 4.5,
  numberOfEnrolledUsers: 0,
  thumbnailUrl: "https://example.com/js-course.jpg",
  skills: ["JavaScript", "Programming", "Web Development"],
  categories: ["Programming", "Web Development"],
  modules: [],
  createdAt: new Date(),
  updatedAt: new Date()
});

print('Sample data inserted successfully');

print('SkillForge MongoDB initialization completed successfully!');
print('Database: skillForge');
print('User: skillForge');
print('Password: PickleR1cK!');
print('');
print('Connection strings:');
print('MongoDB: mongodb://skillForge:PickleR1cK!@localhost:27017/skillForge');
print('Users DB: mongodb://skillForge:PickleR1cK!@localhost:27017/skillforge_users');
print('Courses DB: mongodb://skillForge:PickleR1cK!@localhost:27017/skillforge_courses'); 