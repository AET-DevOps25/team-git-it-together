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

    