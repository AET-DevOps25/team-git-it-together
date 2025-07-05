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
