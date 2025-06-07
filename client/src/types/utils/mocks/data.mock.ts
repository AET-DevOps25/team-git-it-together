// =====================
// Categories
// =====================
import {
  CategoryResponse, CourseProgressResponse,
  CourseResponse, EnrolledCourseResponse,
  Language,
  LessonResponse,
  Level,
  ModuleResponse,
  SkillResponse,
} from '@/types';
import { LessonContentType } from '@/types/utils/LessonContentType.ts';

export const mockCategories: CategoryResponse[] = [
  { id: "cat-1", name: "Programming", description: "Coding and software development." },
  { id: "cat-2", name: "Design", description: "UI/UX, graphics, and visual arts." },
  { id: "cat-3", name: "Business", description: "Entrepreneurship and management." },
  { id: "cat-4", name: "Languages", description: "Foreign language acquisition." },
];

export const mockInterests: CategoryResponse[] = [
  mockCategories[0], // Programming
  mockCategories[1], // Design
];

// =====================
// Skills
// =====================
export const mockSkills: SkillResponse[] = [
  {
    id: "skill-1",
    name: "JavaScript",
    description: "Frontend and backend scripting.",
    category: mockCategories[0],
    difficultyLevel: Level.BEGINNER,
  },
  {
    id: "skill-2",
    name: "React",
    description: "Modern web interfaces with React.",
    category: mockCategories[0],
    difficultyLevel: Level.INTERMEDIATE,
  },
  {
    id: "skill-3",
    name: "Photoshop",
    description: "Graphic design and photo editing.",
    category: mockCategories[1],
    difficultyLevel: Level.BEGINNER,
  },
  {
    id: "skill-4",
    name: "UX Research",
    description: "User research and personas.",
    category: mockCategories[1],
    difficultyLevel: Level.INTERMEDIATE,
  },
  {
    id: "skill-5",
    name: "Business Plan Writing",
    description: "Build a plan for startups.",
    category: mockCategories[2],
    difficultyLevel: Level.ADVANCED,
  },
  {
    id: "skill-6",
    name: "Spanish Grammar",
    description: "Speak and write correct Spanish.",
    category: mockCategories[3],
    difficultyLevel: Level.BEGINNER,
  },
];

export const mockUserSkills: SkillResponse[] = [
  mockSkills[0], // JavaScript
  mockSkills[1], // React
  mockSkills[2], // Photoshop
];

export const mockSkillsInProgress: SkillResponse[] = [
  mockSkills[1], // React
  mockSkills[3], // UX Research
];

// =====================
// Lessons
// =====================
export const mockLessons: LessonResponse[] = [
  {
    title: "JS Basics",
    description: "Introduction to JavaScript syntax.",
    content: { type: LessonContentType.TEXT, content: "Variables, types, and operators in JS." },
    order: 1,
  },
  {
    title: "Working with DOM",
    description: "Manipulating the DOM tree.",
    content: { type: LessonContentType.HTML, content: "<div>Hello JS!</div>" },
    order: 2,
  },
  {
    title: "Intro to React",
    description: "JSX, components, and props.",
    content: { type: LessonContentType.TEXT, content: "Learn how to build UI with React." },
    order: 1,
  },
  {
    title: "React State Management",
    description: "Using useState and useReducer.",
    content: { type: LessonContentType.TEXT, content: "Hooks for dynamic UIs." },
    order: 2,
  },
  {
    title: "Basic Photo Editing",
    description: "Cropping and adjusting images.",
    content: { type: LessonContentType.IMAGE, content: "https://source.unsplash.com/400x200/?editing,photo" },
    order: 1,
  },
  {
    title: "User Interviews",
    description: "Preparing questions for research.",
    content: { type: LessonContentType.TEXT, content: "Best practices for interviewing users." },
    order: 1,
  },
  {
    title: "Executive Summary",
    description: "How to write an executive summary.",
    content: { type: LessonContentType.TEXT, content: "This is the key section of any plan." },
    order: 1,
  },
  {
    title: "Verb Conjugations",
    description: "Regular and irregular Spanish verbs.",
    content: { type: LessonContentType.TEXT, content: "Ser, estar, tener, etc." },
    order: 1,
  },
];

// =====================
// Modules
// =====================
export const mockModules: ModuleResponse[] = [
  {
    title: "JS Fundamentals",
    description: "Start here if you are new to programming.",
    courseId: "course-1",
    lessons: [mockLessons[0], mockLessons[1]],
    order: 1,
  },
  {
    title: "React Essentials",
    description: "Build modern web apps.",
    courseId: "course-2",
    lessons: [mockLessons[2], mockLessons[3]],
    order: 1,
  },
  {
    title: "Editing Basics",
    description: "Introduction to Photoshop tools.",
    courseId: "course-3",
    lessons: [mockLessons[4]],
    order: 1,
  },
  {
    title: "UX Research 101",
    description: "The basics of UX research.",
    courseId: "course-4",
    lessons: [mockLessons[5]],
    order: 1,
  },
  {
    title: "Planning a Business",
    description: "Start your business journey.",
    courseId: "course-5",
    lessons: [mockLessons[6]],
    order: 1,
  },
  {
    title: "Spanish Grammar",
    description: "Core grammar concepts.",
    courseId: "course-6",
    lessons: [mockLessons[7]],
    order: 1,
  },
];

