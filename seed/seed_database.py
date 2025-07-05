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


def make_request(url: str, method="GET", data: Optional[Dict] = None,
                 headers: Optional[Dict] = None) -> Tuple[bool, str, Optional[Dict]]:
    headers = headers or {}
    data_bytes = json.dumps(data).encode() if data else None
    if data:
        headers['Content-Type'] = 'application/json'
    req = urllib.request.Request(url, data=data_bytes, headers=headers, method=method)
    try:
        with urllib.request.urlopen(req, timeout=10) as res:
            txt = res.read().decode()
            try: return True, txt, json.loads(txt)
            except: return True, txt, None
    except urllib.error.HTTPError as e:
        return False, e.read().decode(), None
    except Exception as e:
        return False, str(e), None


def check_health(name: str, url: str) -> bool:
    print_status(f"Checking {name}...", "HEALTH")
    ok, _, _ = make_request(url)
    print_status(f"{name} is {'running' if ok else 'not running'}", "SUCCESS" if ok else "ERROR")
    return ok


def check_services() -> bool:
    print_status("Checking service health...", "HEALTH")
    services = [
        ("Gateway", f"{GATEWAY_URL}/actuator/health"),
        ("User Service", f"{USERS_ENDPOINT}/health"),
        ("Course Service", f"{COURSES_ENDPOINT}/health")
    ]
    return all(check_health(name, url) for name, url in services)


def register_user() -> bool:
    print_status(f"Registering user: {USERNAME}", "USER")
    register_data = {
        "username": USERNAME,
        "email": f"{USERNAME}@example.com",
        "password": PASSWORD,
        "firstName": "Max",
        "lastName": "Tester"
    }
    ok, res, parsed = make_request(f"{USERS_ENDPOINT}/register", "POST", register_data)
    if ok and (parsed or "already exists" in res.lower()):
        print_status("User registered or already exists", "SUCCESS")
        return True
    print_status(f"Registration failed: {res}", "ERROR")
    return False



def login_user() -> bool:
    global JWT_TOKEN
    print_status(f"Logging in: {USERNAME}", "LOGIN")
    data = {"username": USERNAME, "password": PASSWORD}
    ok, res, parsed = make_request(f"{USERS_ENDPOINT}/login", "POST", data)
    if ok and parsed:
        JWT_TOKEN = parsed.get("jwtToken") or parsed.get("token", "")
        if JWT_TOKEN:
            print_status("Login successful, JWT obtained", "SUCCESS")
            return True
    print_status(f"Login failed: {res}", "ERROR")
    return False


def get_auth_headers():
    return {"Authorization": f"Bearer {JWT_TOKEN}"}

