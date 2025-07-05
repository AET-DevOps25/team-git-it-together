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
    