// =====================
// Courses
// =====================
export const mockCourses: CourseResponse[] = [
  {
    id: "course-1",
    title: "JavaScript for Beginners",
    description: "Learn JavaScript from scratch.",
    instructor: "Alice Johnson",
    skills: [mockSkills[0]],
    modules: [mockModules[0]],
    numberOfEnrolledUsers: 240,
    categories: [mockCategories[0]],
    level: Level.BEGINNER,
    thumbnailUrl: "https://source.unsplash.com/400x200/?javascript,code",
    published: true,
    language: Language.ENGLISH,
    rating: 4.8,
  },
  {
    id: "course-2",
    title: "React from Zero to Hero",
    description: "Master React for modern web apps.",
    instructor: "Bob Lee",
    skills: [mockSkills[1]],
    modules: [mockModules[1]],
    numberOfEnrolledUsers: 178,
    categories: [mockCategories[0]],
    level: Level.INTERMEDIATE,
    thumbnailUrl: "https://source.unsplash.com/400x200/?react,web",
    published: true,
    language: Language.ENGLISH,
    rating: 4.7,
  },
  {
    id: "course-3",
    title: "Photoshop Basics",
    description: "Edit photos like a pro.",
    instructor: "Camille Dupont",
    skills: [mockSkills[2]],
    modules: [mockModules[2]],
    numberOfEnrolledUsers: 115,
    categories: [mockCategories[1]],
    level: Level.BEGINNER,
    thumbnailUrl: "https://source.unsplash.com/400x200/?photoshop,design",
    published: true,
    language: Language.ENGLISH,
    rating: 4.5,
  },
  {
    id: "course-4",
    title: "UX Research Masterclass",
    description: "Research for excellent user experience.",
    instructor: "Dana Kim",
    skills: [mockSkills[3]],
    modules: [mockModules[3]],
    numberOfEnrolledUsers: 62,
    categories: [mockCategories[1]],
    level: Level.INTERMEDIATE,
    thumbnailUrl: "https://source.unsplash.com/400x200/?ux,users",
    published: true,
    language: Language.ENGLISH,
    rating: 4.6,
  },
  {
    id: "course-5",
    title: "Business Plan Essentials",
    description: "Everything you need to create a business plan.",
    instructor: "Eva Schmidt",
    skills: [mockSkills[4]],
    modules: [mockModules[4]],
    numberOfEnrolledUsers: 85,
    categories: [mockCategories[2]],
    level: Level.ADVANCED,
    thumbnailUrl: "https://source.unsplash.com/400x200/?business,plan",
    published: true,
    language: Language.ENGLISH,
    rating: 4.4,
  },
  {
    id: "course-6",
    title: "Spanish for Beginners",
    description: "Start speaking Spanish today.",
    instructor: "Francisco García",
    skills: [mockSkills[5]],
    modules: [mockModules[5]],
    numberOfEnrolledUsers: 191,
    categories: [mockCategories[3]],
    level: Level.BEGINNER,
    thumbnailUrl: "https://source.unsplash.com/400x200/?spanish,language",
    published: true,
    language: Language.SPANISH,
    rating: 4.9,
  },
];

export const mockBookmarkedCourses: CourseResponse[] = [
  mockCourses[0], // JavaScript for Beginners
  mockCourses[1], // React from Zero to Hero
  mockCourses[3], // UX Research Masterclass
];

// =====================
// Progress
// =====================
export const mockProgresses: CourseProgressResponse[] = [
  {
    courseId: "course-1",
    userId: "user-001",
    progress: 100,
    enrolledAt: "2024-04-01T08:00:00Z",
    lastAccessedAt: "2024-05-12T14:20:00Z",
    completed: true,
    completedAt: "2024-05-12T14:20:00Z",
  },
  {
    courseId: "course-2",
    userId: "user-001",
    progress: 50,
    enrolledAt: "2024-05-02T10:00:00Z",
    lastAccessedAt: "2024-06-06T13:00:00Z",
    completed: false,
  },
  {
    courseId: "course-3",
    userId: "user-001",
    progress: 10,
    enrolledAt: "2024-05-12T12:00:00Z",
    lastAccessedAt: "2024-06-03T09:00:00Z",
    completed: false,
  },
  {
    courseId: "course-4",
    userId: "user-001",
    progress: 100,
    enrolledAt: "2024-01-15T11:30:00Z",
    lastAccessedAt: "2024-03-20T16:00:00Z",
    completed: true,
    completedAt: "2024-03-20T16:00:00Z",
  },
  {
    courseId: "course-5",
    userId: "user-001",
    progress: 30,
    enrolledAt: "2024-05-21T17:00:00Z",
    lastAccessedAt: "2024-06-05T21:00:00Z",
    completed: false,
  },
  {
    courseId: "course-6",
    userId: "user-001",
    progress: 95,
    enrolledAt: "2024-02-18T08:00:00Z",
    lastAccessedAt: "2024-06-07T11:30:00Z",
    completed: false,
  },
];

// =====================
// Enrolled Courses (combine courses + progress)
// =====================
export const mockEnrolledCourses: EnrolledCourseResponse[] = mockProgresses.map(progress => ({
  course: mockCourses.find(course => course.id === progress.courseId)!,
  progress,
}));

// =====================
// Example user with enrolled courses
// =====================
export const mockUser = {
  id: "user-001",
  name: "John Example",
  email: "john@example.com",
  enrolledCourses: mockEnrolledCourses,
};
