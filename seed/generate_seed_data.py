#!/usr/bin/env python3
"""
Seed Data Generator for SkillForge Course Service

This script generates realistic, detailed seed data for the Course service database,
including courses, modules, lessons, and categories. The lessons contain authentic
content that would be found in real learning platforms.
"""

import json
import os
import uuid
import random
import string
from datetime import datetime
from typing import List, Dict, Any

# Constants
COURSE_SERVICE_PORT = 8081
BASE_URL = f"http://localhost:{COURSE_SERVICE_PORT}/api/v1"

class SeedDataGenerator:
    def __init__(self):
        self.categories = []
        self.courses = []
        self.output_dir = os.path.join(os.path.dirname(os.path.abspath(__file__)), "seed_courses")
        os.makedirs(self.output_dir, exist_ok=True)

    def generate_categories(self) -> List[Dict[str, Any]]:
        """Generate realistic course categories"""
        categories_data = [
            {"name": "Programming & Development", "description": "Learn programming languages, frameworks, and software development practices"},
            {"name": "Data Science & Analytics", "description": "Master data analysis, machine learning, and statistical modeling"},
            {"name": "Web Development", "description": "Build modern web applications with frontend and backend technologies"},
            {"name": "Mobile Development", "description": "Create mobile applications for iOS and Android platforms"},
            {"name": "DevOps & Cloud", "description": "Learn deployment, infrastructure, and cloud computing"},
            {"name": "Cybersecurity", "description": "Understand security principles, ethical hacking, and defense strategies"},
            {"name": "Design & UX", "description": "Master user experience design, UI/UX principles, and design tools"},
            {"name": "Business & Marketing", "description": "Learn business strategies, digital marketing, and entrepreneurship"},
            {"name": "Artificial Intelligence", "description": "Explore AI, machine learning, and neural networks"},
            {"name": "Blockchain & Cryptocurrency", "description": "Understand blockchain technology, smart contracts, and crypto"}
        ]
        self.categories = categories_data
        return categories_data

        
    def generate_lesson_content(self, lesson_type: str, topic: str) -> Dict[str, Any]:
        """Generate realistic lesson content based on type and topic"""
        
        if lesson_type == "TEXT":
            content = self.generate_text_content(topic)
        elif lesson_type == "HTML":
            content = self.generate_html_content(topic)
        elif lesson_type == "VIDEO":
            content = f"https://www.youtube.com/watch?v={self.generate_video_id()}"
        elif lesson_type == "IMAGE":
            content = f"https://images.unsplash.com/photo-{self.generate_image_id()}"
        elif lesson_type == "URL":
            content = f"https://docs.example.com/{topic.lower().replace(' ', '-')}"
        else:
            content = self.generate_text_content(topic)
        
        return {
            "type": lesson_type,
            "content": content
        }
    
    def generate_text_content(self, topic: str) -> str:
        """Generate realistic text content for lessons"""
        content_templates = {
            "Introduction": f"""
# Introduction to {topic}

Welcome to this comprehensive lesson on {topic}! In this session, we'll explore the fundamental concepts and practical applications that will help you master this essential skill.

## What You'll Learn

- Core principles and concepts
- Real-world applications and use cases
- Best practices and common pitfalls
- Hands-on exercises and examples

## Prerequisites

Before diving into this lesson, make sure you have:
- Basic understanding of related concepts
- Development environment set up
- Required tools and software installed

## Learning Objectives

By the end of this lesson, you will be able to:
1. Understand the key concepts of {topic}
2. Apply these concepts in practical scenarios
3. Identify common challenges and solutions
4. Build a foundation for advanced topics

Let's get started on your journey to mastering {topic}!
            """,
            
            "Fundamentals": f"""
# {topic} Fundamentals

In this lesson, we'll dive deep into the fundamental concepts of {topic}. Understanding these basics is crucial for building a strong foundation.

## Key Concepts

### 1. Core Principles
The fundamental principles of {topic} include:
- **Principle 1**: Essential concept explanation
- **Principle 2**: Important foundational idea
- **Principle 3**: Core methodology

### 2. Basic Terminology
Familiarize yourself with these key terms:
- **Term A**: Definition and explanation
- **Term B**: How it relates to {topic}
- **Term C**: Practical significance

### 3. Common Patterns
Recognize these common patterns in {topic}:
- Pattern 1: When and how to use it
- Pattern 2: Benefits and trade-offs
- Pattern 3: Real-world examples

## Practical Examples

Let's look at some practical examples:

**Example 1: Basic Implementation**
```python
# Sample code demonstrating {topic}
def basic_example():
    # Implementation details
    pass
```

**Example 2: Common Use Case**
```python
# Real-world scenario
def practical_application():
    # Practical implementation
    pass
```

## Exercises

**Exercise 1**: Try implementing a basic {topic} example
**Exercise 2**: Identify {topic} patterns in existing code
**Exercise 3**: Solve a simple problem using {topic} concepts

## Summary

In this lesson, we covered:
- Fundamental concepts of {topic}
- Key terminology and definitions
- Common patterns and practices
- Practical examples and exercises

Next, we'll explore more advanced topics and real-world applications.
            """,
            
            "Advanced": f"""
# Advanced {topic} Techniques

Welcome to the advanced level of {topic}! In this lesson, we'll explore sophisticated techniques and advanced concepts that will take your skills to the next level.

## Advanced Concepts

### 1. Complex Patterns
Advanced {topic} patterns include:
- **Advanced Pattern A**: Complex implementation details
- **Advanced Pattern B**: Performance optimization techniques
- **Advanced Pattern C**: Scalability considerations

### 2. Best Practices
Industry best practices for {topic}:
- **Performance**: Optimization strategies
- **Security**: Security considerations and implementations
- **Maintainability**: Code organization and structure
- **Testing**: Comprehensive testing approaches

### 3. Real-World Applications
Advanced applications of {topic} in production:

**Enterprise Implementation**
```python
# Enterprise-grade {topic} implementation
class Enterprise{topic.replace(' ', '')}:
    def __init__(self):
        self.config = self.load_configuration()
        self.optimizations = self.apply_optimizations()
    
    def advanced_operation(self):
        # Complex implementation
        pass
```

**Scalable Architecture**
```python
# Scalable {topic} architecture
class Scalable{topic.replace(' ', '')}:
    def __init__(self):
        self.distributed_system = self.setup_distribution()
    
    def handle_scale(self):
        # Scale handling implementation
        pass
```

## Performance Optimization

### 1. Memory Management
- Efficient memory usage patterns
- Garbage collection optimization
- Memory leak prevention

### 2. Algorithm Optimization
- Time complexity analysis
- Space complexity optimization
- Algorithm selection strategies

### 3. Caching Strategies
- Cache implementation patterns
- Cache invalidation strategies
- Distributed caching approaches

## Advanced Exercises

**Exercise 1**: Implement a high-performance {topic} solution
**Exercise 2**: Design a scalable {topic} architecture
**Exercise 3**: Optimize an existing {topic} implementation

## Troubleshooting

Common advanced issues and solutions:
- **Issue 1**: Problem description and solution
- **Issue 2**: Performance bottleneck resolution
- **Issue 3**: Scalability challenge solutions

## Next Steps

After mastering these advanced concepts:
1. Explore specialized {topic} domains
2. Contribute to open-source projects
3. Mentor others in {topic} development
4. Stay updated with latest trends and innovations
            """,
            
            "Practical": f"""
# Practical {topic} Applications

In this hands-on lesson, we'll apply {topic} concepts to real-world scenarios. You'll build practical solutions and gain valuable experience.

## Project Overview

We'll be building a practical application that demonstrates {topic} in action. This project will showcase:
- Real-world problem solving
- Industry-standard practices
- Practical implementation techniques

## Step-by-Step Implementation

### Step 1: Project Setup
```bash
# Initialize project structure
mkdir {topic.lower().replace(' ', '-')}-project
cd {topic.lower().replace(' ', '-')}-project
# Setup development environment
```

### Step 2: Core Implementation
```python
# Main {topic} implementation
class {topic.replace(' ', '')}Application:
    def __init__(self):
        self.config = self.load_config()
        self.data = self.initialize_data()
    
    def process_data(self):
        # Core processing logic
        return self.apply_{topic.lower().replace(' ', '_')}_logic()
    
    def generate_output(self):
        # Output generation
        return self.format_results()
```

### Step 3: Testing and Validation
```python
# Comprehensive testing
def test_{topic.lower().replace(' ', '_')}_functionality():
    # Test cases
    assert application.process_data() is not None
    assert application.generate_output() is not None
```

## Real-World Scenarios

### Scenario 1: Data Processing
```python
# Real-world data processing example
def process_real_data():
    # Load real dataset
    data = load_dataset()
    
    # Apply {topic} techniques
    processed_data = apply_{topic.lower().replace(' ', '_')}(data)
    
    # Generate insights
    insights = analyze_results(processed_data)
    
    return insights
```

### Scenario 2: API Integration
```python
# API integration example
def integrate_with_api():
    # API client setup
    client = APIClient()
    
    # {topic} processing
    result = client.process_with_{topic.lower().replace(' ', '_')}()
    
    # Response handling
    return handle_response(result)
```

## Hands-On Exercises

**Exercise 1**: Build a simple {topic} application
- Requirements: Basic functionality
- Deliverable: Working prototype
- Time: 30 minutes

**Exercise 2**: Optimize the application
- Requirements: Performance improvements
- Deliverable: Optimized version
- Time: 45 minutes

**Exercise 3**: Add advanced features
- Requirements: Advanced functionality
- Deliverable: Feature-complete application
- Time: 60 minutes

## Common Challenges and Solutions

### Challenge 1: Performance Issues
**Problem**: Application is slow
**Solution**: Implement caching and optimization
**Code Example**:
```python
# Performance optimization
def optimized_processing():
    cache = Cache()
    if cache.exists(key):
        return cache.get(key)
    result = expensive_operation()
    cache.set(key, result)
    return result
```

### Challenge 2: Error Handling
**Problem**: Unhandled exceptions
**Solution**: Comprehensive error handling
**Code Example**:
```python
# Error handling
def robust_processing():
    try:
        result = process_data()
        return result
    except SpecificError as e:
        logger.error(f"Specific error: {{e}}")
        return fallback_result()
    except Exception as e:
        logger.error(f"Unexpected error: {{e}}")
        raise
```

## Best Practices Summary

1. **Code Organization**: Maintain clean, readable code
2. **Error Handling**: Implement comprehensive error handling
3. **Testing**: Write thorough tests for all functionality
4. **Documentation**: Document your code and APIs
5. **Performance**: Consider performance implications
6. **Security**: Implement security best practices

## Project Submission

Submit your completed project including:
- Source code with comments
- README with setup instructions
- Test cases and results
- Performance analysis
- Lessons learned and improvements

## Next Steps

After completing this practical lesson:
1. Apply these concepts to your own projects
2. Explore more advanced {topic} applications
3. Contribute to open-source {topic} projects
4. Share your knowledge with the community
            """
        }
        
        # Select appropriate template based on topic
        if "introduction" in topic.lower() or "overview" in topic.lower():
            return content_templates["Introduction"]
        elif "fundamental" in topic.lower() or "basic" in topic.lower():
            return content_templates["Fundamentals"]
        elif "advanced" in topic.lower() or "expert" in topic.lower():
            return content_templates["Advanced"]
        else:
            return content_templates["Practical"]
    
    def generate_html_content(self, topic: str) -> str:
        """Generate HTML content for lessons"""
        return f"""
<div class="lesson-content">
    <h1>{topic}</h1>
    <div class="lesson-intro">
        <p>Welcome to this comprehensive lesson on {topic}!</p>
        <div class="learning-objectives">
            <h3>Learning Objectives:</h3>
            <ul>
                <li>Understand core concepts of {topic}</li>
                <li>Apply practical techniques</li>
                <li>Build real-world applications</li>
            </ul>
        </div>
    </div>
    
    <div class="main-content">
        <h2>Core Concepts</h2>
        <p>In this lesson, we'll explore the fundamental principles of {topic} and how they apply to real-world scenarios.</p>
        
        <div class="code-example">
            <h3>Example Implementation</h3>
            <pre><code>
// Sample {topic} implementation
function {topic.lower().replace(' ', '_')}Example() {{
    // Implementation details
    return result;
}}
            </code></pre>
        </div>
        
        <div class="practical-exercise">
            <h3>Hands-On Exercise</h3>
            <p>Try implementing a basic {topic} solution:</p>
            <ol>
                <li>Set up your development environment</li>
                <li>Create a simple {topic} application</li>
                <li>Test your implementation</li>
                <li>Optimize for performance</li>
            </ol>
        </div>
    </div>
    
    <div class="summary">
        <h2>Summary</h2>
        <p>In this lesson, we covered the essential concepts of {topic} and practical applications.</p>
        <p>Next, we'll explore more advanced topics and real-world use cases.</p>
    </div>
</div>
        """


    def generate_video_id(self) -> str:
        """Generate a realistic YouTube video ID"""
        return ''.join(random.choices(string.ascii_letters + string.digits, k=11))

    def generate_image_id(self) -> str:
        """Generate a realistic Unsplash image ID"""
        return ''.join(random.choices(string.digits, k=10)) + '-' + ''.join(random.choices(string.ascii_lowercase + string.digits, k=6))

    def generate_course(self, course_data: Dict[str, Any]) -> Dict[str, Any]:
        """Generate a complete course with modules and lessons"""
        
        # Generate modules
        modules = []
        for i, module_data in enumerate(course_data["modules"]):
            # module_id = str(uuid.uuid4())  # No longer needed
            lessons = []
            
            # Generate lessons for this module
            for j, lesson_data in enumerate(module_data["lessons"]):
                lesson = {
                    "id": str(uuid.uuid4()),  # Keep lesson id unless told otherwise
                    "title": lesson_data["title"],
                    "description": lesson_data["description"],
                    "content": self.generate_lesson_content(
                        lesson_data["content_type"], 
                        lesson_data["title"]
                    ),
                    "thumbnail": f"https://images.unsplash.com/photo-{self.generate_image_id()}",
                    "order": j
                }
                lessons.append(lesson)
            
            module = {
                # "id": module_id,  # Remove
                # "courseId": course_id,  # Remove
                "title": module_data["title"],
                "description": module_data["description"],
                "lessons": lessons,
                "order": i
            }
            modules.append(module)
        
        # Create course request
        course_request = {
            # "id": course_id,  # Remove
            "title": course_data["title"],
            "description": course_data["description"],
            "instructor": course_data["instructor"],
            "skills": course_data["skills"],
            "modules": modules,
            "enrolledUserIds": [],
            "numberOfEnrolledUsers": 0,
            "categories": course_data["categories"],
            "level": course_data["level"],
            "thumbnailUrl": course_data.get("thumbnailUrl", f"https://images.unsplash.com/photo-{self.generate_image_id()}"),
            "published": course_data["published"],
            "isPublic": course_data["isPublic"],
            "language": course_data["language"],
            "rating": course_data["rating"]
        }
        
        return course_request
    
    def generate_all_courses(self) -> List[Dict[str, Any]]:
        """Generate all courses with realistic content"""
        
        courses_data = [
            {
                "title": "Complete Python Programming Masterclass",
                "description": "Master Python programming from basics to advanced concepts. Learn web development, data analysis, automation, and more with hands-on projects.",
                "instructor": "Dr. Sarah Chen",
                "skills": ["Python", "Programming", "Web Development", "Data Analysis", "Automation"],
                "modules": [
                    {
                        "title": "Python Fundamentals",
                        "description": "Learn the basics of Python programming language",
                        "lessons": [
                            {"title": "Introduction to Python", "description": "Overview of Python and its applications", "content_type": "TEXT"},
                            {"title": "Variables and Data Types", "description": "Understanding Python variables and basic data types", "content_type": "HTML"},
                            {"title": "Control Flow", "description": "Learn about loops, conditionals, and program flow", "content_type": "TEXT"},
                            {"title": "Functions and Modules", "description": "Creating reusable code with functions", "content_type": "VIDEO"}
                        ]
                    },
                    {
                        "title": "Object-Oriented Programming",
                        "description": "Master OOP concepts in Python",
                        "lessons": [
                            {"title": "Classes and Objects", "description": "Understanding classes and object creation", "content_type": "TEXT"},
                            {"title": "Inheritance and Polymorphism", "description": "Advanced OOP concepts", "content_type": "HTML"},
                            {"title": "Encapsulation and Abstraction", "description": "Data hiding and abstraction principles", "content_type": "VIDEO"}
                        ]
                    },
                    {
                        "title": "Web Development with Flask",
                        "description": "Build web applications using Flask framework",
                        "lessons": [
                            {"title": "Flask Basics", "description": "Introduction to Flask web framework", "content_type": "TEXT"},
                            {"title": "Routing and Templates", "description": "Creating routes and HTML templates", "content_type": "HTML"},
                            {"title": "Database Integration", "description": "Connecting Flask with databases", "content_type": "VIDEO"},
                            {"title": "Building a Complete Web App", "description": "Create a full-featured web application", "content_type": "TEXT"}
                        ]
                    }
                ],
                "categories": ["Programming & Development"],
                "level": "INTERMEDIATE",
                "thumbnailUrl": "https://images.unsplash.com/photo-1526379095098-d400fd0bf935?w=800&h=600&fit=crop",
                "published": True,
                "isPublic": True,
                "language": "EN",
                "rating": 4.8
            },
            {
                "title": "Data Science Fundamentals with Python",
                "description": "Learn data science from scratch. Master pandas, numpy, matplotlib, and scikit-learn for data analysis and machine learning.",
                "instructor": "Prof. Michael Rodriguez",
                "skills": ["Data Science", "Python", "Pandas", "NumPy", "Machine Learning", "Data Visualization"],
                "modules": [
                    {
                        "title": "Data Analysis Basics",
                        "description": "Introduction to data analysis concepts and tools",
                        "lessons": [
                            {"title": "Introduction to Data Science", "description": "Overview of data science field and applications", "content_type": "TEXT"},
                            {"title": "Working with Pandas", "description": "Data manipulation and analysis with pandas", "content_type": "HTML"},
                            {"title": "Data Visualization", "description": "Creating charts and graphs with matplotlib", "content_type": "VIDEO"},
                            {"title": "Statistical Analysis", "description": "Basic statistical concepts and calculations", "content_type": "TEXT"}
                        ]
                    },
                    {
                        "title": "Machine Learning Introduction",
                        "description": "Basic machine learning concepts and algorithms",
                        "lessons": [
                            {"title": "ML Fundamentals", "description": "Understanding machine learning basics", "content_type": "TEXT"},
                            {"title": "Supervised Learning", "description": "Classification and regression algorithms", "content_type": "HTML"},
                            {"title": "Model Evaluation", "description": "Assessing model performance and accuracy", "content_type": "VIDEO"}
                        ]
                    }
                ],
                "categories": ["Data Science & Analytics"],
                "level": "INTERMEDIATE",
                "thumbnailUrl": "https://images.unsplash.com/photo-1551288049-bebda4e38f71?w=800&h=600&fit=crop",
                "published": True,
                "isPublic": True,
                "language": "EN",
                "rating": 4.9
            },
            {
                "title": "Modern Web Development with React",
                "description": "Build modern, responsive web applications using React. Learn hooks, context, routing, and state management.",
                "instructor": "Alex Johnson",
                "skills": ["React", "JavaScript", "Web Development", "Frontend", "UI/UX"],
                "modules": [
                    {
                        "title": "React Fundamentals",
                        "description": "Core React concepts and component-based architecture",
                        "lessons": [
                            {"title": "Introduction to React", "description": "Understanding React and its ecosystem", "content_type": "TEXT"},
                            {"title": "Components and Props", "description": "Building reusable React components", "content_type": "HTML"},
                            {"title": "State and Lifecycle", "description": "Managing component state and lifecycle", "content_type": "VIDEO"},
                            {"title": "Hooks and Functional Components", "description": "Modern React with hooks", "content_type": "TEXT"}
                        ]
                    },
                    {
                        "title": "Advanced React Patterns",
                        "description": "Advanced patterns and best practices",
                        "lessons": [
                            {"title": "Context API", "description": "Global state management with Context", "content_type": "TEXT"},
                            {"title": "Custom Hooks", "description": "Creating reusable custom hooks", "content_type": "HTML"},
                            {"title": "Performance Optimization", "description": "Optimizing React application performance", "content_type": "VIDEO"}
                        ]
                    }
                ],
                "categories": ["Web Development"],
                "level": "INTERMEDIATE",
                "thumbnailUrl": "https://images.unsplash.com/photo-1633356122544-f134324a6cee?w=800&h=600&fit=crop",
                "published": True,
                "isPublic": True,
                "language": "EN",
                "rating": 4.7
            },
            {
                "title": "DevOps and CI/CD Pipeline",
                "description": "Master DevOps practices, Docker, Kubernetes, and continuous integration/deployment pipelines.",
                "instructor": "Emma Wilson",
                "skills": ["DevOps", "Docker", "Kubernetes", "CI/CD", "Cloud Computing", "Infrastructure"],
                "modules": [
                    {
                        "title": "DevOps Fundamentals",
                        "description": "Core DevOps principles and practices",
                        "lessons": [
                            {"title": "Introduction to DevOps", "description": "Understanding DevOps culture and practices", "content_type": "TEXT"},
                            {"title": "Version Control with Git", "description": "Advanced Git workflows and collaboration", "content_type": "HTML"},
                            {"title": "Containerization with Docker", "description": "Building and managing Docker containers", "content_type": "VIDEO"},
                            {"title": "Container Orchestration", "description": "Managing containers with Kubernetes", "content_type": "TEXT"}
                        ]
                    },
                    {
                        "title": "CI/CD Implementation",
                        "description": "Building automated deployment pipelines",
                        "lessons": [
                            {"title": "Continuous Integration", "description": "Setting up automated testing and building", "content_type": "TEXT"},
                            {"title": "Continuous Deployment", "description": "Automated deployment strategies", "content_type": "HTML"},
                            {"title": "Monitoring and Logging", "description": "Application monitoring and log management", "content_type": "VIDEO"}
                        ]
                    }
                ],
                "categories": ["DevOps & Cloud"],
                "level": "ADVANCED",
                "thumbnailUrl": "https://images.unsplash.com/photo-1667372393119-3d4c48d07fc9?w=800&h=600&fit=crop",
                "published": True,
                "isPublic": True,
                "language": "EN",
                "rating": 4.6
            },
            {
                "title": "Cybersecurity Fundamentals",
                "description": "Learn essential cybersecurity concepts, ethical hacking, and defense strategies to protect systems and data.",
                "instructor": "David Kim",
                "skills": ["Cybersecurity", "Ethical Hacking", "Network Security", "Penetration Testing", "Security Analysis"],
                "modules": [
                    {
                        "title": "Security Fundamentals",
                        "description": "Basic cybersecurity concepts and principles",
                        "lessons": [
                            {"title": "Introduction to Cybersecurity", "description": "Overview of security threats and defenses", "content_type": "TEXT"},
                            {"title": "Network Security Basics", "description": "Protecting network infrastructure", "content_type": "HTML"},
                            {"title": "Cryptography Fundamentals", "description": "Understanding encryption and security protocols", "content_type": "VIDEO"},
                            {"title": "Security Best Practices", "description": "Implementing security measures", "content_type": "TEXT"}
                        ]
                    },
                    {
                        "title": "Ethical Hacking",
                        "description": "Penetration testing and vulnerability assessment",
                        "lessons": [
                            {"title": "Penetration Testing Methodology", "description": "Systematic approach to security testing", "content_type": "TEXT"},
                            {"title": "Vulnerability Assessment", "description": "Identifying and analyzing security weaknesses", "content_type": "HTML"},
                            {"title": "Exploitation Techniques", "description": "Understanding attack vectors and methods", "content_type": "VIDEO"}
                        ]
                    }
                ],
                "categories": ["Cybersecurity"],
                "level": "INTERMEDIATE",
                "thumbnailUrl": "https://images.unsplash.com/photo-1563013544-824ae1b704d3?w=800&h=600&fit=crop",
                "published": True,
                "isPublic": True,
                "language": "EN",
                "rating": 4.5
            },
            {
                "title": "JavaScript Full Stack Development",
                "description": "Master JavaScript for both frontend and backend development. Learn Node.js, Express, and modern JavaScript frameworks.",
                "instructor": "Maria Garcia",
                "skills": ["JavaScript", "Node.js", "Express", "MongoDB", "Full Stack", "Web Development"],
                "modules": [
                    {
                        "title": "JavaScript Fundamentals",
                        "description": "Core JavaScript concepts and modern ES6+ features",
                        "lessons": [
                            {"title": "JavaScript Basics", "description": "Variables, functions, and control structures", "content_type": "TEXT"},
                            {"title": "ES6+ Features", "description": "Arrow functions, destructuring, and modules", "content_type": "HTML"},
                            {"title": "Asynchronous JavaScript", "description": "Promises, async/await, and callbacks", "content_type": "VIDEO"},
                            {"title": "DOM Manipulation", "description": "Working with the browser DOM", "content_type": "TEXT"}
                        ]
                    },
                    {
                        "title": "Node.js Backend Development",
                        "description": "Building server-side applications with Node.js",
                        "lessons": [
                            {"title": "Node.js Introduction", "description": "Server-side JavaScript runtime", "content_type": "TEXT"},
                            {"title": "Express.js Framework", "description": "Building RESTful APIs with Express", "content_type": "HTML"},
                            {"title": "Database Integration", "description": "Connecting to MongoDB and other databases", "content_type": "VIDEO"},
                            {"title": "Authentication & Authorization", "description": "Implementing user authentication", "content_type": "TEXT"}
                        ]
                    }
                ],
                "categories": ["Web Development", "Programming & Development"],
                "level": "INTERMEDIATE",
                "thumbnailUrl": "https://images.unsplash.com/photo-1627398242454-45a1465c2479?w=800&h=600&fit=crop",
                "published": True,
                "isPublic": True,
                "language": "EN",
                "rating": 4.7
            },
            {
                "title": "Machine Learning with Python",
                "description": "Deep dive into machine learning algorithms, neural networks, and AI applications using Python and popular ML libraries.",
                "instructor": "Dr. James Anderson",
                "skills": ["Machine Learning", "Python", "TensorFlow", "Scikit-learn", "Neural Networks", "AI"],
                "modules": [
                    {
                        "title": "Machine Learning Basics",
                        "description": "Foundational concepts and algorithms",
                        "lessons": [
                            {"title": "Introduction to ML", "description": "Overview of machine learning field", "content_type": "TEXT"},
                            {"title": "Supervised Learning", "description": "Classification and regression algorithms", "content_type": "HTML"},
                            {"title": "Unsupervised Learning", "description": "Clustering and dimensionality reduction", "content_type": "VIDEO"},
                            {"title": "Model Evaluation", "description": "Metrics and validation techniques", "content_type": "TEXT"}
                        ]
                    },
                    {
                        "title": "Deep Learning with Neural Networks",
                        "description": "Building and training neural networks",
                        "lessons": [
                            {"title": "Neural Network Basics", "description": "Understanding neural network architecture", "content_type": "TEXT"},
                            {"title": "TensorFlow & Keras", "description": "Building models with TensorFlow", "content_type": "HTML"},
                            {"title": "Convolutional Neural Networks", "description": "Image recognition and computer vision", "content_type": "VIDEO"},
                            {"title": "Natural Language Processing", "description": "Text processing and language models", "content_type": "TEXT"}
                        ]
                    }
                ],
                "categories": ["Artificial Intelligence", "Data Science & Analytics"],
                "level": "ADVANCED",
                "thumbnailUrl": "https://images.unsplash.com/photo-1677442136019-21780ecad995?w=800&h=600&fit=crop",
                "published": True,
                "isPublic": True,
                "language": "EN",
                "rating": 4.8
            },
            {
                "title": "Mobile App Development with React Native",
                "description": "Build cross-platform mobile applications using React Native. Learn to create apps for both iOS and Android.",
                "instructor": "Sophie Chen",
                "skills": ["React Native", "Mobile Development", "JavaScript", "iOS", "Android", "Cross-platform"],
                "modules": [
                    {
                        "title": "React Native Fundamentals",
                        "description": "Core concepts and setup",
                        "lessons": [
                            {"title": "Introduction to React Native", "description": "Cross-platform mobile development", "content_type": "TEXT"},
                            {"title": "Components and Navigation", "description": "Building mobile UI components", "content_type": "HTML"},
                            {"title": "State Management", "description": "Managing app state with Redux", "content_type": "VIDEO"},
                            {"title": "Platform APIs", "description": "Accessing device features", "content_type": "TEXT"}
                        ]
                    },
                    {
                        "title": "Advanced Mobile Features",
                        "description": "Advanced mobile development techniques",
                        "lessons": [
                            {"title": "Push Notifications", "description": "Implementing push notifications", "content_type": "TEXT"},
                            {"title": "Offline Support", "description": "Building offline-capable apps", "content_type": "HTML"},
                            {"title": "Performance Optimization", "description": "Optimizing app performance", "content_type": "VIDEO"},
                            {"title": "App Store Deployment", "description": "Publishing to app stores", "content_type": "TEXT"}
                        ]
                    }
                ],
                "categories": ["Mobile Development"],
                "level": "INTERMEDIATE",
                "thumbnailUrl": "https://images.unsplash.com/photo-1512941937669-90a1b58e7e9c?w=800&h=600&fit=crop",
                "published": True,
                "isPublic": True,
                "language": "EN",
                "rating": 4.6
            },
            {
                "title": "UI/UX Design Principles",
                "description": "Master user interface and user experience design. Learn design principles, tools, and best practices for creating engaging digital experiences.",
                "instructor": "Lisa Thompson",
                "skills": ["UI/UX Design", "Figma", "User Research", "Prototyping", "Design Systems", "User Testing"],
                "modules": [
                    {
                        "title": "Design Fundamentals",
                        "description": "Core design principles and concepts",
                        "lessons": [
                            {"title": "Introduction to UI/UX", "description": "Understanding design principles", "content_type": "TEXT"},
                            {"title": "User Research", "description": "Conducting user research and interviews", "content_type": "HTML"},
                            {"title": "Information Architecture", "description": "Organizing content and navigation", "content_type": "VIDEO"},
                            {"title": "Wireframing", "description": "Creating low-fidelity prototypes", "content_type": "TEXT"}
                        ]
                    },
                    {
                        "title": "Advanced Design Techniques",
                        "description": "Advanced design and prototyping",
                        "lessons": [
                            {"title": "High-Fidelity Design", "description": "Creating detailed UI designs", "content_type": "TEXT"},
                            {"title": "Prototyping with Figma", "description": "Building interactive prototypes", "content_type": "HTML"},
                            {"title": "Design Systems", "description": "Creating consistent design systems", "content_type": "VIDEO"},
                            {"title": "User Testing", "description": "Testing and validating designs", "content_type": "TEXT"}
                        ]
                    }
                ],
                "categories": ["Design & UX"],
                "level": "INTERMEDIATE",
                "thumbnailUrl": "https://images.unsplash.com/photo-1561070791-2526d30994b5?w=800&h=600&fit=crop",
                "published": True,
                "isPublic": True,
                "language": "EN",
                "rating": 4.7
            },
            {
                "title": "Blockchain Development Fundamentals",
                "description": "Learn blockchain technology, smart contracts, and decentralized applications. Master Solidity and Ethereum development.",
                "instructor": "Robert Zhang",
                "skills": ["Blockchain", "Solidity", "Ethereum", "Smart Contracts", "Web3", "Cryptocurrency"],
                "modules": [
                    {
                        "title": "Blockchain Basics",
                        "description": "Understanding blockchain technology",
                        "lessons": [
                            {"title": "Introduction to Blockchain", "description": "Blockchain fundamentals and concepts", "content_type": "TEXT"},
                            {"title": "Cryptography in Blockchain", "description": "Cryptographic principles", "content_type": "HTML"},
                            {"title": "Consensus Mechanisms", "description": "Proof of Work vs Proof of Stake", "content_type": "VIDEO"},
                            {"title": "Ethereum Platform", "description": "Understanding Ethereum blockchain", "content_type": "TEXT"}
                        ]
                    },
                    {
                        "title": "Smart Contract Development",
                        "description": "Building smart contracts with Solidity",
                        "lessons": [
                            {"title": "Solidity Programming", "description": "Smart contract programming language", "content_type": "TEXT"},
                            {"title": "Smart Contract Security", "description": "Security best practices", "content_type": "HTML"},
                            {"title": "DApp Development", "description": "Building decentralized applications", "content_type": "VIDEO"},
                            {"title": "DeFi Protocols", "description": "Understanding DeFi applications", "content_type": "TEXT"}
                        ]
                    }
                ],
                "categories": ["Blockchain & Cryptocurrency"],
                "level": "ADVANCED",
                "thumbnailUrl": "https://images.unsplash.com/photo-1639762681485-074b7f938ba0?w=800&h=600&fit=crop",
                "published": True,
                "isPublic": True,
                "language": "EN",
                "rating": 4.5
            },
            {
                "title": "Digital Marketing and Growth Hacking",
                "description": "Master digital marketing strategies, SEO, social media marketing, and growth hacking techniques for business success.",
                "instructor": "Amanda Foster",
                "skills": ["Digital Marketing", "SEO", "Social Media Marketing", "Growth Hacking", "Analytics", "Content Marketing"],
                "modules": [
                    {
                        "title": "Digital Marketing Fundamentals",
                        "description": "Core marketing concepts and strategies",
                        "lessons": [
                            {"title": "Marketing Fundamentals", "description": "Understanding marketing principles", "content_type": "TEXT"},
                            {"title": "SEO Optimization", "description": "Search engine optimization techniques", "content_type": "HTML"},
                            {"title": "Social Media Marketing", "description": "Marketing on social platforms", "content_type": "VIDEO"},
                            {"title": "Content Marketing", "description": "Creating engaging content strategies", "content_type": "TEXT"}
                        ]
                    },
                    {
                        "title": "Growth Hacking and Analytics",
                        "description": "Data-driven growth strategies",
                        "lessons": [
                            {"title": "Growth Hacking Basics", "description": "Rapid growth strategies", "content_type": "TEXT"},
                            {"title": "Marketing Analytics", "description": "Measuring and analyzing performance", "content_type": "HTML"},
                            {"title": "Conversion Optimization", "description": "Improving conversion rates", "content_type": "VIDEO"},
                            {"title": "Marketing Automation", "description": "Automating marketing processes", "content_type": "TEXT"}
                        ]
                    }
                ],
                "categories": ["Business & Marketing"],
                "level": "INTERMEDIATE",
                "thumbnailUrl": "https://images.unsplash.com/photo-1460925895917-afdab827c52f?w=800&h=600&fit=crop",
                "published": True,
                "isPublic": True,
                "language": "EN",
                "rating": 4.4
            }
        ]
        
        courses = []
        for course_data in courses_data:
            course = self.generate_course(course_data)
            courses.append(course)
        
        self.courses = courses
        return courses
    
