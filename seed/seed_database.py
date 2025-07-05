"""
Seed Data Script for SkillForge Course Service via Gateway
- Uses only native libraries
- Routes all traffic through API Gateway
"""

import json
import os
import sys
import urllib.request
import urllib.error
from typing import Dict, List, Optional, Tuple

# Config
GATEWAY_URL = "http://localhost:8081"
API_VERSION = "api/v1"
API_URL = f"{GATEWAY_URL}/{API_VERSION}"

USERNAME = "max123"
PASSWORD = "password"
JWT_TOKEN = ""

# Endpoints
USERS_ENDPOINT = f"{API_URL}/users"
COURSES_ENDPOINT = f"{API_URL}/courses"


def print_status(msg: str, status: str = "INFO"):
    emojis = {
        "INFO": "ℹ️", "SUCCESS": "✅", "ERROR": "❌", "WARNING": "⚠️",
        "SKIP": "⏭️", "START": "🚀", "HEALTH": "🔍", "USER": "👤",
        "LOGIN": "🔐", "COURSE": "📚", "CATEGORY": "📂", "COMPLETE": "🎉"
    }
    print(f"{emojis.get(status, 'ℹ️')} {msg}")

