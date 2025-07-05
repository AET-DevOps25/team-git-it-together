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
    
